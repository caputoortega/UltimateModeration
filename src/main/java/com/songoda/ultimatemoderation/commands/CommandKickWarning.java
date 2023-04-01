package com.songoda.ultimatemoderation.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimatemoderation.UltimateModeration;
import com.songoda.ultimatemoderation.punish.Punishment;
import com.songoda.ultimatemoderation.punish.PunishmentType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandKickWarning extends AbstractCommand {

    private final UltimateModeration plugin;

    public CommandKickWarning(UltimateModeration plugin) {
        super(CommandType.CONSOLE_OK, "KickWarning");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 1)
            return ReturnType.SYNTAX_ERROR;

        StringBuilder reasonBuilder = new StringBuilder();
        if (args.length > 1) {
            for (int i = 1; i < args.length; i++) {
                String line = args[i];
                reasonBuilder.append(line).append(" ");

            }
        }
        String reason = reasonBuilder.toString().trim();

        OfflinePlayer player = Bukkit.getPlayer(args[0]);

        if (sender instanceof Player && player.getPlayer().hasPermission("um.kickwarning.exempt")) {
            plugin.getLocale().newMessage("You cannot kick-warn this player.").sendPrefixedMessage(sender);
            return ReturnType.FAILURE;
        }

        new Punishment(PunishmentType.KICK_WARNING, reason.equals("") ? null : reason)
                .execute(sender, player);

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            return players;
        } else if (args.length == 2) {
            return Collections.singletonList("For being bad");
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "um.kickwarn";
    }

    @Override
    public String getSyntax() {
        return "/Kickwarn <player> [reason]";
    }

    @Override
    public String getDescription() {
        return "Allows you to kick-warn players.";
    }
}
