package foundationgames.enhancedblockentities.core.mixin;

import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

    @Shadow public abstract Block getBlock();

    @Inject(method = "getRenderShape", at = @At("HEAD"), cancellable = true)
    public void enhanced_bes$overrideRenderType(CallbackInfoReturnable<RenderShape> cir) {
        Block block = this.getBlock();
        if (EBE.BLOCKS.contains(block)) {
            cir.setReturnValue(RenderShape.MODEL);
        }
    }
}
