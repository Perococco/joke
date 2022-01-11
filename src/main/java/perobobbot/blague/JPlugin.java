package perobobbot.blague;


import com.google.common.collect.ImmutableSet;
import perobobbot.extension.ExtensionPlugin;

/**
 * This is the entry point of the plugin (at the jplugman level).
 * <p>
 * For jplugman, the plugin provides a service which in the case
 * of the bot, is an ExtensionPlugin (which might be confusing, sorry).
 * <p>
 * An ExtensionPlugin is a plugin for the Bot to add an extension to itself.
 */
public class JPlugin extends ExtensionPlugin {

    public JPlugin() {
        super(JokeExtensionPlugin::new,
                ImmutableSet.of(
                        Requirements.IO,
                        Requirements.TWITCH_SERVICE,
                        Requirements.O_AUTH_TOKEN_IDENTIFIER_SETTER,
                        Requirements.PLATFORM_USER_SERVICE));
    }

}
