package foundationgames.enhancedblockentities.core.mixin.ffapi;

import com.mojang.datafixers.util.Pair;
import foundationgames.enhancedblockentities.common.util.DefaultedTuple;
import foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin.ModelLoadingPlugin;
import foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin.ModelLoadingPluginManager;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;

@Mixin(ModelManager.class)
public abstract class ModelManagerMixin {

    @Unique private final DefaultedTuple<ResourceManager, Executor> ebe$forPrepare = new DefaultedTuple<>(Minecraft.getInstance().getResourceManager(), Executors.newCachedThreadPool());

    @Inject(method = "reload", at = @At("HEAD"))
    public void declareForPrepare(PreparableReloadListener.PreparationBarrier barrier, ResourceManager manager, ProfilerFiller filler, ProfilerFiller filler2, Executor executor, Executor prepare, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        this.ebe$forPrepare.setA(manager);
        this.ebe$forPrepare.setB(prepare);
    }

    @Redirect(method = "reload", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenCombineAsync(Ljava/util/concurrent/CompletionStage;Ljava/util/function/BiFunction;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;", remap = false), allow = 1)
    private CompletableFuture<ModelBakery> loadModelPluginData(CompletableFuture<Map<ResourceLocation, BlockModel>> instance, CompletionStage<Map<ResourceLocation, List<ModelBakery.LoadedJson>>> other, BiFunction<Map<ResourceLocation, BlockModel>, Map<ResourceLocation, List<ModelBakery.LoadedJson>>, ModelBakery> modelLoaderConstructor, Executor applyExecutor) {
        CompletableFuture<Pair<Map<ResourceLocation, BlockModel>, Map<ResourceLocation, List<ModelBakery.LoadedJson>>>> pairFuture = instance.thenCombine(other, Pair::of);
        if (this.ebe$forPrepare.isDefault()) {
            EBE.LOGGER.error(EBE.FFAPI, "Cannot load FFAPI plugins!");
            return pairFuture.thenApplyAsync(pair -> modelLoaderConstructor.apply(pair.getFirst(), pair.getSecond()), applyExecutor);
        }

        CompletableFuture<List<ModelLoadingPlugin>> pluginsFuture = ModelLoadingPluginManager.preparePlugins(this.ebe$forPrepare.getA(), this.ebe$forPrepare.getB());
        return pairFuture.thenCombineAsync(pluginsFuture, (pair, plugins) -> {
            ModelLoadingPluginManager.CURRENT_PLUGINS.set(plugins);
            return modelLoaderConstructor.apply(pair.getFirst(), pair.getSecond());
        }, applyExecutor);

    }
}
