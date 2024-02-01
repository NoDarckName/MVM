package org.harbingers_of_chaos.mvb.application;

import com.mojang.authlib.GameProfile;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.minecraft.server.Whitelist;
import net.minecraft.util.dynamic.DynamicSerializableUuid;
import net.minecraft.server.WhitelistEntry;
import org.apache.logging.log4j.core.util.UuidUtil;
import org.harbingers_of_chaos.mvb.Discord;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import org.harbingers_of_chaos.mvlib.mySQL;
import org.harbingers_of_chaos.mvb.profiles.profiles;
import org.harbingers_of_chaos.mvlib.Config;
import org.harbingers_of_chaos.mvm.MystiVerseModServer;

import static org.harbingers_of_chaos.mvm.MystiVerseModServer.LOGGER;

public class AcceptButton {
    private static Guild guild;
    private static TextChannel applicationsLogChat;
    public static boolean onButton(ButtonInteractionEvent event, SimpleDateFormat format, Date date) {
        try {
            guild = event.getGuild();
            assert guild != null;
            Role accessRole = guild.getRoleById("1160295664668913816");
            applicationsLogChat = Discord.getJda().getTextChannelById(Config.INSTANCE.discord.LogChat);
            assert applicationsLogChat != null;
            assert accessRole != null;



            //получает айди кнопки которую нажали
            int appInt = Integer.parseInt(Objects.requireNonNull(event.getButton().getId()));
            long authorId = mySQL.getApplicationUser_id(appInt);
            String nickname = mySQL.getApplicationNickname(appInt);
            LOGGER.info("Application access");
            LOGGER.info(authorId);

            if (authorId != 0) {
                try {
                    guild.addRoleToMember(UserSnowflake.fromId(authorId), Objects.requireNonNull(guild.getRoleById("1160295664668913816"))).queue();
                    guild.removeRoleFromMember(UserSnowflake.fromId(authorId), Objects.requireNonNull(guild.getRoleById("1190724252966596701"))).queue();

                    EmbedBuilder applicationsAcceptLog = new EmbedBuilder();
                    applicationsAcceptLog.setTitle("Заявка от Id:" + authorId, null);
                    applicationsAcceptLog.setColor(new Color(0x0bda51));
                    applicationsAcceptLog.setDescription("### ✅ Одобрено - <@" + event.getUser().getId() + ">");
                    applicationsAcceptLog.setFooter("Заявка была создана в " + format.format(date) + "  \nAppID: " + authorId);

                    assert applicationsLogChat != null;
                    applicationsLogChat.sendMessage("<@" + authorId + ">").setEmbeds(applicationsAcceptLog.build()).queue();

                    event.getChannel().deleteMessageById(event.getMessage().getId()).queue();

                    Config.INSTANCE.game.players++;
                    mySQL.addPlayer(Config.INSTANCE.game.players,appInt,nickname, String.valueOf(authorId));
//                    Member member = guild.getMember(UserSnowflake.fromId(authorId));
//                    guild.modifyNickname(guild.getMember(UserSnowflake.fromId(authorId)),nickname).queue();

                    Whitelist whitelist = MystiVerseModServer.getMinecraftServer().getPlayerManager().getWhitelist();
                    WhitelistEntry whitelistEntry = new WhitelistEntry(new GameProfile(DynamicSerializableUuid.getOfflinePlayerUuid(nickname),nickname));
                    whitelist.add(whitelistEntry);

                    LOGGER.info("Заявка от Id:" + authorId);
                } catch (Exception e) {
                    LOGGER.warn("Access application error: " + e);
                }
            } else {
                LOGGER.warn("id null");
                return false;
            }
        } catch (Exception e) {
            LOGGER.warn("Application warning: " + e);
            return false;
        }
        return true;
    }
}