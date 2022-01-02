package perobobbot.blague.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

public class TestJokeDeser {

    public static final String JOKE = """
            {
             "error": false,
             "category": "Misc",
             "type": "twopart",
             "setup": "Qu'est-ce qu'un éléphant dit à un homme tout nu?",
             "delivery": "Et tu arrives à attraper des cacahuètes avec ça?",
             "flags": {
             "nsfw": false,
             "religious": false,
             "political": false,
             "racist": false,
             "sexist": false,
             "explicit": false
             },
             "safe": true,
             "id": 69,
             "lang": "fr"
             }
            """;



    public static Stream<String> payloads() {
        return Stream.of(JOKE);
    }

    @ParameterizedTest
    @MethodSource("payloads")
    public void name(@NonNull String payload) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(payload,JokeBody.class);

    }
}
