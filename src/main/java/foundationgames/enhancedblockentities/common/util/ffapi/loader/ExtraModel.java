package foundationgames.enhancedblockentities.common.util.ffapi.loader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface ExtraModel {
    void provide(ResourceManager manager, Consumer<ResourceLocation> rl);
}
