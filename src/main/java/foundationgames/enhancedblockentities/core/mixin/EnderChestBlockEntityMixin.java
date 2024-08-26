package foundationgames.enhancedblockentities.core.mixin;

import foundationgames.enhancedblockentities.client.renderer.entity.ChestBlockEntityRendererOverride;
import foundationgames.enhancedblockentities.common.util.ModelStateHolder;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderChestBlockEntity.class)
public abstract class EnderChestBlockEntityMixin implements ModelStateHolder {

    @Unique private int ebe$modelState = 0;

    @Inject(method = "lidAnimateTick", at = @At(value = "TAIL"))
    private static void enhanced_bes$listenForOpenClose(Level world, BlockPos pos, BlockState state, EnderChestBlockEntity blockEntity, CallbackInfo ci) {
        var lid = ChestBlockEntityRendererOverride.getAnimationProgress(blockEntity, 0.5f);
        int mState = lid.getOpenNess(0.5f) > 0 ? 1 : 0;

        if (EBE.checkValue(cfg -> cfg.renderEnhancedChests) && ((ModelStateHolder) blockEntity).ebe$getModelState() != mState) {
            ((ModelStateHolder) blockEntity).setModelState(mState, world, pos);
        }
    }

    @Override
    public int ebe$getModelState() {
        return this.ebe$modelState;
    }

    @Override
    public void ebe$applyModelState(int state) {
        this.ebe$modelState = state;
    }

}
