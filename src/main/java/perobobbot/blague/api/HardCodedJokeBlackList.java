package perobobbot.blague.api;

import lombok.NonNull;

import java.util.Set;

public class HardCodedJokeBlackList implements JokeBlackList {

    private final Set<Integer> bannedJokeId = Set.of();

    public boolean isBlackListed(int jokeId) {
        return bannedJokeId.contains(jokeId);
    }

}
