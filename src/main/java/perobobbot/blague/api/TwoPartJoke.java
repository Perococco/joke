package perobobbot.blague.api;

import lombok.NonNull;

public record TwoPartJoke(int id, @NonNull String setup, @NonNull String delivery) implements Joke {
}
