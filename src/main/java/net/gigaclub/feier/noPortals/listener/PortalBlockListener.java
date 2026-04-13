package net.gigaclub.feier.noPortals.listener;

import net.gigaclub.feier.noPortals.config.PortalSettings;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PortalBlockListener implements Listener {

    private static final double MIN_VECTOR_LENGTH_SQUARED = 1.0E-4;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final Plugin plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private PortalSettings settings;

    public PortalBlockListener(Plugin plugin, PortalSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public void updateSettings(PortalSettings settings) {
        this.settings = settings;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null || !hasChangedBlock(event)) {
            return;
        }

        String message = getBlockedMessage(event.getTo().getBlock().getType());
        if (message == null) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        long now = System.currentTimeMillis();
        if (cooldowns.getOrDefault(player.getUniqueId(), 0L) > now) {
            return;
        }
        cooldowns.put(player.getUniqueId(), now + 1000L);

        handleBlockedPortal(player, message, event.getFrom(), event.getTo());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        String message = getBlockedMessage(event.getCause());
        if (message != null) {
            event.setCancelled(true);
        }
    }

    private String getBlockedMessage(Material type) {
        if (type == Material.NETHER_PORTAL && settings.blockNether()) {
            return settings.netherBlockedMessage();
        }
        if (type == Material.END_PORTAL && settings.blockEnd()) {
            return settings.endBlockedMessage();
        }
        return null;
    }

    private String getBlockedMessage(PlayerTeleportEvent.TeleportCause cause) {
        if (cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && settings.blockNether()) {
            return settings.netherBlockedMessage();
        }
        if (cause == PlayerTeleportEvent.TeleportCause.END_PORTAL && settings.blockEnd()) {
            return settings.endBlockedMessage();
        }
        return null;
    }

    private void handleBlockedPortal(Player player, String message, Location from, Location to) {
        if (settings.pushback()) {
            Vector pushback = calculatePushback(player, from, to);
            plugin.getServer().getScheduler().runTask(plugin, () -> player.setVelocity(pushback));
        }
        if (!message.isBlank()) {
            player.sendMessage(MINI_MESSAGE.deserialize(message));
        }
    }

    private Vector calculatePushback(Player player, Location from, Location to) {
        Vector pushback = null;
        Location blockedLocation = to != null ? to : player.getLocation();

        if (from != null && to != null) {
            pushback = from.toVector().subtract(to.toVector());
            pushback.setY(0);
        }

        if (pushback == null || pushback.lengthSquared() < MIN_VECTOR_LENGTH_SQUARED) {
            Location portalCenter = blockedLocation.getBlock().getLocation().add(0.5, 0.0, 0.5);
            Location referenceLocation = from != null ? from : player.getLocation();
            pushback = referenceLocation.toVector().subtract(portalCenter.toVector());
            pushback.setY(0);
        }

        if (pushback.lengthSquared() < MIN_VECTOR_LENGTH_SQUARED) {
            pushback = new Vector(0, 0, 0);
        } else {
            pushback.normalize().multiply(settings.pushbackStrength());
        }

        pushback.setY(0.2);
        return pushback;
    }

    private boolean hasChangedBlock(PlayerMoveEvent event) {
        return event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()
                || !event.getFrom().getWorld().equals(event.getTo().getWorld());
    }
}
