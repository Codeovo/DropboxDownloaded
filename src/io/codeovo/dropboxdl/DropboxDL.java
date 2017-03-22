package io.codeovo.dropboxdl;

import org.bukkit.plugin.java.JavaPlugin;

public class DropboxDL extends JavaPlugin {
    private static DropboxDL dropboxDL;

    private String pluginConsolePrefix = "Dropbox DL >> ";

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

    public static DropboxDL getInstance() { return dropboxDL; }
}