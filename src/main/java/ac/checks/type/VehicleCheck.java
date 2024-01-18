package ac.checks.type;

import ac.AbstractCheck;
import ac.utils.anticheat.update.VehiclePositionUpdate;

public interface VehicleCheck extends AbstractCheck {

    void process(final VehiclePositionUpdate vehicleUpdate);
}
