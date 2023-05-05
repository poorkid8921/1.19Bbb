package bab.bbb;

import bab.bbb.Commands.*;
import bab.bbb.Events.DupeEvent;
import bab.bbb.Events.Dupes.FrameDupe;
import bab.bbb.Events.Dupes.Salc1;
import bab.bbb.Events.misc.*;
import bab.bbb.Events.misc.patches.*;
import bab.bbb.tpa.*;
import bab.bbb.utils.*;
import com.comphenix.protocol.ProtocolLibrary;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.apache.commons.collections4.map.ListOrderedMap;
import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static bab.bbb.utils.ItemUtils.isSpawnEgg;

public final class Bbb extends JavaPlugin implements CommandExecutor, TabExecutor {
    public FileConfiguration config = this.getConfig();
    private static double tps;
    private final ArrayList<TpaRequest> requests = new ArrayList<TpaRequest>();
    private File customConfigFile = new File(getDataFolder(), "data.yml");
    private FileConfiguration customConfigConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    public HashMap<String, OfflinePlayer> nick2Player = new HashMap<String, OfflinePlayer>();
    private static Bbb instance;

    @Override
    public void onEnable() {
        instance = this;

        this.reloadConfig();
        this.saveCustomConfig();
        DataStore.generatePlayerList();

        File homesFolder = new File(getDataFolder(), "homedata");
        if (!homesFolder.exists())
            homesFolder.mkdir();

        Bukkit.getPluginManager().registerEvents(new MiscEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new DupeEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MoveEvents(), this);
        Bukkit.getPluginManager().registerEvents(new AnvilListener(this), this);

        if (this.getConfig().getBoolean("randommotdenabled"))
            Bukkit.getPluginManager().registerEvents(new RandomMotd(), this);

        if (this.getConfig().getBoolean("better-chat"))
            Bukkit.getPluginManager().registerEvents(new BetterChat(), this);

        if (this.getConfig().getBoolean("anti-illegals"))
            Bukkit.getPluginManager().registerEvents(new AntiIllegalsListener(), this);

        if (this.getConfig().getBoolean("salc1-donkey-dupe"))
            Bukkit.getPluginManager().registerEvents(new Salc1(), this);

        if (this.getConfig().getBoolean("auto-restart")) {
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Bbb.getInstance(), () -> {
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.kickPlayer(Methods.translatestring("&cServer Restarting"));
                    Bukkit.getServer().shutdown();
                });
            }, config.getInt("auto-restart-minutes"), TimeUnit.MINUTES);
        }

        if (this.getConfig().getBoolean("item-frame-dupe") && this.getConfig().getInt("item-frame-dupe-rng") > 0)
            Bukkit.getPluginManager().registerEvents(new FrameDupe(), this);

        if (this.getConfig().getBoolean("disable-the-use-of-packet-elytra-fly"))
            Bukkit.getPluginManager().registerEvents(new AntiPacketElytraFly(), this);

        if (this.getConfig().getBoolean("chest-limit-per-chunk"))
            Bukkit.getPluginManager().registerEvents(new ChestLimit(), this);

        if (this.getConfig().getBoolean("anti-burrow"))
            Bukkit.getPluginManager().registerEvents(new AntiBurrow(), this);

        this.getCommand("discord").setExecutor(new EECA());
        if (config.getBoolean("tpa")) {
            this.getCommand("tpa").setExecutor(new TpaCommand(this));
            this.getCommand("tpaccept").setExecutor(new TpacceptCommand(this));
            this.getCommand("tpahere").setExecutor(new TpahereCommand(this));
            this.getCommand("tpdeny").setExecutor(new TpdenyCommand(this));
        }
        if (config.getBoolean("home")) {
            this.getCommand("delhome").setExecutor(new DelHomeCommand());
            this.getCommand("home").setExecutor(new HomeCommand());
            this.getCommand("sethome").setExecutor(new SetHomeCommand());
        }

        tps = getServer().getTPS()[0];
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
                () -> new Thread(() -> tps = getServer().getTPS()[0]).start(), 1, 1, TimeUnit.SECONDS
        );
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Tablist(), 0, 100);
    }

    public static Bbb getInstance() {
        return instance;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        if (cmd.getName().equals("nick")) {
            if (args.length == 0) {
                removeNick(player);
                player.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "INFO" + ChatColor.GRAY + "] your nickname has been removed");
            } else {
                if (player.isOp()) {
                    StringBuilder builder = new StringBuilder();
                    int a = args.length;
                    for (String s : args) {
                        a--;
                        builder.append(s);
                        if (a > 0)
                            builder.append(' ');
                    }
                    String nick = builder.toString();
                    changeNick(player, nick);
                } else
                    changeNick(player, args[0]);
            }
            return true;
        } else if (cmd.getName().equals("msg")) {
            if (args.length == 0) {
                Methods.errormsg((Player) sender, "the arguments are invalid");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Methods.errormsg((Player) sender, "the player is invalid");
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                msgargs.append(args[i]).append(" ");
            }

            if (msgargs.toString().equals("")) {
                Methods.errormsg(player, "the message is invalid");
                return true;
            }

            String b = Bbb.getInstance().getCustomConfig().getString("otherdata." + target.getUniqueId() + ".ignorelist");
            if (b != null && b.contains(player.getName())) {
                Methods.errormsg(player, "you can't send messages to players ignoring you");
                return true;
            }

            String be = Bbb.getInstance().getCustomConfig().getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (be != null && be.contains(target.getName())) {
                Methods.errormsg(player, "you can't send messages to players you are ignoring");
                return true;
            }

            player.sendMessage(Methods.parseText("&7you whisper to " + target.getDisplayName() + "&7: " + msgargs));
            target.sendMessage(Methods.parseText(player, "&7" + player.getDisplayName() + " &7whispers to you: " + msgargs));

            return true;
        } else if (cmd.getName().equals("secure")) {
            if (getCustomConfig().getString("otherdata." + player.getUniqueId() + ".ip") != null) {
                this.getCustomConfig().set("otherdata." + player.getUniqueId() + ".ip", null);
                this.saveCustomConfig();
                Methods.errormsg(player, "your account has been unsecured");
                return true;
            }

            this.getCustomConfig().set("otherdata." + player.getUniqueId() + ".ip", player.getAddress().getAddress().getHostAddress());
            this.saveCustomConfig();
            player.sendMessage(Methods.infostring("&7you have successfully secured your account"));

            return true;
        } else if (cmd.getName().equals("ignore")) {
            String b = this.getCustomConfig().getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (args.length < 1) {
                if (b != null)
                    player.sendMessage(Methods.infostring("your ignored players are: " + b.replace(", ", "&e, &7")));
                Methods.errormsg(player, "the arguments are invalid");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Methods.errormsg(player, "the player is invalid");
                return true;
            }

            if (target.getName().equals(player.getName()))
            {
                Methods.errormsg(player, "you can't ignore yourself");
                return true;
            }

            String breplace = target.getName() + ", ";
            if (b != null) {
                if (b.contains(target.getName())) {
                    this.getCustomConfig().set("otherdata." + player.getUniqueId() + ".ignorelist", b.replace(target.getName() + ", ", ""));
                    this.saveCustomConfig();
                    player.sendMessage(Methods.infostring("successfully un ignored &e" + target.getDisplayName()));
                    return true;
                }

                breplace += b;
            }

            this.getCustomConfig().set("otherdata." + player.getUniqueId() + ".ignorelist", breplace);
            this.saveCustomConfig();
            player.sendMessage(Methods.infostring("successfully ignored &e" + target.getDisplayName()));
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("ignore")) {
            List<String> list = new ArrayList<String>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                list.add(p.getName());
            }
            if (args[0] != null)
                return list.stream().filter(lis -> lis.startsWith(args[0])).collect(Collectors.toList());
            else
                return new ArrayList<>(list);
        }
        return Collections.emptyList();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();

        saveDefaultConfig();
        config = getConfig();
        config.options().copyDefaults(true);
        saveConfig();
    }

    public void setnickonjoin(Player p) {
        String s = this.getCustomConfig().getString("otherdata." + p.getUniqueId() + ".nickname");
        if (s != null) {
            //realname(p, ColorUtils.removeColorCodes(s));

            p.setPlayerListName(Methods.translatestring(s + org.bukkit.ChatColor.GRAY));
            p.setDisplayName(Methods.translatestring(s + org.bukkit.ChatColor.GRAY));

            this.nick2Player.put(p.getName(), p.getPlayer());
        }
    }

    public void changeNick(Player p, String nick) {
        String nickcolor = Methods.translatestring(nick);
        String nickuncolor = ColorUtils.removeColorCodes(nickcolor);
        if(nickuncolor.length() < 3)
            Methods.errormsg(p, "the nickname you entered is too short");
        else if(nickuncolor.length() > 16)
            Methods.errormsg(p, "the nickname you entered is too long");
        else if((nick2Player.containsKey(nickuncolor) && (!nick2Player.get(nickuncolor).getName().equals(p.getName()) || !nick2Player.get(nickuncolor).getPlayer().getDisplayName().equals(p.getDisplayName()))))
            Methods.errormsg(p, "the nickname you entered is already in use");
        else {
            String prevnick = ColorUtils.removeColorCodes(p.getDisplayName());
            p.setDisplayName(nickcolor + ChatColor.GRAY);
            p.setPlayerListName(nickcolor + ChatColor.GRAY);
            nick2Player.remove(prevnick);
            nick2Player.put(nickuncolor, p);

            this.getCustomConfig().set("otherdata." + p.getUniqueId() + ".nickname", nick);
            this.saveCustomConfig();

            //realname(p, nickuncolor);
            p.setDisplayName(nickcolor + ChatColor.GRAY);
            p.setPlayerListName(nickcolor + ChatColor.GRAY);
            p.sendMessage(ChatColor.GRAY + "[" + ChatColor.YELLOW + "INFO" + ChatColor.GRAY + "] " + ChatColor.GRAY + "your nickname has been set to " + nickcolor);
        }
    }

    public void removeNick(Player p) {
        if(p.getName().equals(p.getDisplayName()))
            return;
        String nickuncolor = ChatColor.stripColor(p.getDisplayName());
        removeNick(nickuncolor);
        //realname(p, nickuncolor);

        p.setDisplayName(p.getName());
        p.setPlayerListName(p.getName());
        nick2Player.put(p.getName(), p);
    }

    public void removeNick(String nickuncolor) {
        OfflinePlayer p = nick2Player.get(nickuncolor);
        nick2Player.remove(nickuncolor);
        this.getCustomConfig().set("otherdata." + p.getUniqueId() + ".nickname", null);
        this.saveCustomConfig();
    }

    public void realname(Player p, String name)
    {
        PlayerProfile profile = p.getPlayerProfile();
        profile.setName(ColorUtils.removeColorCodes(p.getName()));
        p.setPlayerProfile(profile);

        for (Player players : Bukkit.getOnlinePlayers())
        {
            players.hidePlayer(p);
            players.showPlayer(p);
        }
    }

    public void reloadCustomConfig() {
        if (customConfigFile == null)
            customConfigFile = new File(getDataFolder(), "data.yml");

        customConfigConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    }

    public FileConfiguration getCustomConfig() {
        if (customConfigConfig == null) {
            this.reloadCustomConfig();
        }
        return customConfigConfig;
    }

    public void saveCustomConfig() {
        if (customConfigConfig == null || customConfigFile == null) {
            return;
        }
        try {
            getCustomConfig().save(customConfigFile);
        } catch (IOException ex) {
            this.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
        }
    }
    public static double getTPSofLastSecond() {
        return tps;
    }
    public static int countMinecartInChunk(Chunk chunk) {
        int count = 0;

        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Minecart) {
                count++;
            }
        }
        return count;
    }

    public static void removeMinecartInChunk(Chunk chunk) {
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof Minecart) {
                entity.remove();
            }
        }
    }

    @Override
    public void onDisable() {
        saveCustomConfig();
        reloadConfig();
    }

    public TpaRequest getRequest (Player user) {
        for (TpaRequest request: requests){
            if (request.getReciever().getName().equalsIgnoreCase(user.getName())) {
                return request;
            }
        }
        return null;
    }

    public void addRequest (Player sender, Player receiver, Type type) {
        TpaRequest tpaRequest = new TpaRequest(sender, receiver, type);
        requests.add(tpaRequest);
    }

    public void removeRequest (Player user) {
        requests.remove(getRequest(user));
    }

    public static void checkInventory(final Inventory inventory, final Location location, final boolean checkRecursive) {
        checkInventory(inventory, location, checkRecursive, false);
    }

    public static void checkInventory(final Inventory inventory, final Location location, final boolean checkRecursive, final boolean isInsideShulker) {
        final List<ItemStack> removeItemStacks = new ArrayList<>();
        final List<ItemStack> bookItemStacks = new ArrayList<>();

        boolean wasFixed = false;
        int fixesIllegals = 0;
        int fixesBooks = 0;

        for (final ItemStack itemStack : inventory.getContents()) {
            switch (checkItemStack(itemStack, location, checkRecursive)) {
                case illegal -> {
                    removeItemStacks.add(itemStack);
                    Bukkit.getServer().getLogger().log(Level.SEVERE, "removed illegal");
                }
                case wasFixed -> wasFixed = true;
            }
        }

        for (final ItemStack itemStack2 : removeItemStacks) {
            itemStack2.setAmount(0);
            inventory.remove(itemStack2);
            ++fixesIllegals;
        }
   }

    public static void checkArmorContents(final PlayerInventory playerInventory, final Location location, final boolean checkRecursive) {
        for (final ItemStack itemStack : playerInventory.getArmorContents()) {
            checkItemStack(itemStack, location, checkRecursive);
        }
    }

    public boolean hasIllegalNBT(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            return meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES) || meta.hasItemFlag(ItemFlag.HIDE_DESTROYS)
                    || meta.hasItemFlag(ItemFlag.HIDE_ENCHANTS) || meta.hasItemFlag(ItemFlag.HIDE_PLACED_ON)
                    || meta.hasItemFlag(ItemFlag.HIDE_POTION_EFFECTS) || meta.hasItemFlag(ItemFlag.HIDE_UNBREAKABLE)
                    || meta.hasLore() || meta.hasAttributeModifiers() || meta.hasItemFlag(ItemFlag.HIDE_DYE);
        }
        return false;
    }

    public static ItemState checkItemStack(ItemStack itemStack, final Location location, final boolean checkRecursive) {
        boolean wasFixed = false;

        if (itemStack == null)
            return ItemState.empty;

        if (!getInstance().getConfig().getBoolean("anti-illegals"))
            return ItemState.clean;

        if (getInstance().getConfig().getBoolean ("illegal-items")) {
            if (itemStack.getType() == Material.BEDROCK) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (itemStack.getType() == Material.END_PORTAL_FRAME) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (itemStack.getType() == Material.BARRIER) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (itemStack.getType() == Material.LIGHT) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (itemStack.getType() == Material.PLAYER_HEAD) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (itemStack.getType() == Material.COMMAND_BLOCK) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (itemStack.getType() == Material.CHAIN_COMMAND_BLOCK) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (itemStack.getType() == Material.REPEATING_COMMAND_BLOCK) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (isSpawnEgg(itemStack)) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (itemStack.getType() == Material.SPAWNER) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (getInstance().hasIllegalNBT(itemStack)) {
                itemStack.setAmount(0);
                return ItemState.illegal;
            }

            if (itemStack.getItemMeta().isUnbreakable() || itemStack.getDurability() < 0 || itemStack.getDurability() > 2031)
                itemStack.setDurability(itemStack.getType().getMaxDurability());
        }

        if (getInstance().getConfig().getBoolean("overstacked-items")) {
            if (itemStack.getAmount() > itemStack.getMaxStackSize()) {
                itemStack.setAmount(itemStack.getMaxStackSize());
                wasFixed = true;
            }
        }

        if (getInstance().getConfig().getBoolean("illegal-enchants")) {
            final List<Enchantment> keys = new ArrayList<>(itemStack.getEnchantments().keySet());
            Collections.shuffle(keys);

            for (int kI1 = 0; kI1 < keys.size(); ++kI1) {
                for (int kI2 = kI1 + 1; kI2 < keys.size(); ++kI2) {
                    final Enchantment e1 = keys.get(kI1);

                    if (e1.conflictsWith(keys.get(kI2))) {
                        itemStack.removeEnchantment(e1);
                        keys.remove(e1);

                        if (kI1 > 0) {
                            --kI1;
                            break;
                        }
                    }
                }
            }

            if (itemStack.getType() == Material.POTION || itemStack.getType() == Material.SPLASH_POTION || itemStack.getType() == Material.LINGERING_POTION) {
                PotionMeta meta = (PotionMeta) itemStack.getItemMeta();

                if (meta.getCustomEffects().size() > 0) {
                    meta.clearCustomEffects();
                    itemStack.setItemMeta(meta);
                    wasFixed = true;
                }
            }

            for (final Enchantment enchantment : itemStack.getEnchantments().keySet()) {
                if (itemStack.getType() == Material.TOTEM_OF_UNDYING)
                    itemStack.removeEnchantment(enchantment);

                if (enchantment.canEnchantItem(itemStack)) {
                    if (itemStack.getEnchantmentLevel(enchantment) > enchantment.getMaxLevel()) {
                        wasFixed = true;
                        itemStack.removeEnchantment(enchantment);
                        itemStack.addUnsafeEnchantment(enchantment, enchantment.getMaxLevel());
                    } else if (itemStack.getEnchantmentLevel(enchantment) < 1) {
                        wasFixed = true;
                        itemStack.removeEnchantment(enchantment);
                        itemStack.addUnsafeEnchantment(enchantment, 1);
                    }
                } else {
                    wasFixed = true;
                    itemStack.removeEnchantment(enchantment);
                }
            }
        }

        if (getInstance().getConfig().getBoolean("check-in-shulker-box")) {
            if (itemStack.getType().toString().contains("SHULKER_BOX") && checkRecursive && itemStack.getItemMeta() instanceof BlockStateMeta) {
                final BlockStateMeta blockMeta = (BlockStateMeta) itemStack.getItemMeta();

                if (blockMeta.getBlockState() instanceof ShulkerBox) {
                    final ShulkerBox shulker = (ShulkerBox) blockMeta.getBlockState();
                    final Inventory inventoryShulker = shulker.getInventory();

                    checkInventory(inventoryShulker, location, true, true);
                    shulker.getInventory().setContents(inventoryShulker.getContents());
                    blockMeta.setBlockState(shulker);

                    try {
                        itemStack.setItemMeta(blockMeta);
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        return wasFixed ? ItemState.wasFixed : ItemState.clean;
    }

    public enum ItemState {empty, clean, wasFixed, illegal}
}
