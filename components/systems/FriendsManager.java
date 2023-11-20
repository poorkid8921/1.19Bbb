package net.onyx.client.components.systems;

import net.minecraft.entity.player.PlayerEntity;
import net.onyx.client.OnyxClient;
import net.onyx.client.utils.ServerUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FriendsManager {
    public static class Friend {
        private final UUID uuid;
        private String username = null;

        public Friend(String uuid) {
            this.uuid = UUID.fromString(uuid);
        }

        public Friend(UUID uuid) {
            this.uuid = uuid;
        }

        public Friend(PlayerEntity player) {
            this.uuid = player.getUuid();
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public boolean isOnline() {
            return OnyxClient.getClient().world.getPlayerByUuid(this.uuid) != null;
        }

        public String getUsernameOrUuid() {
            String val = this.getUsername();

            return val == null ? this.getUuid().toString() : val;
        }

        public String getUsername() {
            PlayerEntity player = OnyxClient.getClient().world.getPlayerByUuid(this.uuid);
            if (player != null) {
                username = player.getDisplayName().getString();
            }
            
            return username;
        }

        public static Friend fromUsername(String username) {
            PlayerEntity target = ServerUtils.getPlayerByName(username);
            if (target == null) return null;

            return new Friend(target.getUuid());
        }

        private static UUID extractUUID(String uuid) {
            UUID realUuid = null;
        
            try {
                realUuid = UUID.fromString(uuid);
            } catch (IllegalArgumentException exception) {
                // ...
            }

            return realUuid;
        }

        public static Friend fromMiscString(String input) {
            // See if it is a UUID
            Friend target = null;

            UUID targetUuid = extractUUID(input);
            if (targetUuid != null) {
                target = new Friend(targetUuid);
            } else {
                target = Friend.fromUsername(input);
            }

            return target;
        } 
    }

    private final HashMap<UUID, Friend> friendList = new HashMap<UUID, Friend>();
    
    public boolean onFriendList(PlayerEntity entity) {
        return this.onFriendList(entity.getUuid());
    }

    public boolean onFriendList(Friend friend) {
        return this.onFriendList(friend.getUuid());
    }

    public boolean onFriendList(UUID uuid) {
        return this.friendList.get(uuid) != null;
    }

    public List<Friend> getFriends() {
        List<Friend> friends = new ArrayList<Friend>();
        
        for (Friend friend : friendList.values()) {
            friends.add(friend);
        }

        return friends;
    }

    public boolean addFriend(Friend friend) {
        // Check if the player is on the list
        if (this.onFriendList(friend)) {
            return false;
        }

        // Add new player
        this.friendList.put(friend.getUuid(), friend);
        return true;
    }

    public boolean removeFriend(Friend friend) {
        // Check if the player is on the list
        if (!this.onFriendList(friend)) {
            return false;
        }

        // Add new player
        this.friendList.remove(friend.getUuid());
        return true;
    }

    public int totalFriends() {
        return this.friendList.size();
    }

    public FriendsManager() {
        
    }
}
