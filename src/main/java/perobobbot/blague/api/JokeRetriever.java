package perobobbot.blague.api;

import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface JokeRetriever {

    String DEFAULT_LANGUAGE = "en";


    @NonNull Mono<JokeRetrievalResult> retrieveRandomJoke(@NonNull String language);

    @NonNull Mono<JokeRetrievalResult> retrieveJoke(@NonNull String language, int id);

}
