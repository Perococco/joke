package perobobbot.blague.api;

import lombok.NonNull;
import org.springframework.web.reactive.function.client.WebClient;
import perobobbot.lang.ImmutableEntry;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static perobobbot.lang.ImmutableEntry.of;

public class UnsafeJokeRetriever implements JokeRetriever {

    private static final Set<String> KNOWN_LANGUAGES = Set.of("cs", "de", "en", "es", "fr", "pt");

    public static final String URL_BASE = "https://v2.jokeapi.dev/joke/Any";
    public static final String DEFAULT_BLACKLIST_FLAGS = "nsfw,religious,political,racist,sexist,explicit";


    public @NonNull Mono<JokeRetrievalResult> retrieveRandomJoke(@NonNull String language) {

        return retrieveJoke(filterLanguage(language), "");
    }

    public @NonNull Mono<JokeRetrievalResult> retrieveJoke(@NonNull String language, int id) {
        return retrieveJoke(filterLanguage(language), "" + id);
    }


    private @NonNull String filterLanguage(@NonNull String language) {
        if (KNOWN_LANGUAGES.contains(language)) {
            return language;
        }
        return DEFAULT_LANGUAGE;
    }

    private @NonNull Mono<JokeRetrievalResult> retrieveJoke(@NonNull String language, @NonNull String idOption) {
        final var parameters = Stream.of(of("lang", language),
                                             of("idRange", idOption),
                                             of("blacklistFlags", DEFAULT_BLACKLIST_FLAGS)
                                     ).filter(e -> !e.getValue().isBlank())
                                     .map(e -> e.getKey() + "=" + e.getValue())
                                     .collect(Collectors.joining("&", "?", ""));

        return WebClient.create().get()
                        .uri(URL_BASE + parameters)
                        .retrieve()
                        .bodyToMono(JokeBody.class)
                        .map(JokeBody::asJoke)
                        .map(j -> j.map(JokeRetrievalResult::success).orElseGet(() -> JokeRetrievalResult.failure("Could not transform to a Joke")));
    }

}
