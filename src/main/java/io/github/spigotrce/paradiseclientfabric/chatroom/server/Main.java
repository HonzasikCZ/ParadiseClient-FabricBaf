package io.github.spigotrce.paradiseclientfabric.chatroom.server;

import io.github.spigotrce.paradiseclientfabric.chatroom.common.model.UserModel;
import io.github.spigotrce.paradiseclientfabric.chatroom.server.exception.UserAlreadyRegisteredException;
import io.github.spigotrce.paradiseclientfabric.chatroom.server.config.Config;
import io.github.spigotrce.paradiseclientfabric.chatroom.server.discord.DiscordBotImpl;
import io.github.spigotrce.paradiseclientfabric.chatroom.server.netty.ChatRoomServer;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.sql.SQLException;

public class Main {
    public static Config CONFIG = new Config(new File(System.getProperty("user.dir")).toPath());

    public static void main(String[] args) {
        try {
            CONFIG.load();
        } catch (IOException exception) {
            Logging.error("Unable to load configuration", exception);
        }
        DiscordBotImpl.startDiscordBot();
        try {
            ChatRoomServer.startServer(CONFIG.getServer());
        } catch (Exception exception) {
            Logging.error("Error starting chat server...", exception);
            System.exit(1);
        }
    }

    public static boolean registerNewUser(UserModel user) throws SQLException, UserAlreadyRegisteredException {
        // TODO: register a new user in a database

        // true if the user needs verification, false otherwise
        return true;
    }
}
