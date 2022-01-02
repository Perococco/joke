package perobobbot.blague.api;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

@RequiredArgsConstructor
public class SafeJokeRetriever implements JokeRetriever {

    public static final int NB_TRY_BEFORE_FAILURE = 10;
    public static final String FAIL_TO_GET_SAFE_JOKE = "Cannot retrieve the joke";
    public static final String BLACKLISTED_MSG = "This joke is not allowed here";

    private final JokeRetriever unsafe;

    private final JokeBlackList blackList;

    @Override
    public @NonNull Mono<JokeRetrievalResult> retrieveRandomJoke(@NonNull String language) {
       return Mono.defer(() -> this.getSafeJoke(language));
    }


    @Override
    public @NonNull Mono<JokeRetrievalResult> retrieveJoke(@NonNull String language,int id) {
        if (blackList.isBlackListed(id)) {
            return Mono.just(JokeRetrievalResult.failure(BLACKLISTED_MSG));
        }
        return unsafe.retrieveJoke(language, id);
    }

    private @NonNull Mono<JokeRetrievalResult> getSafeJoke(@NonNull String language) {
        for (int i = 0; i < NB_TRY_BEFORE_FAILURE; i++) {
            final var joke = unsafe.retrieveRandomJoke(language).block();

            final var safeJoke = joke.getJoke().filter(Predicate.not(blackList::isBlackListed));
            if (safeJoke.isPresent()) {
                return Mono.just(joke);
            }
        }
        return Mono.just(JokeRetrievalResult.failure(FAIL_TO_GET_SAFE_JOKE));
    }


}
