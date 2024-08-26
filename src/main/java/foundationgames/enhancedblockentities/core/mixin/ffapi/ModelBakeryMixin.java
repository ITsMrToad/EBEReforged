package foundationgames.enhancedblockentities.core.mixin.ffapi;

import foundationgames.enhancedblockentities.common.util.ffapi.loader.ModelResolverContext;
import foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin.ModelLoadingPluginContext;
import foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin.ModelLoadingPluginManager;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @Shadow public abstract UnbakedModel getModel(ResourceLocation rl);

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void afterMissingModelInit(BlockColors blockColors, ProfilerFiller profiler, Map<ResourceLocation, BlockModel> map, Map<ResourceLocation, List<ModelBakery.LoadedJson>> blockStates, CallbackInfo ci) {
        profiler.push("ffapi_plugins_init");
        ModelLoadingPluginManager.CURRENT_PLUGINS.get().forEach(plugin -> plugin.init(new ModelLoadingPluginContext(new ModelResolverContext((ModelBakery) (Object) this, this::getModel))));
        EBE.LOGGER.info(EBE.FFAPI, "Plugins Init");
        ModelLoadingPluginManager.CURRENT_PLUGINS.remove();
        profiler.pop();
    }

}
