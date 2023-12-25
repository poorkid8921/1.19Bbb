package main.utils.instances;

import lombok.Getter;
import lombok.Setter;
import main.expansions.optimizer.AnimPackets;

@Getter
@Setter
public class CustomPlayerDataHolder {
    private int m;
    private int t;
    private AnimPackets lastPacket = AnimPackets.MISC;
    private boolean ignoreAnim = false;

    public CustomPlayerDataHolder(int m,
                                  int t) {
        this.m = m;
        this.t = t;
    }
}
