package perobobbot.blague;


import jplugman.annotation.Extension;
import jplugman.api.Disposable;
import jplugman.api.ServiceProvider;
import lombok.NonNull;
import perobobbot.extension.PerobobbotExtensionPluginBase;
import perobobbot.plugin.PerobobbotPlugin;

@Extension(point = PerobobbotPlugin.class, version = "1.0.0")
public class JokeExtensionPlugin extends PerobobbotExtensionPluginBase implements Disposable {

    public JokeExtensionPlugin(@NonNull ModuleLayer pluginLayer, @NonNull ServiceProvider serviceProvider) {
        super(new JokeExtensionFactory(), pluginLayer,serviceProvider);
    }

    @Override
    public void dispose() {
        getData().disableExtension();
    }
}
