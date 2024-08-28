package foundationgames.enhancedblockentities.core.mixin;

import foundationgames.enhancedblockentities.common.util.ChunkRenderTaskAccess;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.jetbrains.annotations.Nullable;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @ModifyVariable(method = "compileChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;of(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/SectionPos;", shift = At.Shift.AFTER), index = 8)
    public ChunkRenderDispatcher.RenderChunk addPostRebuildTask(ChunkRenderDispatcher.RenderChunk value) {
        if (!EBEOtherUtils.CHUNK_UPDATE_TASKS.isEmpty() ) {
            SectionPos pos = SectionPos.of(value.getOrigin());

            if (EBEOtherUtils.CHUNK_UPDATE_TASKS.containsKey(pos)) {
                Runnable task = EBEOtherUtils.CHUNK_UPDATE_TASKS.remove(pos);
                ((ChunkRenderTaskAccess) value).ebe$setTaskAfterRebuild(task);
            }
        }
        return value;
    }
    
    @Inject(method = "addRecentlyCompiledChunk", at = @At("HEAD"))
    private void runPostRebuildTask(ChunkRenderDispatcher.RenderChunk chunk, CallbackInfo ci) {
        ((ChunkRenderTaskAccess) chunk).runAfterRebuildTask();
    }

}
