package foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import net.minecraft.Util;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@OnlyIn(Dist.CLIENT)
public class ModelLoadingPluginManager {

    public static final List<ModelLoadingPlugin> PLUGINS = Lists.newArrayList();
    public static final List<PreparablePluginListener<?>> PREPARABLE_PLUGINS = Lists.newArrayList();

    public static final ThreadLocal<List<ModelLoadingPlugin>> CURRENT_PLUGINS = new ThreadLocal<>();

    public static void registerPlugin(ModelLoadingPlugin plugin) {
        Preconditions.checkNotNull(plugin, "Plugin cannot be null!");
        PLUGINS.add(plugin);
    }

    public static <T> void registerPlugin(PreparableModelLoadingPlugin.DataLoader<T> loader, PreparableModelLoadingPlugin<T> plugin) {
        Preconditions.checkNotNull(loader, "Data loader cannot be null!");
        Preconditions.checkNotNull(plugin, "Plugin cannot be null!");
        PREPARABLE_PLUGINS.add(new PreparablePluginListener<>(loader, plugin));
    }

    public static CompletableFuture<List<ModelLoadingPlugin>> preparePlugins(ResourceManager manager, Executor executor) {
        List<CompletableFuture<ModelLoadingPlugin>> futures = Lists.newArrayList();
        PLUGINS.forEach(plugin -> futures.add(CompletableFuture.completedFuture(plugin)));
        PREPARABLE_PLUGINS.forEach(holder -> futures.add(preparePlugin(holder, manager, executor)));
        return Util.sequence(futures);
    }

    private static <T> CompletableFuture<ModelLoadingPlugin> preparePlugin(PreparablePluginListener<T> holder, ResourceManager resourceManager, Executor executor) {
        CompletableFuture<T> dataFuture = holder.loader().load(resourceManager, executor);
        return dataFuture.thenApply(data -> pluginContext -> holder.plugin().onInitializeModelLoader(data, pluginContext));
    }
}
