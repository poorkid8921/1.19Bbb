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
import java.util.stream.Collectors;

@SuppressWarnings("deprecation")
public final class Bbb extends JavaPlugin implements CommandExecutor, TabExecutor {
    public FileConfiguration config = this.getConfig();
    private final ArrayList<TpaRequest> requests = new ArrayList<>();
    private File customConfigFile = new File(getDataFolder(), "data.yml");
    private FileConfiguration customConfigConfig = YamlConfiguration.loadConfiguration(customConfigFile);
    public static HashMap<UUID, UUID> lastReceived = new HashMap<>();
    private static Bbb instance;
    public final HashSet<String> linkRegexes = new HashSet<>();
    public final HashSet<String> allowedCommands = new HashSet<>();
    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();
        this.saveCustomConfig();
        Utils.generatePlayerList();

        File homesFolder = new File(getDataFolder(), "homedata");
        if (!homesFolder.exists())
            homesFolder.mkdir();

        linkRegexes.addAll(Arrays.asList(
                "(https?://(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?://(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})",
                "[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z()]{1,6}\\b([-a-zA-Z()@:%_+.~#?&/=]*)"
        ));

        List<String> configuredAllowedCommands = Arrays.asList(
                "help", "d", "discord", "home", "sethome", "delhome", "reply", "r", "msg", "tell", "whisper", "tpa", "tpahere", "tpaccept", "tpno", "tpn", "tpy", "tpdeny", "tpyes", "nick", "nickname", "reg", "secure", "suicide", "kill", "ignore"
        );
        for (String configuredAllowedCmd : configuredAllowedCommands)
                allowedCommands.add(configuredAllowedCmd.toLowerCase());

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
                        player.kickPlayer(Utils.parseText("&7Server Restarting"));
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

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("home")) {
            Player player = (Player) sender;
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) return Collections.emptyList();
            if (args.length < 1)
                return homes.stream().map(Home::getName).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
            else
                return homes.stream().map(Home::getName).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
        } else if (cmd.getName().equalsIgnoreCase("delhome")) {
            Player player = (Player) sender;
            List<Home> homes = Utils.getHomes().getOrDefault(player.getUniqueId(), null);
            if (homes == null) return Collections.emptyList();
            if (args.length < 1)
                return homes.stream().map(Home::getName).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
            else
                return homes.stream().map(Home::getName).filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase())).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
        } else {
            if (args.length > 1) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase())).collect(Collectors.toList());
            } else return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        Player player = (Player) sender;

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
                Utils.errormsg(player, "The arguments are invalid");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Utils.errormsg(player, "The player is invalid");
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (int i = 1; i < args.length; i++)
                msgargs.append(args[i]).append(" ");

            if (msgargs.toString().equals("")) {
                Utils.errormsg(player, "The message is invalid");
                return true;
            }

            String b = Utils.getString("otherdata." + target.getUniqueId() + ".ignorelist");
            if (b != null && b.contains(player.getName())) {
                Utils.errormsg(player, "You can't send messages to players ignoring you");
                return true;
            }

            String be = Utils.getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (be != null && be.contains(target.getName())) {
                Utils.errormsg(player, "You can't send messages to players you are ignoring");
                return true;
            }

            player.sendMessage(Utils.parseText(player, "&7you whisper to " + target.getDisplayName() + "&7: " + msgargs));
            target.sendMessage(Utils.parseText(target, "&7" + player.getDisplayName() + " &7whispers to you: " + msgargs));
            lastReceived.put(player.getUniqueId(), target.getUniqueId());
            lastReceived.put(target.getUniqueId(), player.getUniqueId());

            return true;
        } else if (cmd.getName().equals("reply")) {
            if (args.length == 0) {
                Utils.errormsg(player, "The arguments are invalid");
                return true;
            }
            Player target = Bukkit.getPlayer(lastReceived.get(player.getUniqueId()));
            if (target == null || !lastReceived.containsKey(player.getUniqueId()) || lastReceived.get(player.getUniqueId()) == null) {
                Utils.errormsg(player, "You have no one to reply to");
                return true;
            }

            StringBuilder msgargs = new StringBuilder();

            for (String arg : args) msgargs.append(arg).append(" ");

            if (msgargs.toString().equals("")) {
                Utils.errormsg(player, "The message is invalid");
                return true;
            }

            String b = Utils.getString("otherdata." + target.getUniqueId() + ".ignorelist");
            if (b != null && b.contains(player.getName())) {
                Utils.errormsg(player, "You can't send messages to players ignoring you");
                return true;
            }

            String be = Utils.getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (be != null && be.contains(target.getName())) {
                Utils.errormsg(player, "You can't send messages to players you are ignoring");
                return true;
            }

            player.sendMessage(Utils.parseText(player, "&7you reply to " + target.getDisplayName() + "&7: " + msgargs));
            target.sendMessage(Utils.parseText(target, "&7" + player.getDisplayName() + " &7whispers to you: " + msgargs));
            lastReceived.put(player.getUniqueId(), target.getUniqueId());
            lastReceived.put(target.getUniqueId(), player.getUniqueId());

            return true;
        } else if (cmd.getName().equals("secure")) {
            if (getCustomConfig().getString("otherdata." + player.getUniqueId() + ".ip") != null) {
                this.getCustomConfig().set("otherdata." + player.getUniqueId() + ".ip", null);
                this.saveCustomConfig();
                Utils.errormsg(player, "Your account has been unsecured");
                return true;
            }

            Utils.setData("otherdata." + player.getUniqueId() + ".ip", Objects.requireNonNull(player.getAddress()).getAddress().getHostAddress());
            Utils.saveData();
            Utils.infomsg(player, "You have successfully secured your account");

            return true;
        } else if (cmd.getName().equals("ignore")) {
            String b = Utils.getString("otherdata." + player.getUniqueId() + ".ignorelist");
            if (args.length < 1) {
                if (b != null) {
                    Utils.infomsg(player, "Your ignored players are: " + b.replace(", ", "&e, &7"));
                    return true;
                }
                Utils.errormsg(player, "The arguments are invalid");
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                Utils.errormsg(player, "The player is invalid");
                return true;
            }

            if (target.getName().equals(player.getName())) {
                Utils.errormsg(player, "You can't ignore yourself");
                return true;
            }

            String breplace = target.getName() + ", ";
            if (b != null) {
                if (b.contains(target.getName())) {
                    Utils.setData("otherdata." + player.getUniqueId() + ".ignorelist", b.replace(target.getName() + ", ", ""));
                    Utils.saveData();
                    Utils.infomsg(player, "Successfully un ignored &e" + target.getDisplayName());
                    return true;
                }

                breplace += b;
            }

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
            //realname(p, ColorUtils.removeColorCodes(s));

            p.setPlayerListName(Utils.parseText(s + ChatColor.GRAY));
            p.setDisplayName(Utils.parseText(s + ChatColor.GRAY));
        }
    }

    public void changeNick(Player p, String nick) {
        String nickcolor = Utils.parseText(nick);
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
        boolean inuse = Utils.isduplicated(strr, nickuncolor);

        if (nickuncolor.length() < 3)
            Utils.errormsg(p, "The nickname you entered is too short");
        else if (nickuncolor.length() > 16)
            Utils.errormsg(p, "The nickname you entered is too long");
        else if (nickuncolor.contains("[") || nickuncolor.contains("]") || nickuncolor.contains("!") || nickuncolor.contains("@") || nickuncolor.contains("#") || nickuncolor.contains("$") || nickuncolor.contains("%") || nickuncolor.contains("*"))
            Utils.errormsg(p, "The nickname you entered is invalid");
        else if (inuse)
            Utils.errormsg(p, "The nickname you entered is already in use");
        else {
            String prevnick = Utils.removeColorCodes(p.getDisplayName());
            String str = strr.replace("_" + prevnick, "_" + nickuncolor).replace("_" + p.getName(), "");

            p.setDisplayName(nickcolor + ChatColor.GRAY);
            p.setPlayerListName(nickcolor + ChatColor.GRAY);

            Utils.setData("otherdata." + p.getUniqueId() + ".nickname", nick);
            Utils.setData("otherdata.nicknames", str);
            Utils.saveData();

            //realname(p, nickuncolor);
            p.setDisplayName(nickcolor + ChatColor.GRAY);
            p.setPlayerListName(nickcolor + ChatColor.GRAY);
            Utils.infomsg(p, "Your nickname has been set to " + nickcolor);
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