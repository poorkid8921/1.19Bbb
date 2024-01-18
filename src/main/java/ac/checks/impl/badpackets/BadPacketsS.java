package ac.checks.impl.badpackets;

import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "BadPacketsS")
public class BadPacketsS extends Check implements PacketCheck {
    public BadPacketsS(GrimPlayer player) {
        super(player);
    }

}
