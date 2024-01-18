package ac;

import java.util.Collection;
import java.util.UUID;

public interface User {
    String getName();

    UUID getUniqueId();

    int getTransactionPing();

    String getVersionName();

    double getHorizontalSensitivity();

    double getVerticalSensitivity();

    boolean isVanillaMath();

    void updatePermissions();

    Collection<? extends AbstractCheck> getChecks();
}