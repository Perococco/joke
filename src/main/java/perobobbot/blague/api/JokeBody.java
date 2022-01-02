package perobobbot.blague.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**

 */

@RequiredArgsConstructor
@EqualsAndHashCode
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JokeBody {
    boolean error;
    @NonNull String category;
    @NonNull String type;
    String setup;
    String delivery;
    String joke;
    int id;


    public @NonNull Optional<Joke> asJoke() {
        return switch (type) {
            case "single" -> formSingleJoke();
            case "twopart" -> formTwoPartJoke();
            default -> Optional.empty();
        };
    }

    private @NonNull Optional<Joke> formSingleJoke() {
        return Optional.ofNullable(joke).map(j -> new SingleJoke(id, j));
    }

    private @NonNull Optional<Joke> formTwoPartJoke() {
        if (setup == null || delivery == null) {
            return Optional.empty();
        }
        return Optional.of(new TwoPartJoke(id, setup, delivery));
    }
}
