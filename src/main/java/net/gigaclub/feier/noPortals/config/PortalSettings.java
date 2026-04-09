package net.gigaclub.feier.noPortals.config;

import org.bukkit.configuration.file.FileConfiguration;

public record PortalSettings(
        boolean blockNether,
        boolean blockEnd,
        boolean pushback,
        double pushbackStrength,
        String netherBlockedMessage,
        String endBlockedMessage
) {
    public static PortalSettings load(FileConfiguration config) {
        return new PortalSettings(
                config.getBoolean("block-nether", true),
                config.getBoolean("block-end", true),
                config.getBoolean("pushback", true),
                config.getDouble("pushback-strength", 1.0),
                config.getString("messages.nether-blocked", "<red>Du darfst Nether-Portale nicht betreten.</red>"),
                config.getString("messages.end-blocked", "<red>Du darfst End-Portale nicht betreten.</red>")
        );
    }
}
