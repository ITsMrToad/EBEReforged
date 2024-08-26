package foundationgames.enhancedblockentities.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import foundationgames.enhancedblockentities.client.renderer.BlockEntityRendererOverride;
import foundationgames.enhancedblockentities.core.mixin.accessor.SignRendererInvoker;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class SignBlockEntityRendererOverride extends BlockEntityRendererOverride {

    @Override
    public void render(BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (blockEntity instanceof SignBlockEntity entity) {
            BlockState state = entity.getBlockState();
            SignBlock block = (SignBlock) state.getBlock();
            SignRendererInvoker invoker = (SignRendererInvoker) renderer;
            invoker.rotateText(matrices, - block.getYRotationDegrees(state), state);
            invoker.renderText(entity.getBlockPos(), entity.getFrontText(), matrices, vertexConsumers, light, entity.getTextLineHeight(), entity.getMaxTextLineWidth(), true);
            invoker.renderText(entity.getBlockPos(), entity.getBackText(), matrices, vertexConsumers, light, entity.getTextLineHeight(), entity.getMaxTextLineWidth(), false);
        }
    }

}
