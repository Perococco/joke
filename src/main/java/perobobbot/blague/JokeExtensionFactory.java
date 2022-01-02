package perobobbot.blague;

import com.google.common.collect.ImmutableList;
import jplugman.api.ServiceProvider;
import lombok.NonNull;
import perobobbot.access.AccessRule;
import perobobbot.blague.command.ExtensionStateCommand;
import perobobbot.blague.command.SayJokeCommand;
import perobobbot.chat.core.IO;
import perobobbot.command.CommandDeclaration;
import perobobbot.extension.ExtensionFactory;
import perobobbot.lang.Role;

import java.time.Duration;

public class JokeExtensionFactory implements ExtensionFactory<JokeExtension> {

    @Override
    public @NonNull JokeExtension createExtension(@NonNull ModuleLayer pluginLayer, @NonNull ServiceProvider serviceProvider) {
        return new JokeExtension(
                serviceProvider.getAnyService(Requirements.IO.getServiceType()),
                serviceProvider.getAnyService(Requirements.TWITCH_SERVICE.getServiceType()),
                serviceProvider.getAnyService(Requirements.O_AUTH_TOKEN_IDENTIFIER_SETTER.getServiceType()),
                serviceProvider.getAnyService(Requirements.VIEWER_IDENTITY_SERVICE.getServiceType())
                );
    }

    @Override
    public @NonNull ImmutableList<CommandDeclaration> createCommandDefinitions(@NonNull JokeExtension extension, @NonNull ServiceProvider serviceProvider, CommandDeclaration.@NonNull Factory factory) {
        final var accessRule = AccessRule.create(Role.ANY_USER, Duration.ofSeconds(15));
        final var adminRule = AccessRule.create(Role.ADMINISTRATOR, Duration.ZERO);
        return ImmutableList.of(
                factory.create("joke-cfg {cmd} [parm]", adminRule, new ExtensionStateCommand(serviceProvider.getAnyService(IO.class),extension)),
                factory.create("joke [id]", accessRule, new SayJokeCommand(extension))
        );
    }

}
