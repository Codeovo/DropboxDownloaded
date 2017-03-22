package io.codeovo.dropboxdl;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import org.bukkit.plugin.java.JavaPlugin;

public class DropboxDL extends JavaPlugin {
    private static DropboxDL dropboxDL;

    private String pluginConsolePrefix = "Dropbox DL >> ";
    private DbxClientV2 dbxClient;

    @Override
    public void onEnable() {
        getLogger().info(pluginConsolePrefix + "Enabling...");
        dropboxDL = this;
        getLogger().info(pluginConsolePrefix + "Enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info(pluginConsolePrefix + "Disabling...");
        dropboxDL = null;
        getLogger().info(pluginConsolePrefix + "Disabled.");
    }

    private void loadDropboxData(String accessToken) {
        getLogger().info(pluginConsolePrefix + "Connecting to Dropbox...");
        DbxRequestConfig config = new DbxRequestConfig("codeovo/dropbox-dl");
        dbxClient = new DbxClientV2(config, accessToken);
    }

    public static DropboxDL getInstance() { return dropboxDL; }

    public DbxClientV2 getClient() { return dbxClient; }
}