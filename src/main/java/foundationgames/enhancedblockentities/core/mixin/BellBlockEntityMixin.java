package foundationgames.enhancedblockentities.core.mixin;

import foundationgames.enhancedblockentities.common.util.ModelStateHolder;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(BellBlockEntity.class)
public abstract class BellBlockEntityMixin extends BlockEntity implements ModelStateHolder {

    @Unique private int ebe$modelState = 0;

    public BellBlockEntityMixin(BlockEntityType<?> bet, BlockPos blockPos, BlockState state) {
        super(bet, blockPos, state);
    }

    @ModifyVariable(method = "tick", at = @At(value = "JUMP", opcode = Opcodes.IF_ICMPLT, ordinal = 0, shift = At.Shift.BEFORE), index = 3, argsOnly = true)
    private static BellBlockEntity enhanced_bes$listenForStopRinging(BellBlockEntity blockEntity) {
        int mState = blockEntity.ticks > 0 ? 1 : 0;
        Level lvl = blockEntity.getLevel();
        if (EBE.checkValue(cfg -> cfg.renderEnhancedBells) && lvl != null && ((ModelStateHolder) blockEntity).ebe$getModelState() != mState) {
            ((ModelStateHolder)blockEntity).setModelState(mState, lvl, blockEntity.getBlockPos());
        }
        return blockEntity;
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
