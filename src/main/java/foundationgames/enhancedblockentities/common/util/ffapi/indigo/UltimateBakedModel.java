package foundationgames.enhancedblockentities.common.util.ffapi.indigo;

import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.TriState;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.GeomRenderContext;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material.MaterialLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadEmitter;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public interface UltimateBakedModel {

    MaterialLookup MATERIAL_STANDARD = IndigoRenderer.INSTANCE.materialFinder().find();
    MaterialLookup MATERIAL_NO_AO = IndigoRenderer.INSTANCE.materialFinder().ambientOcclusion(TriState.FALSE).find();

    static void emitBlockQuads(BakedModel model, @Nullable BlockState state, Supplier<RandomSource> randomSupplier, GeomRenderContext context, QuadEmitter emitter) {
        MaterialLookup defaultMaterial = model.useAmbientOcclusion() ? MATERIAL_STANDARD : MATERIAL_NO_AO;

        for (int i = 0; i <= 6; i++) {
            Direction cullFace = EBEOtherUtils.FACES[i];

            if (!context.hasTransform() && context.isFaceCulled(cullFace)) {
                continue;
            }
            List<BakedQuad> quads = model.getQuads(state, cullFace, randomSupplier.get(), ModelData.EMPTY, null);

            for (final BakedQuad q : quads) {
                emitter.fromVanilla(q, defaultMaterial, cullFace);
                emitter.emit();
            }
        }
    }

    static void emitItemQuads(BakedModel model, @Nullable BlockState state, Supplier<RandomSource> randomSupplier, GeomRenderContext context) {
        QuadEmitter emitter = context.getEmitter();
        for (int i = 0; i <= 6; i++) {
            Direction cullFace = EBEOtherUtils.FACES[i];
            List<BakedQuad> quads = model.getQuads(state, cullFace, randomSupplier.get(), ModelData.EMPTY, null);
            for (final BakedQuad q : quads) {
                emitter.fromVanilla(q, MATERIAL_STANDARD, cullFace);
                emitter.emit();
            }
        }
    }

    default void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, GeomRenderContext context) {
        emitBlockQuads((BakedModel) this, state, randomSupplier, context, context.getEmitter());
    }

    default void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, GeomRenderContext context) {
        emitItemQuads((BakedModel) this, null, randomSupplier, context);
    }
}
