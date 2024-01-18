package ac.checks.impl.movement;

import ac.checks.Check;
import ac.checks.CheckData;
import ac.checks.type.PostPredictionCheck;
import ac.player.GrimPlayer;

@CheckData(name = "Entity control", configName = "EntityControl")
public class EntityControl extends Check implements PostPredictionCheck {
    public EntityControl(GrimPlayer player) {
        super(player);
    }

    public void rewardPlayer() {
        reward();
    }
}
