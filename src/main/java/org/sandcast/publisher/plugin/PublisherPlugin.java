package org.sandcast.publisher.plugin;

import com.mashape.unirest.http.Unirest;
import java.io.IOException;
import org.sandcast.publisher.plugin.listener.PublishableEventListener;
import java.util.logging.Level;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.sandcast.publisher.plugin.command.PublisherCommand;
import org.sandcast.publisher.plugin.adapter.DiscordAdapter;
import org.sandcast.publisher.plugin.adapter.PublishingAdapter;

public class PublisherPlugin extends JavaPlugin {

    private PublishingAdapter adapter;
    private PublishableEventListener publishableListener;

    @Override
    public void onDisable() {
        try {
            Unirest.shutdown();
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onEnable() {
        final String pluginToken = getConfig().getString("discord-token");
        adapter = new DiscordAdapter(pluginToken); //could instead register multiple including httpaddapter...
        publishableListener = new PublishableEventListener(adapter);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(publishableListener, this);
        getCommand("discord").setExecutor(new PublisherCommand(adapter));
        PluginDescriptionFile pdfFile = this.getDescription();
        getLogger().log(Level.INFO, "{0} version {1} is enabled!", new Object[]{pdfFile.getName(), pdfFile.getVersion()});
    }

    public PublishingAdapter getWireController() {
        return adapter;
    }
}