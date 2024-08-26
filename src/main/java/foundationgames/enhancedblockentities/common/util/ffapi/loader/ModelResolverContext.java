package foundationgames.enhancedblockentities.common.util.ffapi.loader;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public record ModelResolverContext(ModelBakery modelBakery, Function<ResourceLocation, UnbakedModel> modelLoader) implements ModelResolver.Context {

    @Override
    public UnbakedModel getOrLoadModel(ResourceLocation id) {
        return this.modelLoader.apply(id);
    }

    @Override
    public ModelBakery loader() {
        return this.modelBakery;
    }

}
