package foundationgames.enhancedblockentities.core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public abstract class BlockEntityWithoutLevelRendererMixin {

    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    private void enhanced_bes$renderBeds(ItemStack stack, ItemDisplayContext mode, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, CallbackInfo ci) {
        if (EBE.checkValue(cfg -> cfg.renderEnhancedBeds) && stack.getItem() instanceof BlockItem item && item.getBlock() instanceof BedBlock bed && EBE.BLOCKS.contains(bed)) {
            BlockModelShaper models = Minecraft.getInstance().getModelManager().getBlockModelShaper();

            BlockState bedState = bed.defaultBlockState().trySetValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
            BlockState footState = bedState.trySetValue(BlockStateProperties.BED_PART, BedPart.FOOT);
            BlockState headState = bedState.trySetValue(BlockStateProperties.BED_PART, BedPart.HEAD);

            BakedModel footModel = models.getBlockModel(footState);
            BakedModel headModel = models.getBlockModel(headState);

            matrices.pushPose();
            EBEOtherUtils.renderBakedModel(vertexConsumers, headState, matrices, headModel, light, overlay);
            matrices.translate(0, 0, -1);
            EBEOtherUtils.renderBakedModel(vertexConsumers, footState, matrices, footModel, light, overlay);
            matrices.popPose();

            ci.cancel();
        }
    }

}
