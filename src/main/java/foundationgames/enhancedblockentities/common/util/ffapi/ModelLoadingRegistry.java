package foundationgames.enhancedblockentities.common.util.ffapi;

import com.google.common.collect.Lists;
import foundationgames.enhancedblockentities.common.util.ffapi.loader.ExtraModel;
import foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin.ModelLoadingPlugin;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ModelLoadingRegistry {

    public static final ModelLoadingRegistry INSTANCE = new ModelLoadingRegistry();

    public final List<ExtraModel> modelProviders = Lists.newArrayList();

    public void init(ResourceManager resourceManager, ModelLoadingPlugin.Context pluginContext) {
        this.modelProviders.forEach(model -> model.provide(resourceManager, pluginContext::addModels));
    }

    public void addModelProvider(ExtraModel model) {
        this.modelProviders.add(model);
    }

}
