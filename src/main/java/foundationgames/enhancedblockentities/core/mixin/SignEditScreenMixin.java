package foundationgames.enhancedblockentities.core.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SignEditScreen.class)
public abstract class SignEditScreenMixin {

    @Inject(method = "renderSignBackground", at = @At("HEAD"), cancellable = true)
    private void renderBakedModelSign(GuiGraphics context, BlockState state, CallbackInfo ci) {
        boolean enhanceSigns = EBE.checkValue(cfg -> cfg.renderEnhancedSigns);

        if (!EBE.BLOCKS.contains(state.getBlock())) return;

        if (enhanceSigns) {
            BlockModelShaper models = Minecraft.getInstance().getModelManager().getBlockModelShaper();
            var buffers = Minecraft.getInstance().renderBuffers().crumblingBufferSource();
            float up = 0;
            if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                state = state.trySetValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
                up += 5f/16;
            } else if (state.hasProperty(BlockStateProperties.ROTATION_16)) {
                state = state.trySetValue(BlockStateProperties.ROTATION_16, 0);
            }

            BakedModel signModel = models.getBlockModel(state);
            PoseStack matrices = context.pose();

            matrices.pushPose();
            matrices.translate(0, 31, -50);
            matrices.scale(93.75f, -93.75f, 93.75f);
            matrices.translate(-0.5, up - 0.5, 0);

            EBEOtherUtils.renderBakedModel(buffers, state, matrices, signModel, 15728880, OverlayTexture.NO_OVERLAY);

            matrices.popPose();
            ci.cancel();
        }
    }

}
