package ac.checks.type;

import ac.AbstractCheck;
import ac.utils.anticheat.update.RotationUpdate;

public interface RotationCheck extends AbstractCheck {

    default void process(final RotationUpdate rotationUpdate) {
    }
}
