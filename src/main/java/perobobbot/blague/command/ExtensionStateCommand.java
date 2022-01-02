package perobobbot.blague.command;


import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import perobobbot.blague.JokeExtension;
import perobobbot.chat.core.IO;
import perobobbot.command.CommandAction;
import perobobbot.command.CommandParsing;
import perobobbot.lang.ExecutionContext;

import java.time.Duration;

@RequiredArgsConstructor
public class ExtensionStateCommand implements CommandAction {

    private final @NonNull IO io;

    private final @NonNull JokeExtension extension;

    @Override
    public void execute(@NonNull CommandParsing parsing, @NonNull ExecutionContext context) {
        final var cmd = parsing.getParameter("cmd");
        final var parm = parsing.findIntParameter("parm");

        switch (cmd) {
            case "start" -> extension.start();
            case "stop" -> extension.stop();
            case "delay" -> {
                if (parm.isEmpty()) {
                    io.send(context.getChatConnectionInfo(), context.getChannelName(),"usage : !"+parsing.getFullName()+" delay <delay_in_second>");
                } else {
                    extension.setDelayBeforeAnswer(Duration.ofSeconds(parm.get()));
                }
            }
        }
    }
}
