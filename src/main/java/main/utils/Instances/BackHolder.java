package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

public class BackHolder {
    @Setter
    @Getter
    private Location back;

    public BackHolder(Location back) {
        this.back = back;
    }
}