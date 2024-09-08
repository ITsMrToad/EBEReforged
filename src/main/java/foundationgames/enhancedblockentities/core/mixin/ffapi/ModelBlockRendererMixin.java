package foundationgames.enhancedblockentities.core.mixin.mfrapi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundationgames.enhancedblockentities.common.util.mfrapi.block.ModernBlockRendererImpl;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelBlockRenderer.class)
public abstract class ModelBlockRendererMixin {

    @Unique
    private final ThreadLocal<ModernBlockRendererImpl> ebe$ctx = ThreadLocal.withInitial(ModernBlockRendererImpl::new);

    @Inject(method = "tesselateBlock(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/client/resources/model/BakedModel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;JI)V", at = @At("HEAD"), cancellable = true)
    private void hookRender(BlockAndTintGetter getter, BakedModel model, BlockState state, BlockPos pos, PoseStack stack, VertexConsumer buffer, boolean cull, RandomSource rand, long seed, int overlay, CallbackInfo ci) {
        ModernBlockRendererImpl context = this.ebe$ctx.get();
        context.render(getter, model, state, pos, stack, buffer, cull, rand, seed, overlay);
        ci.cancel();
    }
}
