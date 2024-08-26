package foundationgames.enhancedblockentities.core.mixin;

import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DecoratedPotBlockEntity.class)
public abstract class DecoratedPotBlockEntityMixin {

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void updateChunkOnPatternsLoaded(CompoundTag nbt, CallbackInfo ci) {
        DecoratedPotBlockEntity self = (DecoratedPotBlockEntity) (Object) this;
        if (self.getLevel() != null && self.getLevel().isClientSide()) {
            EBEOtherUtils.rebuildChunk(self.getLevel(), self.getBlockPos());
        }
    }
}
