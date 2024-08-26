package foundationgames.enhancedblockentities.core.mixin.accessor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SignRenderer.class)
public interface SignRendererInvoker {

    @Invoker("renderSignText")
    void renderText(BlockPos blockPos, SignText text, PoseStack stack, MultiBufferSource source, int x, int y, int z, boolean b);

    @Invoker("translateSign")
    void rotateText(PoseStack stack, float f, BlockState state);

}
