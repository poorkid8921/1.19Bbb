package org.yuri.aestheticnetwork.commands.parties;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Party {
    private final UUID owner;
    private final ArrayList<UUID> members = new ArrayList<>();
    private final String name;
    private final ArrayList<String> membername = new ArrayList<>();

    public Party(UUID owner, String name) {
        this.owner = owner;
        this.name = name;
    }

    public String getName() {
        return name;
    }
    public UUID getOwner() {
        return owner;
    }
    public ArrayList<UUID> getMembers() {
        return members;
    }
    public List<String> getMembersStr() {
        return membername;
    }
    public void addMember(UUID p,
                          String pname) {
        membername.add(pname);
        members.add(p);
    }

    public void kickMember(UUID p,
                           String pname) {
        membername.remove(pname);
        members.remove(p);
    }
}
