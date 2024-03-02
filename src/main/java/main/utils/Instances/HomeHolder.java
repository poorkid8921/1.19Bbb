package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public class HomeHolder {
    private String name;
    private Location location;

    public HomeHolder(String name,
                      Location location) {
        this.name = name;
        this.location = location;
    }
}
