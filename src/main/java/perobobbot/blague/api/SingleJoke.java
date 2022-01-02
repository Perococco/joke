package perobobbot.blague.api;

import lombok.NonNull;

public record SingleJoke(int id, @NonNull String joke) implements Joke {
}
