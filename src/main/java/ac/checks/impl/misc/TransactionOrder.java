package ac.checks.impl.misc;

import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PacketCheck;
import ac.player.GrimPlayer;

@CheckData(name = "TransactionOrder", experimental = false)
public class TransactionOrder extends Check implements PacketCheck {
    public TransactionOrder(GrimPlayer player) {
        super(player);
    }
}