package net.onyx.client.components;

import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.util.Identifier;

import java.util.Random;
import java.util.UUID;

public class FemboySkinHelper extends DefaultSkinHelper {
    private static class FemboySkin {
        public boolean isSlim;
        public Identifier id;

        public FemboySkin(Identifier id, boolean isSlim) {
            this.isSlim = isSlim;
            this.id = id;
        }

        public FemboySkin(Identifier id) {
            this(id, false);
        }
    }

    private static final FemboySkin[] FEMBOY_SKINS = {
        // Felix
        new FemboySkin(new Identifier("como-client:textures/entity/femboy/felix_1.png")),
        new FemboySkin(new Identifier("como-client:textures/entity/femboy/felix_2.png")),
        new FemboySkin(new Identifier("como-client:textures/entity/femboy/felix_3.png")),
        new FemboySkin(new Identifier("como-client:textures/entity/femboy/felix_4.png"))
    };

    private static final FemboySkin[] SLIM_FEMBOY_SKINS = {
        // Astolfo
        new FemboySkin(new Identifier("como-client:textures/entity/femboy/slim/astolfo_1.png"), true),
        new FemboySkin(new Identifier("como-client:textures/entity/femboy/slim/astolfo_2.png"), true),

        // My Fav Polish Gent <3
        new FemboySkin(new Identifier("como-client:textures/entity/femboy/slim/shm11.png"), true)
    };

    /**
     * A random slim femboy skin
     * @param random
     * @return random femboy skin from the 'slim' set
     */
    private static FemboySkin randomSlimSkin(Random random) {
        return SLIM_FEMBOY_SKINS[random.nextInt(0, SLIM_FEMBOY_SKINS.length)];
    }


    /**
     * A random skin model
     * @return either "slim" or "default"
     */
    public static String randomModel(Random random) {
        return random.nextBoolean() ? "slim" : "default";
    }

    /**
     * A random default femboy skin
     * @param random
     * @return random femboy skin from the 'default' set
     */
    private static FemboySkin randomDefaultSkin(Random random) {
        return FEMBOY_SKINS[random.nextInt(0, SLIM_FEMBOY_SKINS.length)];
    }

    /**
     * Get default skin texture
     * @return default skin texture
     */
    public static Identifier getTexture() {
        return randomDefaultSkin(new Random()).id;
    }
    
    /**
     * Instantiates a random with the UUID as the seed
     * @param uuid A player's UUID
     * @return Random
     */
    public static Random randomFromUuid(UUID uuid) {
        return new Random(uuid.getLeastSignificantBits());
    }

    /**
     * Get a random texture with a specific random object
     * @param uuid
     * @param random
     * @param isSlim if the player uses the slim model
     * @return random id
     */
    public static Identifier getTexture(UUID uuid, Random random, boolean isSlim) {
        FemboySkin skin = isSlim ? randomSlimSkin(random) : randomDefaultSkin(random);
        return skin.id;
    }

    /**
     * Get a random texture with a specific random object
     * @param uuid
     * @param random
     * @return random id
     */
    public static Identifier getTexture(UUID uuid, Random random) {
        return getTexture(uuid, random, DefaultSkinHelper.getModel(uuid).equals("slim"));
    }
}
