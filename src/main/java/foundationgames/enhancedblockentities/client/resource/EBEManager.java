package foundationgames.enhancedblockentities.client.resource;

import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class EBEManager extends SimplePreparableReloadListener<Void> {


    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        EBE.load(EBE.CONFIG);
        return null;
    }

    @Override
    protected void apply(Void v, ResourceManager p_10794_, ProfilerFiller profiler) {
        EBE.LOGGER.info("EBE reloaded");
    }
}
