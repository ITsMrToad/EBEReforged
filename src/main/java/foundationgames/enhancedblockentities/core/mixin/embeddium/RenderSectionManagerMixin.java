package foundationgames.enhancedblockentities.core.mixin.embeddium;

import foundationgames.enhancedblockentities.common.util.ChunkRenderTaskAccess;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSection;
import me.jellysquid.mods.sodium.client.render.chunk.RenderSectionManager;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildOutput;
import net.minecraft.core.SectionPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(value = RenderSectionManager.class, remap = false)
public abstract class RenderSectionManagerMixin {

    @ModifyVariable(method = "submitRebuildTasks", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.BEFORE, ordinal = 0, target = "Lme/jellysquid/mods/sodium/client/render/chunk/RenderSection;isDisposed()Z"), index = 4, require = 0)
    private RenderSection ebe$cacheUpdatingChunk(RenderSection section) {
        if (!EBEOtherUtils.CHUNK_UPDATE_TASKS.isEmpty()) {
            SectionPos pos = SectionPos.of(section.getChunkX(), section.getChunkY(), section.getChunkZ());

            if (EBEOtherUtils.CHUNK_UPDATE_TASKS.containsKey(pos)) {
                var task = EBEOtherUtils.CHUNK_UPDATE_TASKS.remove(pos);
                ((ChunkRenderTaskAccess) section).ebe$setTaskAfterRebuild(task);
            }
        }

        return section;
    }

    @ModifyVariable(method = "processChunkBuildResults", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.BEFORE, ordinal = 0, target = "Lme/jellysquid/mods/sodium/client/render/chunk/RenderSection;getBuildCancellationToken()Lme/jellysquid/mods/sodium/client/util/task/CancellationToken;"), index = 4, require = 0)
    private ChunkBuildOutput ebe$runPostRebuildTask(ChunkBuildOutput output) {
        ((ChunkRenderTaskAccess) output.render).runAfterRebuildTask();
        return output;
    }
}
