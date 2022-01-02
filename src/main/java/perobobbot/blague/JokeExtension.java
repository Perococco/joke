package perobobbot.blague;


import lombok.NonNull;
import lombok.Setter;
import lombok.Synchronized;
import lombok.experimental.ExtensionMethod;
import lombok.extern.log4j.Log4j2;
import perobobbot.blague.api.*;
import perobobbot.chat.core.IO;
import perobobbot.data.service.UserService;
import perobobbot.data.service.ViewerIdentityService;
import perobobbot.extension.ExtensionBase;
import perobobbot.lang.ChannelInfo;
import perobobbot.lang.ExecutionContext;
import perobobbot.lang.Looper;
import perobobbot.lang.ViewerIdentity;
import perobobbot.lang.fp.Value2;
import perobobbot.oauth.BroadcasterIdentifier;
import perobobbot.oauth.OAuthTokenIdentifierSetter;
import perobobbot.oauth.TokenIdentifier;
import perobobbot.twitch.client.api.TwitchService;
import perobobbot.twitch.client.api.channel.ChannelInformation;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;
import java.util.function.Supplier;

@Log4j2
public class JokeExtension extends ExtensionBase {

    private volatile boolean started = false;

    @Setter
    private Duration delayBeforeAnswer = Duration.ofSeconds(10);

    private final JokeRetriever jokeRetriever = new SafeJokeRetriever(new UnsafeJokeRetriever(), new HardCodedJokeBlackList());

    private final @NonNull IO io;
    private final @NonNull TwitchService twitchService;
    private final @NonNull OAuthTokenIdentifierSetter oAuthTokenIdentifierSetter;
    private final @NonNull ViewerIdentityService viewerIdentityService;

    private final SayLoop sayLoop = new SayLoop();

    public JokeExtension(@NonNull IO io,
                         @NonNull TwitchService twitchService,
                         @NonNull OAuthTokenIdentifierSetter oAuthTokenIdentifierSetter,
                         @NonNull ViewerIdentityService viewerIdentityService) {
        super("Blague");
        this.io = io;
        this.twitchService = twitchService;
        this.oAuthTokenIdentifierSetter = oAuthTokenIdentifierSetter;
        this.viewerIdentityService = viewerIdentityService;
    }

    @Override
    protected void onEnabled() {
        super.onEnabled();
        start();
    }

    @Override
    protected void onDisabled() {
        super.onDisabled();
        stop();
    }

    @Synchronized
    public void start() {
        if (started) {
            return;
        }
        sayLoop.start();
        started = true;
    }

    @Synchronized
    public void stop() {
        if (!started) {
            return;
        }
        started = false;
        sayLoop.requestStop();
    }

    public void sayRandomJoke(ExecutionContext context) {
        this.sayJoke(context, jokeRetriever::retrieveRandomJoke);
    }

    public void sayJoke(ExecutionContext context, int id) {
        this.sayJoke(context, language -> jokeRetriever.retrieveJoke(language, id));
    }


    @Synchronized
    private void sayJoke(@NonNull ExecutionContext context, @NonNull Function<String, Mono<JokeRetrievalResult>> retriever) {
        if (this.started) {
            getChannelLanguage(context.getChannelInfo())
                    .flatMap(retriever)
                    .subscribe(j -> sayLoop.addJoke(j, context));
        }
    }

    private Mono<String> getChannelLanguage(@NonNull ChannelInfo channelInfo) {
        var broadcasterId = viewerIdentityService.findIdentity(channelInfo.getPlatform(), channelInfo.getChannelName())
                                                 .map(ViewerIdentity::getViewerId)
                                                 .orElse(null);

        if (broadcasterId == null) {
            return Mono.just(JokeRetriever.DEFAULT_LANGUAGE);
        }

        var identifier = new BroadcasterIdentifier(channelInfo.getPlatform(), broadcasterId);
        return this.oAuthTokenIdentifierSetter
                .wrapCall(identifier, () -> twitchService.getChannelInformation(broadcasterId))
                .map(ChannelInformation::getBroadcasterLanguage);
    }

    private class SayLoop extends Looper {

        private final BlockingDeque<Value2<JokeRetrievalResult, ExecutionContext>> pendingJokes = new LinkedBlockingDeque<>();

        public void addJoke(@NonNull JokeRetrievalResult joke, @NonNull ExecutionContext context) {
            pendingJokes.offer(Value2.of(joke, context));
        }

        @Override
        protected @NonNull IterationCommand performOneIteration() throws Exception {
            final var jokeInfo = pendingJokes.take();

            final var result = jokeInfo.getFirst();
            final var context = jokeInfo.getSecond();

            result.handle(j -> sayJoke(context, j), err -> sayError(context, err));

            return IterationCommand.CONTINUE;
        }

        private void sayError(@NonNull ExecutionContext context, @NonNull String errorMessage) {
            LOG.info("Send error message to chat {}", errorMessage);
            send(context, errorMessage);
        }

        private void sayJoke(ExecutionContext context, @NonNull Joke joke) {
            LOG.info("Send joke to chat {}", joke);

            if (joke instanceof SingleJoke singleJoke) {
                send(context, "[%d] %s".formatted(joke.id(), singleJoke.joke()));
            } else if (joke instanceof TwoPartJoke twoPartJoke) {
                send(context, "[%d] %s".formatted(joke.id(), twoPartJoke.setup()));
                sleep(delayBeforeAnswer);
                send(context, twoPartJoke.delivery());
            }

        }

        private void send(@NonNull ExecutionContext context, @NonNull String message) {
            io.send(context.getChatConnectionInfo(), context.getChannelName(), message);
        }
    }

}
