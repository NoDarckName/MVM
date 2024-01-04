package org.harbingers_of_chaos.mvb;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.prefs.Preferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.harbingers_of_chaos.mvb.CityHandler.cityHandler;
import org.harbingers_of_chaos.mvb.CityHandler.cityModalHandler;
import org.harbingers_of_chaos.mvb.application.ApplicationHandler;
import org.harbingers_of_chaos.mvb.application.RejectWithReasonButton;
import org.harbingers_of_chaos.mvb.commands.CommandHandler;
import org.harbingers_of_chaos.mvb.suggestion.SuggestHandler;
import org.harbingers_of_chaos.mvm.Config;
import org.harbingers_of_chaos.mvm.MystiVerseModServer;
public class Discord {
    // We can't use the Gson instance from the MystiVerseModServer class since it has html escaping disabled, which we want enabled for obvious reasons
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setLenient().create();

    private static JDA jda;

    public static void start() {
        if (Config.INSTANCE.discord.token.isEmpty()) {
            MystiVerseModServer.LOGGER.fatal("Unable to load, no Discord token is specified!");
            return;
        }

        if (Config.INSTANCE.discord.channel.isEmpty()) {
            MystiVerseModServer.LOGGER.fatal("Unable to load, no Discord channel is specified!");
            return;
        }

        if (Config.INSTANCE.sqLite.url.isEmpty()) {
            MystiVerseModServer.LOGGER.fatal("Unable to load, no SQLite url is specified!");
            return;
        }

        if (Config.INSTANCE.sqLite.password.isEmpty()) {
            MystiVerseModServer.LOGGER.fatal("Unable to load, no SQLite password is specified!");
            return;
        }

        if (Config.INSTANCE.sqLite.user.isEmpty()) {
            MystiVerseModServer.LOGGER.fatal("Unable to load, no SQLite user is specified!");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            SQLite.getConnection();
            SQLite.createDB();
            MystiVerseModServer.LOGGER.info("MVM:SQLite start");
        } catch (SQLException | ClassNotFoundException e) {
            MystiVerseModServer.LOGGER.fatal("Exception initializing SQLite", e);
        }


        try {
            jda = JDABuilder.createDefault(Config.INSTANCE.discord.token, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                    //.setActivity(Activity.playing("Лучший в мире сервер MystiVerse"))
                    .addEventListeners(new CommandHandler())
                    .addEventListeners(new ApplicationHandler())
                    .addEventListeners(new RejectWithReasonButton())
                    .addEventListeners(new SuggestHandler())
                    .addEventListeners(new cityHandler())
                    .addEventListeners(new cityModalHandler())
                    //.addEventListeners(new NewsEmbedHandler())
                    .build();
        } catch (Exception e) {
            MystiVerseModServer.LOGGER.fatal("Exception initializing JDA", e);
        }
        MystiVerseModServer.LOGGER.info("Bot started");
        MystiVerseModServer.LOGGER.info("Number of applications sent: " + Config.INSTANCE.discord.appInt);
    }
    public static void send(String message) {
        if (jda != null) {
            var channel = jda.getTextChannelById(Config.INSTANCE.discord.channel);
            if (channel != null) {
                channel.sendMessage(message).queue();
            } else {
                MystiVerseModServer.LOGGER.error("Unable to send messages, invalid Discord channel!!!!");
            }
        }
    }
    public static void stop() {
        if (jda != null) {
            jda.shutdown();
            jda = null;
            try {
                SQLite.closeConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static JDA getJda() {return jda;}
}
