package foundationgames.enhancedblockentities.core.mixin;

import foundationgames.enhancedblockentities.common.util.ModelStateHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class ShulkerBoxBlockEntityMixin extends BlockEntity implements ModelStateHolder {

    @Shadow public abstract float getProgress(float f);
    @Unique private int ebe$modelState = 0;

    public ShulkerBoxBlockEntityMixin(BlockEntityType<?> bet, BlockPos blockPos, BlockState state) {
        super(bet, blockPos, state);
    }

    @Inject(method = "updateAnimation", at = @At("TAIL"))
    private void updateModelState(Level world, BlockPos pos, BlockState state, CallbackInfo ci) {
        int mState = this.getProgress(0) > 0 ? 1 : 0;
        if (mState != this.ebe$modelState) {
            this.setModelState(mState, world, pos);
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
