package bab.bbb.Events.Dupes;

import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerDupeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    Entity player;

    public Entity getPlayer() {
        return player;
    }

    private boolean cancelled;

    public PlayerDupeEvent(Entity player) {
       this.player = player;
    }
    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}