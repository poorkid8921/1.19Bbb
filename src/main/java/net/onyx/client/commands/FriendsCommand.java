package net.onyx.client.commands;

import net.onyx.client.commands.structures.Command;
import net.onyx.client.commands.structures.CommandNode;
import net.onyx.client.components.systems.FriendsManager;
import net.onyx.client.utils.ChatUtils;

import java.util.List;

public class FriendsCommand extends CommandNode {
    private static class FriendsSubCommand extends Command {
        FriendsManager manager;

        public FriendsSubCommand(String command, String helpText, String description) {
            super(command, helpText, description);
            
            this.commandDisplay = "FriendsList";
        }
        
        public FriendsManager.Friend findFriendWithMessages(String input) {
            FriendsManager.Friend target = FriendsManager.Friend.fromMiscString(input);

            if (target == null) {
                this.displayChatMessage(String.format("%sUnable to find player '%s', maybe try again when the player is online or use their UUID.", ChatUtils.RED, input));
            }

            return target;
        }

        public void action(FriendsManager.Friend target) {
            // Something...
        }

        @Override
        public Boolean trigger(String[] args) {
            if (args.length == 0) return this.handleHelp(args);

            FriendsManager.Friend target = this.findFriendWithMessages(args[0]);
            if (target != null) this.action(target);

            return true;
        }
    }
    
    private static class addCommand extends FriendsSubCommand {
        public addCommand(FriendsManager manager) {
            super("add", "add <name|uuid>", "Add a player to your friends list");

            this.manager = manager;
        }

        @Override
        public void action(FriendsManager.Friend target) {
            boolean success = this.manager.addFriend(target);
            
            if (success) {
                this.displayChatMessage(String.format("Successfully added '%s' to your friends list.", target.getUsernameOrUuid()));
            } else {
                this.displayChatMessage(String.format("%sUnable to add '%s' to your friends list, check that they are not already on it.", ChatUtils.RED, target.getUsernameOrUuid()));
            }
        }
    }
    private static class removeCommand extends FriendsSubCommand {

        public removeCommand(FriendsManager manager) {
            super("remove", "remove <name|uuid>", "Remove a player from your friends list");

            this.manager = manager;
        }

        @Override
        public void action(FriendsManager.Friend target) {
            boolean success = this.manager.removeFriend(target);

            if (success) {
                this.displayChatMessage(String.format("Successfully removed %s from your friends list.", target.getUsernameOrUuid()));
            } else {
                this.displayChatMessage(String.format("%sUnable to remove player '%s', make sure that they are on your friends list.", ChatUtils.RED, target.getUsernameOrUuid()));
            }
        }
    }
    private static class listCommand extends FriendsSubCommand {
        public listCommand(FriendsManager manager) {
            super("list", "list", "Remove a player from your friends list");

            this.manager = manager;
        }

        @Override
        public Boolean trigger(String[] args) {
            List<FriendsManager.Friend> friends = manager.getFriends();

            if (friends.size() == 0) {
                this.displayChatMessage("You currently have no friends.");
                return true;
            }

            this.displayChatMessage("Friends List:");
            for (FriendsManager.Friend friend : friends) {
                String username = friend.getUsername();
                username = username == null ? friend.getUuid().toString() : username;

                String message;
                // TODO this is inefficient as anything, please change this so that it looks up the player in a HashMap or something.
                if (!friend.isOnline()) {
                    message = String.format("[%sOFFLINE%s] %s%s", ChatUtils.RED, ChatUtils.WHITE, ChatUtils.RED, username);
                } else {
                    message = String.format("[%sONLINE%s] %s%s", ChatUtils.GREEN, ChatUtils.WHITE, ChatUtils.GREEN, username);
                }

                this.displayChatMessage(String.format("-> %s", message));
            }

            return true;
        }
    }

    public FriendsCommand(FriendsManager manager) {
        super("friends", "A Simple friend management system");

        this.addSubCommand(new listCommand(manager));
        this.addSubCommand(new addCommand(manager));
        this.addSubCommand(new removeCommand(manager));
    }
    
}
