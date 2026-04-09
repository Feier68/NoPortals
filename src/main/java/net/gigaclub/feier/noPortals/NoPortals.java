package net.gigaclub.feier.noPortals;

import net.gigaclub.feier.noPortals.command.NoPortalsCommand;
import net.gigaclub.feier.noPortals.config.PortalSettings;
import net.gigaclub.feier.noPortals.listener.PortalBlockListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class NoPortals extends JavaPlugin {

    private PortalBlockListener portalBlockListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PortalSettings settings = PortalSettings.load(getConfig());
        portalBlockListener = new PortalBlockListener(this, settings);
        getServer().getPluginManager().registerEvents(portalBlockListener, this);

        var command = getCommand("noportals");
        if (command != null) {
            NoPortalsCommand commandExecutor = new NoPortalsCommand(this, portalBlockListener);
            command.setExecutor(commandExecutor);
            command.setTabCompleter(commandExecutor);
        } else {
            getLogger().warning("Konnte den /noportals Command nicht finden. Ist er in der plugin.yml eingetragen?");
        }

        getLogger().info("NoPortals successfully enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
