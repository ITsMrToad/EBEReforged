package foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@OnlyIn(Dist.CLIENT)
public interface PreparableModelLoadingPlugin<T> {

    void onInitializeModelLoader(T data, ModelLoadingPlugin.Context pluginContext);

    @FunctionalInterface
    interface DataLoader<T> {
        CompletableFuture<T> load(ResourceManager resourceManager, Executor executor);
    }
}
