package foundationgames.enhancedblockentities.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import foundationgames.enhancedblockentities.client.model.ModelIdentifiers;
import foundationgames.enhancedblockentities.client.renderer.BlockEntityRendererCondition;
import foundationgames.enhancedblockentities.client.renderer.BlockEntityRendererOverride;
import foundationgames.enhancedblockentities.client.resource.template.TemplateLoader;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import foundationgames.enhancedblockentities.common.util.ResourceUtil;
import foundationgames.enhancedblockentities.common.util.ffapi.ModelLoadingRegistry;
import foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin.ModelLoadingPluginManager;
import foundationgames.enhancedblockentities.core.config.EBEConfig;
import foundationgames.enhancedblockentities.core.config.EBEConfigEnumValue;
import foundationgames.enhancedblockentities.core.loader.CommonSetup;
import foundationgames.enhancedblockentities.integration.SinytraIntegration;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@Mod(EBE.MODID)
public class EBE {

    public static final Map<BlockEntityType<?>, Pair<BlockEntityRendererCondition, BlockEntityRendererOverride>> ENTITIES = Maps.newHashMap();
    public static final Set<Block> BLOCKS = Sets.newHashSet();

    public static final String MODID = "ebe";
    public static final Logger LOGGER = LoggerFactory.getLogger("EBE");
    public static final Marker FFAPI = MarkerFactory.getMarker("FFAPI");
   
    public static final TemplateLoader TEMPLATE_LOADER = new TemplateLoader();

    @Nullable public static EBEConfig CONFIG;

    public EBE() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::clientSetup));
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        if (EBE.CONFIG == null) {
            EBE.CONFIG = EBEConfig.loadOrCreate();
        }
        
        if (CONFIG != null) {
            event.enqueueWork(() -> CONFIG.save());
        }
        
        event.enqueueWork(() -> {
            CommonSetup.setupResourceProviders();
            ItemProperties.registerGeneric(EBEOtherUtils.id("is_christmas"), (stack, world, entity, seed) -> EBEOtherUtils.isChristmas() ? 1 : 0);
            ModelLoadingPluginManager.registerPlugin((resourceManager, executor) -> CompletableFuture.completedFuture(resourceManager), ModelLoadingRegistry.INSTANCE::init);
            ModelLoadingPluginManager.registerPlugin(new ModelIdentifiers());
        });
    }

    public static void load(@Nullable EBEConfig config) {
        clear();

        ResourceUtil.resetBasePack();
        ResourceUtil.resetTopLevelPack();

        if (config != null) {
            if (config.renderEnhancedChests) {
                CommonSetup.setupChests();
                CommonSetup.setupRRPChests();
            }

            if (config.renderEnhancedSigns) {
                CommonSetup.setupSigns();
                CommonSetup.setupRRPSigns();
            }

            if (config.renderEnhancedBells) {
                CommonSetup.setupBells();
                CommonSetup.setupRRPBells();
            }

            if (config.renderEnhancedBeds) {
                CommonSetup.setupBeds();
                CommonSetup.setupRRPBeds();
            }

            if (config.renderEnhancedShulkerBoxes) {
                CommonSetup.setupShulkerBoxes();
                CommonSetup.setupRRPShulkerBoxes();
            }

            if (config.renderEnhancedDecoratedPots) {
                CommonSetup.setupDecoratedPots();
                CommonSetup.setupRRPDecoratedPots();
            }
        }
    }

    public static void configAction(Consumer<EBEConfig> action) {
        if (CONFIG != null) {
            action.accept(CONFIG);
        }
    }

    public static EBEConfigEnumValue ebeCfgEnumValue(Function<EBEConfig, EBEConfigEnumValue> mapper) {
        if (CONFIG != null) {
            return mapper.apply(CONFIG);
        } else {
            return EBEConfigEnumValue.ON;
        }
    }

    public static boolean checkValue(Function<EBEConfig, Boolean> mapper) {
        if (CONFIG == null) {
            return false;
        } else {
            return mapper.apply(CONFIG);
        }
    }

    public static void register(Block block, BlockEntityType<?> type, BlockEntityRendererCondition condition, BlockEntityRendererOverride renderer) {
        ENTITIES.put(type, new Pair<>(condition, renderer));
        BLOCKS.add(block);
    }

    public static void clear() {
        ENTITIES.clear();
        BLOCKS.clear();
    }

}
