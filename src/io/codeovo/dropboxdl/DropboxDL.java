package io.codeovo.dropboxdl;

import io.codeovo.dropboxdl.commands.UploadCommand;
import io.codeovo.dropboxdl.metrics.Metrics;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.logging.Level;

public class DropboxDL extends JavaPlugin {
    private static DropboxDL dropboxDL;
    private DbxClientV2 dbxClient;

    private String pluginConsolePrefix = "Dropbox DL >> ";
    private String accessToken = "";

    @Override
    public void onEnable() {
        getLogger().info(pluginConsolePrefix + "Enabling...");
        dropboxDL = this;

        loadConfiguration();
        loadDropboxData();
        registerCommands();
        loadMetrics();
        getLogger().info(pluginConsolePrefix + "Enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info(pluginConsolePrefix + "Disabling...");
        dropboxDL = null;
        getLogger().info(pluginConsolePrefix + "Disabled.");
    }

    private void loadConfiguration() {
        getLogger().info(pluginConsolePrefix + "Loading config...");
        saveDefaultConfig();

        accessToken = getConfig().getString("access-token");
    }

    private void loadDropboxData() {
        getLogger().info(pluginConsolePrefix + "Connecting to Dropbox...");
        DbxRequestConfig config = new DbxRequestConfig("codeovo/dropbox-dl");
        dbxClient = new DbxClientV2(config, accessToken);
    }

    private void registerCommands() {
        getLogger().info(pluginConsolePrefix + "Registering commands...");
        getCommand("upload").setExecutor(new UploadCommand(this));
    }

    private void loadMetrics() {
        getLogger().info(pluginConsolePrefix + "Attempting metrics...");
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, pluginConsolePrefix + "Metrics failed.");
            e.printStackTrace();
        }
    }

    public static DropboxDL getInstance() { return dropboxDL; }

    public DbxClientV2 getClient() { return dbxClient; }
}