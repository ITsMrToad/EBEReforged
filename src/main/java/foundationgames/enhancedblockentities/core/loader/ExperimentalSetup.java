package foundationgames.enhancedblockentities.core.loader;

import foundationgames.enhancedblockentities.client.resource.EBEPack;
import foundationgames.enhancedblockentities.common.util.ResourceUtil;
import foundationgames.enhancedblockentities.common.util.hacks.ResourceHacks;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;

import java.io.IOException;

public class ExperimentalSetup {

    private static ResourceManager RESOURCES;

    public static void setup() {
        if (EBE.checkValue(cfg -> cfg.renderEnhancedChests) && EBE.checkValue(cfg -> cfg.experimentalChests)) {
            try {
                if (RESOURCES != null) setupChests(RESOURCES);
            } catch (IOException e) {
                EBE.LOGGER.error("Error loading experimental chests!", e);
                EBE.configAction(cfg -> {
                    cfg.experimentalChests = false;
                    cfg.save();
                });
            }
        }
        if (EBE.checkValue(cfg -> cfg.renderEnhancedBeds) && EBE.checkValue(cfg -> cfg.experimentalBeds)) {
            try {
                if (RESOURCES != null) setupBeds(RESOURCES);
            } catch (IOException e) {
                EBE.LOGGER.error("Error loading experimental beds!", e);
                EBE.configAction(cfg -> {
                    cfg.experimentalBeds = false;
                    cfg.save();
                });;
            }
        }
        if (EBE.checkValue(cfg -> cfg.renderEnhancedSigns) && EBE.checkValue(cfg -> cfg.experimentalSigns)) {
            try {
                if (RESOURCES != null) setupSigns(RESOURCES);
            } catch (IOException e) {
                EBE.LOGGER.error("Error loading experimental signs!", e);
                EBE.configAction(cfg -> {
                    cfg.experimentalSigns = false;
                    cfg.save();
                });
            }
        }
    }

    public static void setupChests(ResourceManager manager) throws IOException {
        EBEPack p = ResourceUtil.getTopLevelPack();

        ResourceHacks.addChestParticleTexture("chest", "entity/chest/normal", manager, p);
        ResourceHacks.addChestParticleTexture("trapped_chest", "entity/chest/trapped", manager, p);
        ResourceHacks.addChestParticleTexture("ender_chest", "entity/chest/ender", manager, p);
        ResourceHacks.addChestParticleTexture("christmas_chest", "entity/chest/christmas", manager, p);
    }

    public static void setupBeds(ResourceManager manager) throws IOException {
        EBEPack p = ResourceUtil.getTopLevelPack();

        for (DyeColor color : DyeColor.values()) {
            ResourceHacks.addBedParticleTexture(color.getName(), "entity/bed/"+color.getName(), manager, p);
        }
    }

    public static void setupSigns(ResourceManager manager) throws IOException {
        EBEPack p = ResourceUtil.getTopLevelPack();

        ResourceHacks.addSignParticleTexture("oak", "entity/signs/oak", manager, p);
        ResourceHacks.addSignParticleTexture("birch", "entity/signs/birch", manager, p);
        ResourceHacks.addSignParticleTexture("spruce", "entity/signs/spruce", manager, p);
        ResourceHacks.addSignParticleTexture("jungle", "entity/signs/jungle", manager, p);
        ResourceHacks.addSignParticleTexture("acacia", "entity/signs/acacia", manager, p);
        ResourceHacks.addSignParticleTexture("dark_oak", "entity/signs/dark_oak", manager, p);
        ResourceHacks.addSignParticleTexture("mangrove", "entity/signs/mangrove", manager, p);
        ResourceHacks.addSignParticleTexture("cherry", "entity/signs/cherry", manager, p);
        ResourceHacks.addSignParticleTexture("crimson", "entity/signs/crimson", manager, p);
        ResourceHacks.addSignParticleTexture("warped", "entity/signs/warped", manager, p);
        ResourceHacks.addSignParticleTexture("bamboo", "entity/signs/bamboo", manager, p);
    }

    public static void cacheResources(ResourceManager resources) {
        RESOURCES = resources;
    }

}
