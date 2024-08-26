package foundationgames.enhancedblockentities.client.renderer.entity;

import com.google.common.base.Function;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import foundationgames.enhancedblockentities.client.renderer.BlockEntityRendererOverride;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import java.util.function.Supplier;

public class ChestBlockEntityRendererOverride extends BlockEntityRendererOverride {

    private BakedModel[] models = null;
    private final Supplier<BakedModel[]> modelGetter;
    private final Function<BlockEntity, Integer> modelSelector;

    public ChestBlockEntityRendererOverride(Supplier<BakedModel[]> modelGetter, Function<BlockEntity, Integer> modelSelector) {
        this.modelGetter = modelGetter;
        this.modelSelector = modelSelector;
    }

    @Override
    public void render(BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (this.models == null) this.models = this.modelGetter.get();
        if (blockEntity instanceof LidBlockEntity) {
            matrices.pushPose();

            LidBlockEntity chest = getAnimationProgress(blockEntity, tickDelta);
            matrices.translate(0.5f, 0, 0.5f);
            Direction dir = blockEntity.getBlockState().getValue(ChestBlock.FACING);
            matrices.mulPose(Axis.YP.rotationDegrees(180 - dir.toYRot()));
            matrices.translate(-0.5f, 0, -0.5f);
            float yPiv = 9f / 16;
            float zPiv = 15f / 16;
            matrices.translate(0, yPiv, zPiv);
            float rot = chest.getOpenNess(tickDelta);
            rot = 1f - rot;
            rot = 1f - (rot * rot * rot);
            matrices.mulPose(Axis.XP.rotationDegrees(rot * 90));
            matrices.translate(0, -yPiv, -zPiv);
            EBEOtherUtils.renderBakedModel(vertexConsumers, blockEntity.getBlockState(), matrices, this.models[this.modelSelector.apply(blockEntity)], light, overlay);

            matrices.popPose();
        }
    }

    public static LidBlockEntity getAnimationProgress(BlockEntity blockEntity, float tickDelta) {
        LidBlockEntity chest = (LidBlockEntity) blockEntity;
        BlockState state = blockEntity.getBlockState();
        if (state.hasProperty(ChestBlock.TYPE) && state.getValue(ChestBlock.TYPE) != ChestType.SINGLE) {
            BlockEntity neighbor = null;
            BlockPos pos = blockEntity.getBlockPos();
            Direction facing = state.getValue(ChestBlock.FACING);
            switch (state.getValue(ChestBlock.TYPE)) {
                case LEFT -> neighbor = blockEntity.getLevel().getBlockEntity(pos.relative(facing.getClockWise()));
                case RIGHT -> neighbor = blockEntity.getLevel().getBlockEntity(pos.relative(facing.getCounterClockWise()));
            }
            if (neighbor instanceof LidBlockEntity lidBlockEntity) {
                float nAnim = lidBlockEntity.getOpenNess(tickDelta);
                if (nAnim > chest.getOpenNess(tickDelta)) {
                    chest = lidBlockEntity;
                }
            }
        }

        return chest;
    }

    @Override
    public void onModelsReload() {
        this.models = null;
    }
}
