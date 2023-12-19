package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;
import main.expansions.optimizer.crystal.AnimPackets;

@Getter
@Setter
public class OptimizerEntry {
    private AnimPackets lastPacket;
    private boolean ignoreAnim;
}
