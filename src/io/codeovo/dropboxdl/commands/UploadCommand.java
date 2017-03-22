package io.codeovo.dropboxdl.commands;

import io.codeovo.dropboxdl.DropboxDL;
import io.codeovo.dropboxdl.utils.FileUtils;

import com.dropbox.core.v2.files.FileMetadata;

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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp() || commandSender.hasPermission("dropboxdl.use")) {
            if (strings.length == 1 && strings[0].contains(":")) {
                String[] split = strings[0].split(":");
                String fileToUpload;

                if (split[0].equalsIgnoreCase("world") && split[1] != null) {
                    fileToUpload = split[1];

                    File world = new File(serverDirectory + "/" + fileToUpload);
                    if (world.exists()) {
                        File worldModified = new File(serverDirectory + "/" + fileToUpload + "_copy");
                        commandSender.sendMessage(prefix + "Uploading " + fileToUpload + ", link will be provided on completion!");

                        try {
                            FileUtils.copyFolder(world, worldModified);
                        } catch (IOException e) {
                            commandSender.sendMessage(prefix + "A fatal error occurred during duplication, check console!");
                            e.printStackTrace();
                        }

                        try {
                            FileUtils.zipFolder(serverDirectory + "/" + fileToUpload + "_copy", serverDirectory + "/" + fileToUpload + "_ready.zip");
                        } catch (Exception e) {
                            commandSender.sendMessage(prefix + "A fatal error occurred during zip, check console!");
                            e.printStackTrace();
                        }

                        File inputFile = new File(serverDirectory + "/" + fileToUpload + "_ready.zip");
                        try {
                            try (InputStream in = new FileInputStream(inputFile)) {
                                FileMetadata metadata = client.files().uploadBuilder("/" + fileToUpload + ".zip")
                                        .uploadAndFinish(in);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        try {
                            commandSender.sendMessage(prefix + "Uploaded! Download at " + ChatColor.DARK_AQUA + downloadLink.client.createShareableUrl("/" + fileToUpload + ".zip"));
                        } catch (DbxException e) {
                            e.printStackTrace();
                            commandSender.sendMessage(prefix + "A fatal error has occurred, check your console for more details.");
                        }

                        worldModified.delete();
                        inputFile.delete();
                    } else {
                        commandSender.sendMessage(prefix + "World not found, check your parameters!");
                    }
                } else if (split[0].equalsIgnoreCase("schematic") && split[1] != null) {
                    fileToUpload = split[1];

                    File schematic = new File(serverDirectory + "/plugins/WorldEdit/schematics/" + fileToUpload + ".schematic");
                    if (schematic.exists()) {
                        commandSender.sendMessage(prefix + "Uploading " + fileToUpload + ".schematic, link will be provided on completion!");
                        String path = serverDirectory + "/plugins/WorldEdit/schematics/" + fileToUpload + ".schematic";
                        File inputFile = new File(path);
                        FileInputStream inputStream = null;
                        try {
                            inputStream = new FileInputStream(inputFile);
                            try {
                                DbxEntry.File uploadedFile = null;
                                try {
                                    uploadedFile = downloadLink.client.uploadFile("/" + fileToUpload + ".schematic", DbxWriteMode.add(), inputFile.length(), inputStream);
                                } catch (DbxException | IOException e) {
                                }
                            } finally {
                                try {
                                    inputStream.close();
                                } catch (IOException e) {
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        try {
                            commandSender.sendMessage(prefix + "Uploaded! Download at " + ChatColor.DARK_AQUA + downloadLink.client.createShareableUrl("/" + fileToUpload + ".schematic"));
                        } catch (DbxException e) {
                            e.printStackTrace();
                            commandSender.sendMessage(prefix + "A fatal error has occurred, check your console for more details.");
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

        return false;
    }
}
