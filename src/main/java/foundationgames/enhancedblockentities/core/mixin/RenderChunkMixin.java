package foundationgames.enhancedblockentities.core.mixin;

import foundationgames.enhancedblockentities.common.util.ChunkRenderTaskAccess;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;

import org.jetbrains.annotations.Nullable;

@Mixin(ChunkRenderDispatcher.RenderChunk.class)
public  abstract class RenderChunkMixin implements ChunkRenderTaskAccess {

    @Nullable private Runnable enhanced_bes$taskAfterRebuild = null;

    @Override
    public Runnable ebe$getTaskAfterRebuild() {
        return enhanced_bes$taskAfterRebuild;
    }

    @Override
    public void ebe$setTaskAfterRebuild(Runnable task) {
        this.enhanced_bes$taskAfterRebuild = task;
    }
}
