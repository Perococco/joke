package perobobbot.blague.command;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import perobobbot.blague.JokeExtension;
import perobobbot.command.CommandAction;
import perobobbot.command.CommandParsing;
import perobobbot.lang.ExecutionContext;

@RequiredArgsConstructor
public class SayJokeCommand implements CommandAction {

    private final @NonNull JokeExtension extension;

    @Override
    public void execute(@NonNull CommandParsing parsing, @NonNull ExecutionContext context) {
        final var id = parsing.findIntParameter("id");

        id.ifPresentOrElse(
                i -> extension.sayJoke(context,i),
                () -> extension.sayRandomJoke(context)
        );
    }
}
