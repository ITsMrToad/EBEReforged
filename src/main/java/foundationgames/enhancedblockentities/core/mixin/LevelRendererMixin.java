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

@Mixin(value = LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Nullable @Unique public ChunkRenderDispatcher.RenderChunk ebe$chunk;

    @Inject(method = "compileChunks", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/SectionPos;of(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/SectionPos;", shift = At.Shift.AFTER))
    public void addPostRebuildTask(Camera camera, CallbackInfo ci) {
        if (!EBEOtherUtils.CHUNK_UPDATE_TASKS.isEmpty() && this.ebe$chunk != null) {
            SectionPos pos = SectionPos.of(this.ebe$chunk.getOrigin());

            if (EBEOtherUtils.CHUNK_UPDATE_TASKS.containsKey(pos)) {
                Runnable task = EBEOtherUtils.CHUNK_UPDATE_TASKS.remove(pos);
                ((ChunkRenderTaskAccess) this.ebe$chunk).ebe$setTaskAfterRebuild(task);
            }
        }
    }

    @Inject(method = "addRecentlyCompiledChunk", at = @At("HEAD"))
    private void runPostRebuildTask(ChunkRenderDispatcher.RenderChunk chunk, CallbackInfo ci) {
        ((ChunkRenderTaskAccess) chunk).runAfterRebuildTask();
    }

}
