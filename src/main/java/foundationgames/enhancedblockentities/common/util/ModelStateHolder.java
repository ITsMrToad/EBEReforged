package foundationgames.enhancedblockentities.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface ModelStateHolder {

    int ebe$getModelState();

    void ebe$applyModelState(int state);

    default void setModelState(int state, Level world, BlockPos pos) {
        if (!world.isClientSide()) {
            return;
        }

        this.ebe$applyModelState(state);
        EBEOtherUtils.rebuildChunk(world, pos);
    }
}
