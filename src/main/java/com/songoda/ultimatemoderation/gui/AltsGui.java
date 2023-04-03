package com.songoda.ultimatemoderation.gui;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.gui.AnvilGui;
import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.ItemUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatemoderation.UltimateModeration;
import com.songoda.ultimatemoderation.punish.PunishmentType;
import com.songoda.ultimatemoderation.punish.player.PlayerPunishData;
import com.songoda.ultimatemoderation.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AltsGui extends Gui {

    private final UltimateModeration plugin;

    private final List<UUID> players = new ArrayList<>();

    public AltsGui(UltimateModeration plugin, OfflinePlayer toModerate) {
        this.plugin = plugin;
        
        setRows(6);
        setDefaultItem(null);

        players.addAll(plugin.getDataManager().getPlayerAlts(toModerate.getUniqueId()));

        setTitle(plugin.getLocale().getMessage("gui.alts.title").processPlaceholder("toModerate", toModerate.getName()).getMessage());

        showPage();
    }

    private void showPage() {
        if (inventory != null)
            inventory.clear();
        setActionForRange(0, 53, null);

        // decorate the edges
        ItemStack glass2 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_2.getMaterial(CompatibleMaterial.BLUE_STAINED_GLASS_PANE));
        ItemStack glass3 = GuiUtils.getBorderItem(Settings.GLASS_TYPE_3.getMaterial(CompatibleMaterial.LIGHT_BLUE_STAINED_GLASS_PANE));

        // edges will be type 3
        mirrorFill(0, 2, true, true, glass3);
        mirrorFill(1, 1, true, true, glass3);

        // decorate corners with type 2
        mirrorFill(0, 0, true, true, glass2);
        mirrorFill(1, 0, true, true, glass2);
        mirrorFill(0, 1, true, true, glass2);

        setButton(5, 2, GuiUtils.createButtonItem(CompatibleMaterial.ENDER_PEARL,
                plugin.getLocale().getMessage("gui.players.search").getMessage()),
                (event) -> {
                    AnvilGui gui = new AnvilGui(event.player, this);
                    gui.setAction(event2 -> {
                        List<UUID> players = new ArrayList<>(plugin.getPunishmentManager().getPunishments().keySet());

                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (players.contains(p.getUniqueId())) continue;
                            players.add(p.getUniqueId());
                        }

                        List<UUID> found = players.stream().filter(uuid -> Bukkit.getOfflinePlayer(uuid).getName().toLowerCase().contains(gui.getInputText().toLowerCase())).collect(Collectors.toList());

                        if (found.size() >= 1) {
                            this.players.clear();
                            this.players.addAll(found);
                            showPage();
                        } else {
                            plugin.getLocale().getMessage("gui.players.nonefound").sendMessage(event.player);
                        }
                        event2.player.closeInventory();
                    });

                    ItemStack item = new ItemStack(Material.PAPER);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName(plugin.getLocale().getMessage("gui.players.name").getMessage());
                    item.setItemMeta(meta);

                    gui.setInput(item);
                    guiManager.showGUI(event.player, gui);
                });


        this.pages = (int) Math.max(1, Math.ceil(players.size() / ((double) 28)));

        final List<UUID> toUseFinal = players.stream().skip((page - 1) * 28).limit(28).collect(Collectors.toList());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            int num = 11;
            for (UUID uuid : toUseFinal) {
                if (num == 16 || num == 36)
                    num = num + 2;
                OfflinePlayer pl = Bukkit.getOfflinePlayer(uuid);
                ItemStack skull = ItemUtils.getPlayerSkull(pl);
                setItem(num, skull);

                PlayerPunishData playerPunishData = plugin.getPunishmentManager().getPlayer(pl);
                boolean isBanned = playerPunishData.getActivePunishments(PunishmentType.BAN).size() >= 1;

                ArrayList<String> lore = new ArrayList<>();
                if(isBanned) {
                    lore.add(plugin.getLocale().getMessage("gui.alts.banned").getMessage());
                } else {
                    lore.add(
                            TextUtils.formatText(pl.isOnline() 
                                    ? "&a" + plugin.getLocale().getMessage("gui.players.online.online").getMessage()
                                    : "&c" + plugin.getLocale().getMessage("gui.players.online.offline").getMessage())
                            );
                }

                lore.add(plugin.getLocale().getMessage("gui.players.click").getMessage());
                lore.add("");

                setButton(num, GuiUtils.createButtonItem(skull, TextUtils.formatText((isBanned ? "&c&l" : "&7&l") + pl.getName()), lore),
                        (event) -> guiManager.showGUI(event.player, new PlayerGui(plugin, pl, event.player))); 

                num++;
            }
        });

        // enable page events
        setNextPage(4, 7, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, plugin.getLocale().getMessage("gui.general.next").getMessage()));
        setPrevPage(4, 1, GuiUtils.createButtonItem(CompatibleMaterial.ARROW, plugin.getLocale().getMessage("gui.general.back").getMessage()));
        setOnPage((event) -> showPage());
    }

}
