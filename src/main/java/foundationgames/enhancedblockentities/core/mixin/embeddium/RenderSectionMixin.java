package foundationgames.enhancedblockentities.core.mixin.embeddium;

import foundationgames.enhancedblockentities.common.util.ChunkRenderTaskAccess;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;

import org.jetbrains.annotations.Nullable;

@Pseudo
@Mixin(value = RenderSection.class, remap = false)
public abstract class RenderSectionMixin implements ChunkRenderTaskAccess {

    @Unique @Nullable private Runnable enhanced_bes$taskAfterRebuild = null;

    @Override
    public Runnable ebe$getTaskAfterRebuild() {
        return this.enhanced_bes$taskAfterRebuild;
    }

    @Override
    public void ebe$setTaskAfterRebuild(Runnable task) {
        this.enhanced_bes$taskAfterRebuild = task;
    }
}
