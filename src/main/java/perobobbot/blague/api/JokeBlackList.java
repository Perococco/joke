package perobobbot.blague.api;

import lombok.NonNull;

public interface JokeBlackList {

    boolean isBlackListed(int jokeId);

    default boolean isBlackListed(@NonNull Joke joke) {
        return isBlackListed(joke.id());
    }
}
