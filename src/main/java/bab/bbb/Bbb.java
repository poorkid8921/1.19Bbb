package bab.bbb;

import bab.bbb.Commands.*;
import bab.bbb.Events.DupeEvent;
import bab.bbb.Events.Dupes.DonkeyDupe;
import bab.bbb.Events.Dupes.FrameDupe;
import bab.bbb.Events.misc.*;
import bab.bbb.Events.misc.patches.AntiBurrow;
import bab.bbb.Events.misc.patches.AntiIllegalsListener;
import bab.bbb.Events.misc.patches.AntiPacketElytraFly;
import bab.bbb.Events.misc.patches.ChestLimit;
import bab.bbb.tpa.*;
import bab.bbb.utils.Home;
import bab.bbb.utils.Utils;
import bab.bbb.utils.Tablist;
import bab.bbb.utils.Type;
import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@SuppressWarnings("deprecation")
public final class Bbb extends JavaPlugin implements CommandExecutor, TabExecutor {
    public FileConfiguration config = this.getConfig();
    private final ArrayList<TpaRequest> requests = new ArrayList<>();
    private File customConfigFile = new File(getDataFolder(), "data.yml");
    private FileConfiguration customConfigConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    public static HashMap<UUID, UUID> lastReceived = new HashMap<>();
    private static Bbb instance;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.saveCustomConfig();
        Utils.generatePlayerList();

        File homesFolder = new File(getDataFolder(), "homedata");
        if (!homesFolder.exists())
            homesFolder.mkdir();

