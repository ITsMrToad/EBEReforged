package foundationgames.enhancedblockentities.client.model.dynamic;

import foundationgames.enhancedblockentities.common.util.ffapi.loader.ModelResolver;
import foundationgames.enhancedblockentities.common.util.ffapi.loader.plugin.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.Nullable;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class DynamicModelHandler implements ModelLoadingPlugin, ModelResolver {

    private final Supplier<DynamicUnbakedModel> model;
    private final ResourceLocation id;

    public DynamicModelHandler(ResourceLocation id, Supplier<DynamicUnbakedModel> model) {
        this.model = model;
        this.id = id;
    }

    @Override
    public void init(ModelLoadingPlugin.Context ctx) {
        ctx.resolver().add(this);
    }

    @Override
    public @Nullable UnbakedModel resolveModel(ModelResolver.Context ctx) {
        ModelManager manager = Minecraft.getInstance().getModelManager();
        BakedModel model = manager.getModel(this.id);
        if (model != manager.getMissingModel()) {
            return this.model.get();
        }
        return null;
    }

}
