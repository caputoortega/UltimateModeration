package com.songoda.ultimatemoderation.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.ultimatemoderation.UltimateModeration;
import com.songoda.ultimatemoderation.gui.PunishmentsGui;

public class CommandHistory extends AbstractCommand {

    private UltimateModeration instance;

    public CommandHistory(UltimateModeration plugin) {
        super(CommandType.PLAYER_ONLY, "history");

        this.instance = plugin;

    }

    @Override
    public String getDescription() {
        return "View the player's criminal history";
    }

    @Override
    public String getPermissionNode() {
        return "um.history";
    }

    @Override
    public String getSyntax() {
        return "history <player>";
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            List<String> players = new ArrayList<>();
            for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
                players.add(player.getName());
            }
            return players;
        }
        return null;
    }

    protected ReturnType runCommand(CommandSender sender, String... args) {
        if(args.length < 1) return ReturnType.SYNTAX_ERROR;

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if(target != null) {

            instance.getGuiManager().showGUI((Player) sender, new PunishmentsGui(instance, target));

        } else {

            sender.sendMessage(instance.getLocale().getMessage("command.generic.playerNotFound")
                                                    .processPlaceholder("player", args[0])
                                                    .getPrefixedMessage()
            );
            
        }


        return ReturnType.SUCCESS;

    }
    
}
