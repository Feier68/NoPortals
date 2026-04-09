package net.gigaclub.feier.noPortals.command;

import net.gigaclub.feier.noPortals.NoPortals;
import net.gigaclub.feier.noPortals.config.PortalSettings;
import net.gigaclub.feier.noPortals.listener.PortalBlockListener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class NoPortalsCommand implements CommandExecutor, TabCompleter {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private final NoPortals plugin;
    private final PortalBlockListener listener;

    public NoPortalsCommand(NoPortals plugin, PortalBlockListener listener) {
        this.plugin = plugin;
        this.listener = listener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(MINI_MESSAGE.deserialize("<red>Benutzung: /noportals reload</red>"));
            return true;
        }

        if (!sender.hasPermission("noportals.reload")) {
            sender.sendMessage(MINI_MESSAGE.deserialize("<red>Dafür hast du keine Berechtigung.</red>"));
            return true;
        }

        plugin.reloadConfig();
        listener.updateSettings(PortalSettings.load(plugin.getConfig()));
        sender.sendMessage(MINI_MESSAGE.deserialize("<green>NoPortals-Konfiguration wurde neu geladen.</green>"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase())) {
                return Collections.singletonList("reload");
            }
        }
        return Collections.emptyList();
    }
}
