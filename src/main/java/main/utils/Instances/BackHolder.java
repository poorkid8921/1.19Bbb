package main.utils.Instances;

import lombok.Getter;
import lombok.Setter;

public class BackHolder {
    @Setter
    @Getter
    private WorldLocationHolder back;

    public BackHolder(WorldLocationHolder back) {
        this.back = back;
    }
}