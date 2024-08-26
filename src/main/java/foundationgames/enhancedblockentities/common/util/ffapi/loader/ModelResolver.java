package foundationgames.enhancedblockentities.common.util.ffapi.loader;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface ModelResolver {

    @Nullable
    UnbakedModel resolveModel(Context context);

    @ApiStatus.NonExtendable
    interface Context {

        UnbakedModel getOrLoadModel(ResourceLocation id);

        ModelBakery loader();
    }

}
