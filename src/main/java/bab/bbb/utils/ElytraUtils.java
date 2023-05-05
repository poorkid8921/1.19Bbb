package bab.bbb.utils;

import org.bukkit.Location;

import static java.lang.String.format;
import static org.apache.commons.math3.util.FastMath.*;
public class ElytraUtils {
    public static double blocksPerTick(Location from, Location to) {
        return hypot(
                to.getX() - from.getX(),
                to.getZ() - from.getZ()
        );
    }
    public static String speed(double flySpeed) {
        return format("%.2f", min((double) round(flySpeed * 100.0D) / 100.0D, 20.0D));
    }
}