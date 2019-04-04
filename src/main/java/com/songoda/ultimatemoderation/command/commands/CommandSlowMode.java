package com.songoda.ultimatemoderation.command.commands;

import com.songoda.ultimatemoderation.UltimateModeration;
import com.songoda.ultimatemoderation.command.AbstractCommand;
import com.songoda.ultimatemoderation.listeners.ChatListener;
import com.songoda.ultimatemoderation.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandSlowMode extends AbstractCommand {

    public CommandSlowMode() {
        super(true, true, "Slowmode");
    }

    @Override
    protected ReturnType runCommand(UltimateModeration instance, CommandSender sender, String... args) {
        if (args.length != 1)
            return ReturnType.SYNTAX_ERROR;

        long delay = Methods.parseTime(args[0]);

        if (delay == 0)
            return ReturnType.SYNTAX_ERROR;

        ChatListener.setSlowModeOverride(delay);

        Bukkit.getOnlinePlayers().forEach(player ->
                player.sendMessage(instance.getReferences().getPrefix() +
                        instance.getLocale().getMessage("event.slowmode.enabled",(delay / 1000))));

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(UltimateModeration instance, CommandSender sender, String... args) {
        if (args.length == 1) {
            List<String> players = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                players.add(player.getName());
            }
            return players;
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "um.slowmode";
    }

    @Override
    public String getSyntax() {
        return "/slowmode <delay>";
    }

    @Override
    public String getDescription() {
        return "Allows you to see inside of a players inventory.";
    }
}