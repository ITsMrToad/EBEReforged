package foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin;

import foundationgames.enhancedblockentities.common.util.ffapi.loader.ModelResolver;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.List;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface ModelLoadingPlugin {

    void init(Context pluginContext);

    interface Context {
        void addModels(ResourceLocation... rl);

        void addModels(Collection<? extends ResourceLocation> c);

        List<ModelResolver> resolver();
    }

}
