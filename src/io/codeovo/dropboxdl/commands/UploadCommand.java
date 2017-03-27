package io.codeovo.dropboxdl.commands;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.RequestedVisibility;
import com.dropbox.core.v2.sharing.SharedLinkSettings;

import io.codeovo.dropboxdl.DropboxDL;
import io.codeovo.dropboxdl.utils.FileUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.*;

public class UploadCommand implements CommandExecutor {
    private DropboxDL dropboxDL;

    private String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + "DL" + ChatColor.DARK_GRAY + "] " + ChatColor.AQUA;
    private String serverDirectory = new File(".").getAbsolutePath();

    public UploadCommand(DropboxDL dropboxDL) {
        this.dropboxDL = dropboxDL;
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp() || commandSender.hasPermission("dropboxdl.use")) {
            if (strings.length == 1 && strings[0].contains(":")) {
                String[] split = strings[0].split(":");
                final String fileToUpload;

                if (split[0].equalsIgnoreCase("world") && split[1] != null) {
                    try {
                        fileToUpload = split[1];

                        File world = new File(serverDirectory + "/" + fileToUpload);
                        if (world.exists()) {
                            File worldModified = new File(serverDirectory + "/" + fileToUpload + "_copy");
                            commandSender.sendMessage(prefix + "Uploading " + fileToUpload
                                    + ", link will be provided on completion!");

                            FileUtils.copyFolder(world, worldModified);
                            FileUtils.zipFolder(serverDirectory + "/" + fileToUpload + "_copy",
                                    serverDirectory + "/" + fileToUpload + "_ready.zip");

                            File inputFile = new File(serverDirectory + "/" + fileToUpload + "_ready.zip");
                            final InputStream in = new FileInputStream(inputFile);

                            Bukkit.getScheduler().runTaskAsynchronously(DropboxDL.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        dropboxDL.getClient().files().uploadBuilder("/" + fileToUpload + ".zip")
                                                .withMode(WriteMode.OVERWRITE).uploadAndFinish(in);

                                        try {
                                            commandSender.sendMessage(prefix + "Uploaded! Download at "
                                                    + ChatColor.DARK_AQUA + dropboxDL.getClient().sharing()
                                                    .createSharedLinkWithSettings("/" + fileToUpload + ".zip",
                                                            new SharedLinkSettings(RequestedVisibility.PUBLIC,
                                                                    null, null)).getUrl());
                                        } catch (DbxException e) {
                                            if (e.getMessage().contains("shared_link_already_exist")) {
                                                commandSender.sendMessage(prefix + "Uploaded! Download at "
                                                        + ChatColor.DARK_AQUA + dropboxDL.getClient().sharing()
                                                        .listSharedLinksBuilder().withPath("/" + fileToUpload + ".zip")
                                                        .withDirectOnly(true).start().getLinks().get(0).getUrl());
                                            } else {
                                                commandSender.sendMessage(prefix
                                                        + "An error occurred, details logged to console!");
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (DbxException | IOException e) {
                                        commandSender.sendMessage(prefix
                                                + "An error occurred, details logged to console!");
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            in.close();
                                        } catch (IOException ignored) {}
                                    }
                                }
                            });

                            FileUtils.deleteDirectory(worldModified);
                            inputFile.delete();
                        } else {
                            commandSender.sendMessage(prefix + "World not found, check your parameters!");
                            return true;
                        }
                    } catch (IOException e) {
                        commandSender.sendMessage(prefix + "An error occurred, details logged to console!");
                        e.printStackTrace();
                    }
                } else if (split[0].equalsIgnoreCase("schematic") && split[1] != null) {
                    fileToUpload = split[1];
                    final String path = serverDirectory + "/plugins/WorldEdit/schematics/" + fileToUpload + ".schematic";

                    File schematic = new File(path);
                    if (schematic.exists()) {
                        commandSender.sendMessage(prefix + "Uploading " + fileToUpload + ".schematic, link will be provided on completion!");

                        try {
                            File inputFile = new File(path);
                            final FileInputStream in = new FileInputStream(inputFile);

                            Bukkit.getScheduler().runTaskAsynchronously(DropboxDL.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        dropboxDL.getClient().files().uploadBuilder("/" + fileToUpload + ".schematic")
                                                .withMode(WriteMode.OVERWRITE).uploadAndFinish(in);

                                        try {
                                            commandSender.sendMessage(prefix + "Uploaded! Download at "
                                                    + ChatColor.DARK_AQUA + dropboxDL.getClient().sharing()
                                                    .createSharedLinkWithSettings("/" + fileToUpload + ".schematic",
                                                            new SharedLinkSettings(RequestedVisibility.PUBLIC,
                                                                    null, null)).getUrl());
                                        } catch (DbxException e) {
                                            if (e.getMessage().contains("shared_link_already_exist")) {
                                                commandSender.sendMessage(prefix + "Uploaded! Download at "
                                                        + ChatColor.DARK_AQUA + dropboxDL.getClient().sharing()
                                                        .listSharedLinksBuilder().withPath("/" + fileToUpload + ".schematic")
                                                        .withDirectOnly(true).start().getLinks().get(0).getUrl());
                                            } else {
                                                commandSender.sendMessage(prefix
                                                        + "An error occurred, details logged to console!");
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (DbxException | IOException e) {
                                        commandSender.sendMessage(prefix
                                                + "An error occurred, details logged to console!");
                                        e.printStackTrace();
                                    } finally {
                                        try {
                                            in.close();
                                        } catch (IOException ignored) {}
                                    }
                                }
                            });
                        } catch (IOException e) {
                            commandSender.sendMessage(prefix + "An error occurred, details logged to console!");
                            e.printStackTrace();
                        }
                    } else {
                        commandSender.sendMessage(prefix + "Schematic not found, check your parameters!");
                    }
                } else {
                    commandSender.sendMessage(prefix + "Could not parse input!");
                }
            } else {
                commandSender.sendMessage(prefix + "Incorrect command usage.");
            }
        } else {
            commandSender.sendMessage(prefix + "You do not have permission to use that command.");
        }

        return true;
    }
}
