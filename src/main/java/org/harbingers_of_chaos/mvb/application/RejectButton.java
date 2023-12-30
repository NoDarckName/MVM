package org.harbingers_of_chaos.mvb.application;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Objects;

import static org.harbingers_of_chaos.mvb.Main.*;
import static org.harbingers_of_chaos.mvb.Main.log;

public class RejectButton {
    public static boolean onButton(ButtonInteractionEvent event) {

        try {
            Guild guild = event.getGuild();
            assert guild != null;
            TextChannel applicationsLogChat = jda.getTextChannelById("1189900614226944110");
            Role accessRole = guild.getRoleById("1160295664668913816");
            assert applicationsLogChat != null;
            assert accessRole != null;

            long authorId = Long.parseLong(Objects.requireNonNull(event.getButton().getId()))-2;
            log.info("Application №" + check + " rejected" +" от Id:" + authorId);
            if (authorId != 0) {
                try {
                    guild.addRoleToMember(UserSnowflake.fromId(authorId), guild.getRoleById("1190023047822974996")).queue();
                } catch (Exception e) {
                    log.warning("Rejected application error: " + e);
                }
            } else {
                log.warning("id null");
                return false;
            }

        } catch (Exception e) {
            log.warning("Application warning: " + e);
            return false;
        }
        return true;
    }
}