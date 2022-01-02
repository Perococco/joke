package perobobbot.blague.api;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import perobobbot.lang.fp.Consumer1;

import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JokeRetrievalResult {

    public static @NonNull JokeRetrievalResult success(@NonNull Joke joke) {
        return new JokeRetrievalResult(joke,null);
    }

    public static @NonNull JokeRetrievalResult failure(@NonNull String errorMessage) {
        return new JokeRetrievalResult(null,errorMessage);
    }

    private final Joke joke;
    private final String errorMessage;

    public @NonNull Optional<Joke> getJoke() {
        return Optional.ofNullable(joke);
    }


    public boolean isError() {
        return errorMessage != null;
    }

    public boolean isSuccess() {
        return joke != null;
    }

    public @NonNull Optional<String> getErrorMessage() {
        return Optional.ofNullable(errorMessage);
    }

    public void handle(@NonNull Consumer1<Joke> jokeConsumer, @NonNull Consumer1<String> errorConsumer) {
        getJoke().ifPresent(jokeConsumer);
        getErrorMessage().ifPresent(errorConsumer);
    }
}
