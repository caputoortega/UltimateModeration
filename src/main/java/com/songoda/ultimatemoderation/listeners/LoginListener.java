package com.songoda.ultimatemoderation.listeners;

import com.songoda.ultimatemoderation.UltimateModeration;
import com.songoda.ultimatemoderation.commands.CommandVanish;
import com.songoda.ultimatemoderation.punish.AppliedPunishment;
import com.songoda.ultimatemoderation.punish.PunishmentType;
import com.songoda.ultimatemoderation.punish.player.PlayerPunishData;
import com.songoda.ultimatemoderation.utils.Methods;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.List;

public class LoginListener implements Listener {

    private UltimateModeration instance;

    public LoginListener(UltimateModeration ultimateModeration) {
        this.instance = ultimateModeration;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        PlayerPunishData playerPunishData = instance.getPunishmentManager().getPlayer(event.getUniqueId());

        List<AppliedPunishment> appliedPunishments = playerPunishData.getActivePunishments(PunishmentType.BAN);

        if (appliedPunishments.isEmpty()) {
            

            instance.getDataManager().registerUserIp(event.getUniqueId(), event.getAddress().getHostAddress());

            return;
        };

        AppliedPunishment appliedPunishment = playerPunishData.getActivePunishments(PunishmentType.BAN).get(0);

        String banReason = appliedPunishment.getReason() == null ? "" : appliedPunishment.getReason();
        String kickMessage = appliedPunishment.getDuration() == -1 ?
            instance.getLocale().getMessage("event.ban.message.permanent")
                .processPlaceholder("reason", banReason.toLowerCase())
                .getMessage()
            :
            instance.getLocale().getMessage("event.ban.message.temporary")
                .processPlaceholder("reason", banReason.toLowerCase())
                .processPlaceholder("duration", Methods.makeReadable(appliedPunishment.getTimeRemaining()))
                .getMessage()
            ;
        event.setKickMessage(kickMessage);

        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);

    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        CommandVanish.registerVanishedPlayers(player);
    }
}
