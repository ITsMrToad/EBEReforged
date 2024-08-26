package foundationgames.enhancedblockentities.core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import foundationgames.enhancedblockentities.client.renderer.BlockEntityRendererCondition;
import foundationgames.enhancedblockentities.client.renderer.BlockEntityRendererOverride;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityRenderDispatcher.class)
public abstract class BlockEntityRenderDispatcherMixin {

    @Inject(method = "setupAndRender", at = @At("HEAD"), cancellable = true)
    private static void override(BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo ci) {
        if (EBE.ENTITIES.containsKey(blockEntity.getType()) && EBE.BLOCKS.contains(blockEntity.getBlockState().getBlock())) {
            Pair<BlockEntityRendererCondition, BlockEntityRendererOverride> entry = EBE.ENTITIES.get(blockEntity.getType());
            int color = 0;
            if (blockEntity.getLevel() != null) {
                color = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos());
            }

            if (entry.getFirst().shouldRender(blockEntity)) {
                entry.getSecond().render(renderer, blockEntity, tickDelta, matrices, vertexConsumers, color, OverlayTexture.NO_OVERLAY);
            }
            ci.cancel();
        }
    }
}