        Bukkit.getPluginManager().registerEvents(new MiscEvents(this), this);
        Bukkit.getPluginManager().registerEvents(new DupeEvent(), this);
        Bukkit.getPluginManager().registerEvents(new MoveEvents(), this);
        Bukkit.getPluginManager().registerEvents(new AnvilListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChestLimit(), this);
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new Discord());

        if (this.getConfig().getBoolean("random-motd"))
            Bukkit.getPluginManager().registerEvents(new RandomMotd(), this);
        if (this.getConfig().getBoolean("better-chat"))
            Bukkit.getPluginManager().registerEvents(new BetterChat(), this);
        if (this.getConfig().getBoolean("anti-illegals"))
            Bukkit.getPluginManager().registerEvents(new AntiIllegalsListener(), this);
        if (this.getConfig().getBoolean("item-frame-dupe") && this.getConfig().getInt("item-frame-dupe-rng") > 0)
            Bukkit.getPluginManager().registerEvents(new FrameDupe(), this);
        if (this.getConfig().getBoolean("disable-the-use-of-packet-elytra-fly"))
            Bukkit.getPluginManager().registerEvents(new AntiPacketElytraFly(), this);
        if (this.getConfig().getBoolean("anti-burrow"))
            Bukkit.getPluginManager().registerEvents(new AntiBurrow(), this);
        if (config.getBoolean("tpa")) {
            Objects.requireNonNull(this.getCommand("tpa")).setExecutor(new TpaCommand(this));
            Objects.requireNonNull(this.getCommand("tpaccept")).setExecutor(new TpacceptCommand(this));
            Objects.requireNonNull(this.getCommand("tpahere")).setExecutor(new TpahereCommand(this));
            Objects.requireNonNull(this.getCommand("tpdeny")).setExecutor(new TpdenyCommand(this));
        }
        if (config.getBoolean("home")) {
            Objects.requireNonNull(this.getCommand("delhome")).setExecutor(new DelHomeCommand());
            Objects.requireNonNull(this.getCommand("home")).setExecutor(new HomeCommand());
            Objects.requireNonNull(this.getCommand("sethome")).setExecutor(new SetHomeCommand());
        }

        Bukkit.getPluginManager().registerEvents(new DonkeyDupe(), this);
        Bukkit.getScheduler().runTaskTimer(this, new Tablist(), 0, 100);

        if (this.getConfig().getBoolean("auto-restart")) {
            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Bbb.getInstance(), () -> {
                    for (Player player : Bukkit.getOnlinePlayers())
                        player.kickPlayer(Utils.translate("&7Server Restarting"));
                    Bukkit.getServer().shutdown();
                });
            }, config.getInt("auto-restart-minutes"), TimeUnit.MINUTES);
        }

        if (this.getCustomConfig().get("otherdata.nicknames") == null)
        {
            this.getCustomConfig().set("otherdata.nicknames", "");
            this.saveCustomConfig();
        }
    }

    public static Bbb getInstance() {
        return instance;
    }

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player))
            return true;

        if (cmd.getName().equals("nick")) {
            if (args.length == 0) {
                removeNick(player);
                Utils.infomsg(player, "Your nickname has been removed");
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
                Utils.errormsgs(player, 1, "");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Utils.errormsgs(player, 2, args[0]);
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                msgargs.append(args[i]).append(" ");

            if (msgargs.toString().equals("")) {
                Utils.errormsgs(player, 1, "");
                return true;
            }

            String b = Utils.getString("otherdata." + target.getUniqueId() + ".ignorelist");
            if (b != null && b.contains(player.getName())) {
                Utils.errormsgs(player, 4, "");
                return true;
            }

            String be = Utils.getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (b != null && be.contains(target.getName())) {
                Utils.errormsgs(player, 5, "");
                return true;
            }

            player.sendMessage(Utils.translate(player, "&7you whisper to " + target.getDisplayName() + "&7: " + msgargs));
            target.sendMessage(Utils.translate(target, "&7" + player.getDisplayName() + " &7whispers to you: " + msgargs));
            lastReceived.put(player.getUniqueId(), target.getUniqueId());
            lastReceived.put(target.getUniqueId(), player.getUniqueId());

            return true;
        } else if (cmd.getName().equals("reply")) {
            if (args.length == 0) {
                Utils.errormsgs(player, 1, "");
                return true;
            }
            Player target = Bukkit.getPlayer(lastReceived.get(player.getUniqueId()));
            if (target == null || !lastReceived.containsKey(player.getUniqueId()) || lastReceived.get(player.getUniqueId()) == null) {
                Utils.errormsgs(player, 6, "");
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (String arg : args) msgargs.append(arg).append(" ");

            if (msgargs.toString().equals("")) {
                Utils.errormsgs(player, 1, "");
                return true;
            }

            String ignoreclient = Utils.getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (ignoreclient != null && ignoreclient.contains(target.getName())) {
                Utils.errormsgs(player, 5, "");
                return true;
            }

            String ignoretarget = Utils.getString("otherdata." + target.getUniqueId() + ".ignorelist");
            if (ignoretarget != null && ignoretarget.contains(player.getName())) {
                Utils.errormsgs(player, 4, "");
                return true;
            }

            player.sendMessage(Utils.translate(player, "&7you reply to " + target.getDisplayName() + "&7: " + msgargs));
            target.sendMessage(Utils.translate(target, "&7" + player.getDisplayName() + " &7whispers to you: " + msgargs));
            lastReceived.put(player.getUniqueId(), target.getUniqueId());
            lastReceived.put(target.getUniqueId(), player.getUniqueId());

            return true;
        } else if (cmd.getName().equals("secure")) {
            if (Utils.getString("otherdata." + player.getUniqueId() + ".secure") != null) {
                Utils.setData("otherdata." + player.getUniqueId() + ".secure", "");
                Utils.saveData();
                Utils.infomsg(player, "Your account has been unsecured");
                return true;
            }

            Utils.setData("otherdata." + player.getUniqueId() + ".secure", Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
            Utils.saveData();
            Utils.infomsg(player, "You have successfully secured your account");
            return true;
        } else if (cmd.getName().equals("ignore")) {
            String ignoreclient = Utils.getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (args.length < 1) {
                if (ignoreclient != null) {
                    Utils.infomsg(player, "Your ignored players are: " + ignoreclient.replace(", ", "&e, &7"));
                    return true;
                }
                Utils.errormsgs(player, 1, "");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Utils.errormsgs(player, 2, "");
                return true;
            }

            if (target.getName().equals(player.getName())) {
                Utils.errormsgs(player, 8, "");
                return true;
            }

            String breplace = target.getName() + ", ";

            if (ignoreclient != null && ignoreclient.contains(target.getName())) {
                Utils.setData("otherdata." + player.getUniqueId() + ".ignorelist", ignoreclient.replace(target.getName() + ", ", ""));
                Utils.saveData();
                Utils.infomsg(player, "Successfully un ignored &e" + target.getDisplayName());
                return true;
            }

            breplace += ignoreclient;
            Utils.setData("otherdata." + player.getUniqueId() + ".ignorelist", breplace);
            Utils.saveData();
            Utils.infomsg(player, "Successfully ignored &e" + target.getDisplayName());
            return true;
        } else if (cmd.getName().equals("kill")) {
            player.setHealth(0);
            return true;
        }

        return false;
    }

    public void setnickonjoin(Player p) {
        String s = Utils.getString("otherdata." + p.getUniqueId() + ".nickname");
        if (s != null) {
            String str = s;
            //realname(p, ColorUtils.removeColorCodes(s));

            if (Utils.getString("otherdata." + p.getUniqueId() + ".prefix") != null)
                str = Utils.getString("otherdata." + p.getUniqueId() + ".prefix") + " " + str;

            p.setPlayerListName(Utils.translate(str + ChatColor.GRAY));
            p.setDisplayName(Utils.translate(str + ChatColor.GRAY));
        }
    }

    public void changeNick(Player p, String nick) {
        String nickcolor = Utils.translate(nick);
        String nickuncolor = Utils.removeColorCodes(nickcolor);

        if (p.isOp()) {
            p.setDisplayName(nickcolor + ChatColor.GRAY);
            p.setPlayerListName(nickcolor + ChatColor.GRAY);

            Utils.setData("otherdata." + p.getUniqueId() + ".nickname", nick);
            Utils.saveData();

            //realname(p, nickuncolor);
            p.setDisplayName(nickcolor + ChatColor.GRAY);
            p.setPlayerListName(nickcolor + ChatColor.GRAY);
            Utils.infomsg(p, "&e[&4OP&e]&7 Your nickname has been set to &e" + nickcolor);
            return;
        }

        String strr = this.getCustomConfig().getString("otherdata.nicknames");
        if (strr == null)
            return;

        boolean inuse = Utils.isduplicated(strr, nickuncolor) && Utils.isduplicatedname(strr, nickuncolor);

        if (nickuncolor.length() < 3)
            Utils.errormsgs(p, 9, "");
        else if (nickuncolor.length() > 16)
            Utils.errormsgs(p, 10, "");
        else if (nickuncolor.contains("[") || nickuncolor.contains("]") || nickuncolor.contains("!") || nickuncolor.contains("@") || nickuncolor.contains("#") || nickuncolor.contains("$") || nickuncolor.contains("%") || nickuncolor.contains("*"))
            Utils.errormsgs(p, 11, "");
        else if (inuse)
            Utils.errormsgs(p, 12, "");
        else {
            String prevnick = Utils.removeColorCodes(p.getDisplayName());
            if (Utils.getString("otherdata." + p.getUniqueId() + ".prefix") != null) {
                nick = Utils.getString("otherdata." + p.getUniqueId() + ".prefix") + " " + nick;
                nickuncolor = Utils.getString("otherdata." + p.getUniqueId() + ".prefix") + " " + nickuncolor;
            }
            String str = strr.replace("_" + prevnick, "_" + nickuncolor).replace("_" + p.getName(), "");

            Utils.setData("otherdata." + p.getUniqueId() + ".nickname", nick);
            Utils.setData("otherdata.nicknames", str);
            Utils.saveData();

            //realname(p, nickuncolor);
            p.setDisplayName(nickcolor + ChatColor.GRAY);
            p.setPlayerListName(nickcolor + ChatColor.GRAY);
            Utils.infomsg(p, "Your nickname has been set to &e" + nickcolor);
        }
    }

    public void removeNick(Player p) {
        if (p.getName().equals(p.getDisplayName()))
            return;
        String strr = Objects.requireNonNull(Utils.getString("otherdata.nicknames")).replace("_" + Utils.removeColorCodes(Objects.requireNonNull(p.getPlayer()).getDisplayName()), "_" + p.getPlayer().getName());
        //realname(p, nickuncolor);

        p.setDisplayName(p.getName());
        p.setPlayerListName(p.getName());
        Utils.setData("otherdata.nicknames", strr);
        Utils.saveData();
    }

    public void realname(Player p, String name) {
        PlayerProfile profile = p.getPlayerProfile();
        profile.setName(Utils.removeColorCodes(p.getName()));
        p.setPlayerProfile(profile);

        for (Player players : Bukkit.getOnlinePlayers()) {
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
        return Bukkit.getServer().getTPS()[0];
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

    public TpaRequest getRequest(Player user) {
        for (TpaRequest request : requests) {
            if (request.getReciever().getName().equalsIgnoreCase(user.getName())) {
                return request;
            }
        }
        return null;
    }

    public void addRequest(Player sender, Player receiver, Type type) {
        TpaRequest tpaRequest = new TpaRequest(sender, receiver, type);
        requests.add(tpaRequest);
    }

    public void removeRequest(Player user) {
        requests.remove(getRequest(user));
    }

    public static void checkInventory(final Inventory inventory, final Location location, final boolean checkRecursive) {
        checkInventory(inventory, location, checkRecursive, false);
    }

    public static void checkInventory(final Inventory inventory, final Location location, final boolean checkRecursive, final boolean isInsideShulker) {
        final List<ItemStack> removeItemStacks = new ArrayList<>();

        boolean wasFixed = false;
        int fixesIllegals = 0;
        int fixesBooks = 0;

        for (final ItemStack itemStack : inventory.getContents()) {
            switch (checkItemStack(itemStack, location, checkRecursive)) {
                case illegal -> {
                    removeItemStacks.add(itemStack);
                    Bukkit.getServer().getLogger().info("removed illegal");
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
                    || meta.hasAttributeModifiers() || meta.hasItemFlag(ItemFlag.HIDE_DYE);
        }
        return false;
    }

    public static ItemState checkItemStack(ItemStack itemStack, final Location location, final boolean checkRecursive) {
        boolean wasFixed = false;

        if (itemStack == null)
            return ItemState.empty;

        if (!getInstance().getConfig().getBoolean("anti-illegals"))
            return ItemState.clean;

        if (Utils.isBook(itemStack)) {
            BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
            List<String> pages = new ArrayList<>();

            for (String page : bookMeta.getPages()) {
                if (page.getBytes(StandardCharsets.UTF_8).length <= 255)
                    pages.add(page);
            }

            bookMeta.setPages(pages);
            itemStack.setItemMeta(bookMeta);
        }

        if (getInstance().getConfig().getBoolean("illegal-items")) {
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

            if (Utils.isSpawnEgg(itemStack)) {
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

            if (itemStack.getItemMeta().hasLore()) {
                itemStack.getItemMeta().setLore(null);
                return ItemState.illegal;
            }

            if (itemStack.getItemMeta().isUnbreakable() || itemStack.getDurability() < 0 || itemStack.getDurability() > 2031) {
                itemStack.setDurability(itemStack.getType().getMaxDurability());
                return ItemState.illegal;
            }
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

                if (meta.hasCustomEffects()) {
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
            if (itemStack.getType().toString().contains("SHULKER_BOX") && checkRecursive && itemStack.getItemMeta() instanceof final BlockStateMeta blockMeta) {
                if (blockMeta.getBlockState() instanceof final ShulkerBox shulker) {
                    final Inventory inventoryShulker = shulker.getInventory();

                    for (int i = 0; i < inventoryShulker.getSize(); i++)
                    {
                        ItemStack a = inventoryShulker.getItem(i);
                        if (a == null)
                            continue;

                        if (a.getType().toString().contains("SHULKER_BOX"))
                            a.setAmount(0);
                    }

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