package ac.checks.impl.scaffolding;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import ac.checks.CheckData;
import ac.checks.type.BlockPlaceCheck;
import ac.player.GrimPlayer;
import ac.utils.anticheat.update.BlockPlace;
import ac.utils.collisions.datatypes.SimpleCollisionBox;

import java.util.Collections;

@CheckData(name = "PositionPlace")
public class PositionPlace extends BlockPlaceCheck {

    public PositionPlace(GrimPlayer player) {
        super(player);
    }

    @Override
    public void onBlockPlace(final BlockPlace place) {
        if (place.getMaterial() == StateTypes.SCAFFOLDING) return;

        SimpleCollisionBox combined = getCombinedBox(place);

        // Alright, now that we have the most optimal positions for each place
        // Please note that minY may be lower than maxY, this is INTENTIONAL!
        // Each position represents the best case scenario to have clicked
        //
        // We will now calculate the most optimal position for the player's head to be in
        double minEyeHeight = Collections.min(player.getPossibleEyeHeights());
        double maxEyeHeight = Collections.max(player.getPossibleEyeHeights());
        // I love the idle packet, why did you remove it mojang :(
        // Don't give 0.03 lenience if the player is a 1.8 player and we know they couldn't have 0.03'd because idle packet
        double movementThreshold = !player.packetStateData.didLastMovementIncludePosition || player.getClientVersion().isNewerThanOrEquals(ClientVersion.V_1_9) ? player.getMovementThreshold() : 0;

        SimpleCollisionBox eyePositions = new SimpleCollisionBox(player.x, player.y + minEyeHeight, player.z, player.x, player.y + maxEyeHeight, player.z);
        eyePositions.expand(movementThreshold);

        // If the player is inside a block, then they can ray trace through the block and hit the other side of the block
        if (eyePositions.isIntersected(combined)) {
            return;
        }

        // So now we have the player's possible eye positions
        // So then look at the face that the player has clicked
        boolean flag = switch (place.getDirection()) {
            case NORTH -> // Z- face
                    eyePositions.minZ > combined.minZ;
            case SOUTH -> // Z+ face
                    eyePositions.maxZ < combined.maxZ;
            case EAST -> // X+ face
                    eyePositions.maxX < combined.maxX;
            case WEST -> // X- face
                    eyePositions.minX > combined.minX;
            case UP -> // Y+ face
                    eyePositions.maxY < combined.maxY;
            case DOWN -> // Y- face
                    eyePositions.minY > combined.minY;
            default -> false;
        };

        if (flag) {
            if (flag() && shouldModifyPackets() && shouldCancel()) {
                place.resync();
            }
        }
    }
}