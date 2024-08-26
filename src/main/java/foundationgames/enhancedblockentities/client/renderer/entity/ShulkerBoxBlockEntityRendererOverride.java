package foundationgames.enhancedblockentities.client.renderer.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import foundationgames.enhancedblockentities.client.renderer.BlockEntityRendererOverride;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Consumer;

public class ShulkerBoxBlockEntityRendererOverride extends BlockEntityRendererOverride {

    private final Map<DyeColor, BakedModel> models = Maps.newHashMap();
    private final Consumer<Map<DyeColor, BakedModel>> modelMapFiller;

    public ShulkerBoxBlockEntityRendererOverride(Consumer<Map<DyeColor, BakedModel>> modelMapFiller) {
        this.modelMapFiller = modelMapFiller;
    }

    @Override
    public void render(BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (this.models.isEmpty()) this.modelMapFiller.accept(this.models);
        if (blockEntity instanceof ShulkerBoxBlockEntity entity && entity.getLevel() != null) {
            Direction dir = Direction.UP;
            BlockState state = entity.getLevel().getBlockState(entity.getBlockPos());
            if (state.getBlock() instanceof ShulkerBoxBlock) {
                dir = state.getValue(ShulkerBoxBlock.FACING);
            }
            matrices.pushPose();

            float animation = entity.getProgress(tickDelta);

            matrices.translate(0.5, 0.5, 0.5);
            matrices.mulPose(dir.getRotation());
            matrices.mulPose(Axis.YP.rotationDegrees(270 * animation));
            matrices.translate(-0.5, -0.5, -0.5);

            matrices.translate(0, animation * 0.5f, 0);

            BakedModel lidModel = this.models.get(entity.getColor());
            if (lidModel != null) {
                EBEOtherUtils.renderBakedModel(vertexConsumers, blockEntity.getBlockState(), matrices, lidModel, light, overlay);
            }

            matrices.popPose();
        }
    }

    @Override
    public void onModelsReload() {
        this.models.clear();
    }
}
