package foundationgames.enhancedblockentities.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import foundationgames.enhancedblockentities.client.model.ModelIdentifiers;
import foundationgames.enhancedblockentities.client.renderer.BlockEntityRendererOverride;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BellBlockEntityRendererOverride extends BlockEntityRendererOverride {

    private BakedModel bellModel = null;

    @Override
    public void render(BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (this.bellModel == null) this.bellModel = this.getBellModel();
        if (blockEntity instanceof BellBlockEntity self) {
            float ringTicks = (float) self.ticks + tickDelta;
            float bellPitch = 0.0F;
            float bellRoll = 0.0F;
            if (self.shaking) {
                float swingAngle = Mth.sin(ringTicks / (float)Math.PI) / (4.0F + ringTicks / 3.0F);
                if (self.clickDirection == Direction.NORTH) {
                    bellPitch = -swingAngle;
                } else if (self.clickDirection == Direction.SOUTH) {
                    bellPitch = swingAngle;
                } else if (self.clickDirection == Direction.EAST) {
                    bellRoll = -swingAngle;
                } else if (self.clickDirection == Direction.WEST) {
                    bellRoll = swingAngle;
                }
            }
            matrices.pushPose();
            matrices.translate(8f/16, 12f/16, 8f/16);
            matrices.mulPose(Axis.XP.rotation(bellPitch));
            matrices.mulPose(Axis.ZP.rotation(bellRoll));
            matrices.translate(-8f/16, -12f/16, -8f/16);
            EBEOtherUtils.renderBakedModel(vertexConsumers, blockEntity.getBlockState(), matrices, bellModel, light, overlay);
            matrices.popPose();
        }
    }

    private BakedModel getBellModel() {
        ModelManager manager = Minecraft.getInstance().getModelManager();
        return manager.getModel(ModelIdentifiers.BELL_BODY);
    }

    @Override
    public void onModelsReload() {
        this.bellModel = null;
    }

}
