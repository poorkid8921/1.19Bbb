package ac.checks;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import ac.AbstractCheck;
import ac.player.GrimPlayer;
import lombok.Getter;
import lombok.Setter;

// Class from https://github.com/Tecnio/AntiCheatBase/blob/master/src/main/java/me/tecnio/anticheat/check/Check.java
@Getter
public class Check implements AbstractCheck {
    protected final GrimPlayer player;

    public double violations;
    private double decay;
    private double setbackVL;

    private String checkName;
    private String configName;
    private String alternativeName;

    @Setter
    private boolean isEnabled;

    public Check(final GrimPlayer player) {
        this.player = player;
        final Class<?> checkClass = this.getClass();

        if (checkClass.isAnnotationPresent(CheckData.class)) {
            final CheckData checkData = checkClass.getAnnotation(CheckData.class);
            this.checkName = checkData.name();
            this.configName = checkData.configName();

            if (this.configName.equals("DEFAULT")) this.configName = this.checkName;
            this.decay = checkData.decay();
            this.setbackVL = checkData.setback();
            this.alternativeName = checkData.alternativeName();
        }
        reload();
    }

    public boolean shouldModifyPackets() {
        return isEnabled && !player.disableGrim && !player.noModifyPacketPermission;
    }

    public final boolean flag() {
        violations++;
        return true;
    }

    public final boolean flagWithSetback() {
        if (flag()) {
            setbackIfAboveSetbackVL();
            return true;
        }
        return false;
    }

    public final void reward() {
        violations = Math.max(0, violations - decay);
    }

    public void reload() {
        if (setbackVL == -1) setbackVL = Double.MAX_VALUE;
    }

    public void setbackIfAboveSetbackVL() {
        if (getViolations() > setbackVL)
            player.getSetbackTeleportUtil().executeViolationSetback();
    }

    public boolean isTransaction(PacketTypeCommon packetType) {
        return packetType == PacketType.Play.Client.PONG ||
                packetType == PacketType.Play.Client.WINDOW_CONFIRMATION;
    }
}

