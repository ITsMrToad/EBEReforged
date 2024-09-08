package foundationgames.enhancedblockentities.common.util.mfrapi.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.UltimateBakedModel;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.ao.AoCalculator;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;

public class ModernBlockRendererImpl extends ModernBlockRenderer {
    private VertexConsumer vertexConsumer;

    @Override
    protected VertexConsumer getVertexConsumer() {
        return this.vertexConsumer;
    }

    @Override
    protected AoCalculator createAoCalc(Info blockInfo) {
        return new AoCalculator(blockInfo) {
            @Override
            public int light(BlockPos pos, BlockState state) {
                int i = this.blockInfo.blockView.getBrightness(LightLayer.SKY, pos);
                int j = this.blockInfo.blockView.getBrightness(LightLayer.BLOCK, pos);
                int k = state.getLightEmission(this.blockInfo.blockView, pos);
                if (j < k) j = k;
                return i << 20 | j << 4;
            }

            @Override
            public float ao(BlockPos pos, BlockState state) {
                return state.getLightEmission(this.blockInfo.blockView, pos) == 0F ? state.getShadeBrightness(this.blockInfo.blockView, pos) : 1.0F;
            }
        };
    }

    public void render(BlockAndTintGetter blockView, BakedModel model, BlockState state, BlockPos pos, PoseStack stack, VertexConsumer buffer, boolean cull, RandomSource random, long seed, int overlay) {
        this.vertexConsumer = buffer;
        this.matrix = stack.last().pose();
        this.normalMatrix = stack.last().normal();
        this.overlay = overlay;

        this.blockInfo.random = random;
        this.blockInfo.seed = seed;
        this.blockInfo.recomputeSeed = false;

        this.aoCalc.completionFlags = 0;
        this.blockInfo.prepareForWorld(blockView, cull);
        this.blockInfo.prepareForBlock(state, pos, model.useAmbientOcclusion());

        ((UltimateBakedModel) model).emitBlockQuads(blockView, state, pos, this.blockInfo.randomSupplier, this);

        this.blockInfo.release();
        this.blockInfo.random = null;
        this.vertexConsumer = null;
    }

}
