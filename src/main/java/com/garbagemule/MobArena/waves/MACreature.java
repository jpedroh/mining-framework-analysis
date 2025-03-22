package com.garbagemule.MobArena.waves;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Hoglin;
import org.bukkit.entity.PiglinAbstract;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MACreature {
  private static final Map<String, MACreature> map = new HashMap<>();

  private static final List<DyeColor> colors = Arrays.asList(DyeColor.values());

  private static final ItemStack[] NO_ARMOR = new ItemStack[0];

  static {
    registerEntityTypeValues();
    registerExtraAliases();
    registerTypeVariants();
    registerCustomTypes();
    registerBrokenTypes();
  }

  private final String name;

  private final String plural;

  private final EntityType type;

  public MACreature(EntityType type, String name) {
    this.type = type;
    this.name = name;
    this.plural = null;
  }

  /**
     * @deprecated This constructor will be removed in a future update.
     * Use {@link #MACreature(EntityType, String)} instead, and register
     * with {@link #register(String, MACreature)}.
     */
  @Deprecated public MACreature(String name, String plural, EntityType type) {
    this.name = name;
    this.plural = (plural != null) ? plural : name;
    this.type = type;
    register();
  }

  /**
     * @deprecated This constructor will be removed in a future update.
     * Use {@link #MACreature(EntityType, String)} instead, and register
     * with {@link #register(String, MACreature)}.
     */
  @Deprecated public MACreature(String name, EntityType type) {
    this(name, name + "s", type);
  }

  private void register() {
    map.put(name, this);
    map.put(plural, this);
  }

  public String getName() {
    return name;
  }

  public EntityType getType() {
    return type;
  }

  public LivingEntity spawn(Arena arena, World world, Location loc) {
    LivingEntity e = (LivingEntity) world.spawnEntity(loc, type);
    e.setCanPickupItems(false);
    e.getEquipment().setArmorContents(NO_ARMOR);
    switch (this.name) {
      case "sheep":
      ((Sheep) e).setColor(colors.get(MobArena.random.nextInt(colors.size())));
      break;
      case "explodingsheep":
      arena.getMonsterManager().addExplodingSheep(e);
      ((Sheep) e).setColor(DyeColor.RED);
      break;
      case "poweredcreeper":
      ((Creeper) e).setPowered(true);
      break;
      case "angrybee":
      ((Bee) e).setAnger(Integer.MAX_VALUE);
      break;
      case "angrywolf":
      ((Wolf) e).setAngry(true);
      break;
      case "slime":
      case "magmacube":
      ((Slime) e).setSize((1 + MobArena.random.nextInt(3)));
      break;
      case "slimetiny":
      case "magmacubetiny":
      ((Slime) e).setSize(1);
      break;
      case "slimesmall":
      case "magmacubesmall":
      ((Slime) e).setSize(2);
      break;
      case "slimebig":
      case "magmacubebig":
      ((Slime) e).setSize(3);
      break;
      case "slimehuge":
      case "magmacubehuge":
      ((Slime) e).setSize(4);
      break;
      case "babyzombievillager":
      case "babyzombie":
      ((Zombie) e).setBaby(true);
      break;
      case "babypigman":
      case "babyzombifiedpiglin":
      ((Zombie) e).setBaby(true);
      ((PigZombie) e).setAngry(true);
      break;
      case "pigzombie":
      case "zombiepigman":
      case "zombifiedpiglin":
      ((PigZombie) e).setAngry(true);
      break;
      case "killerbunny":
      ((Rabbit) e).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
      break;
      case "piglin":
      case "piglinbrute":
      ((PiglinAbstract) e).setImmuneToZombification(true);
      case "hoglin":
      ((Hoglin) e).setImmuneToZombification(true);
      default:
      break;
    }
    if (e instanceof Creature) {
      Creature c = (Creature) e;
      c.setTarget(WaveUtils.getClosestPlayer(arena, e));
    }
    return e;
  }

  /**
     * Register an MACreature instance by the given key.
     * <p>
     * Adds the MACreature instance to the internal monster type registry such
     * that it can be used in monster waves.
     * <p>
     * Any existing registration under the given key is silently overwritten.
     *
     * @param key a key to reference the creature by
     * @param creature an MACreature instance
     */
  public static void register(String key, MACreature creature) {
    map.put(key, creature);
  }

  public static MACreature fromString(String string) {
    MACreature creature = map.get(string);
    if (creature != null) {
      return creature;
    }
    String squashed = squash(string);
    return map.get(squashed);
  }

  private static String squash(String string) {
    return string.replaceAll("[-_.]", "").toLowerCase();
  }

  private static void registerEntityTypeValues() {
    for (EntityType type : EntityType.values()) {
      if (type.isAlive()) {
        String key = squash(type.name());
        put(key, key + "s", type);
      }
    }
    copy("enderman", "endermen");
    copy("snowman", "snowmen");
    copy("witch", "witches");
    copy("wolf", "wolves");
  }

  private static void registerExtraAliases() {
    put("mooshroom", "mooshrooms", "MUSHROOM_COW");
    put("snowgolem", "snowgolems", "SNOWMAN");
    put("undeadhorse", "undeadhorses", "ZOMBIE_HORSE");
  }

  private static void registerTypeVariants() {
    put("angrybee", "angrybees", "BEE", null);
    put("angrywolf", "angrywolves", "WOLF");
    put("babyzombie", "babyzombies", "ZOMBIE");
    put("babyzombievillager", "babyzombievillagers", "ZOMBIE_VILLAGER");
    put("killerbunny", "killerbunnies", "RABBIT");
    put("magmacubebig", "magmacubesbig", "MAGMA_CUBE");
    put("magmacubehuge", "magmacubeshuge", "MAGMA_CUBE");
    put("magmacubesmall", "magmacubessmall", "MAGMA_CUBE");
    put("magmacubetiny", "magmacubestiny", "MAGMA_CUBE");
    put("poweredcreeper", "poweredcreepers", "CREEPER");
    put("slimebig", "slimesbig", "SLIME");
    put("slimehuge", "slimeshuge", "SLIME");
    put("slimesmall", "slimessmall", "SLIME");
    put("slimetiny", "slimestiny", "SLIME");
  }

  private static void registerCustomTypes() {
    put("explodingsheep", "explodingsheep", "SHEEP");
  }

  private static void registerBrokenTypes() {
    put("babyzombifiedpiglin", "babyzombifiedpiglins", "ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");
    put("zombifiedpiglin", "zombifiedpiglins", "ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");
    put("babypigman", "babypigmen", "ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");
    put("pigzombie", "pigzombies", "ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");
    put("zombiepigman", "zombiepigmen", "ZOMBIFIED_PIGLIN", "PIG_ZOMBIE");
  }

  private static void put(String key, String plural, EntityType type) {
    MACreature creature = new MACreature(type, key);
    register(key, creature);
    register(plural, creature);
  }

  private static void put(String key, String plural, String... names) {
    for (String name : names) {
      if (name == null) {
        return;
      }
      try {
        EntityType type = EntityType.valueOf(name);
        put(key, plural, type);
        return;
      } catch (IllegalArgumentException e) {
      }
    }
    Plugin plugin = Bukkit.getPluginManager().getPlugin("MobArena");
    if (plugin != null) {
      if (names.length == 1) {
        plugin.getLogger().warning("Failed to register monster type \'" + key + "\', because its type was not found: " + names[0]);
      } else {
        plugin.getLogger().warning("Failed to register monster type \'" + key + "\', because none of its possible types were found: " + Arrays.toString(names));
      }
    }
  }

  private static void copy(String source, String target) {
    MACreature creature = map.get(source);
    if (creature != null) {
      map.put(target, creature);
    }
  }
}