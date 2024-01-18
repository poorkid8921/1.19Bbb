package ac.checks.impl.badpackets;

import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "BadPacketsN")
public class BadPacketsN extends Check implements PacketCheck {
    public BadPacketsN(final GrimPlayer player) {
        super(player);
    }
}
