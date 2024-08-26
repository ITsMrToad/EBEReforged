package foundationgames.enhancedblockentities.client.model.dynamic;

import foundationgames.enhancedblockentities.client.model.ModelSelector;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.IndigoRenderer;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.UltimateBakedModel;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.GeomRenderContext;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material.MaterialLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadEmitter;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("deprecation")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class DynamicBakedModel implements BakedModel, UltimateBakedModel {

    private final BakedModel[] models;
    private final ModelSelector selector;
    private final DynamicModelEffects effects;

    private final int[] activeModelIndices;
    private final BakedModel[] displayedModels;

    public DynamicBakedModel(BakedModel[] models, ModelSelector selector, DynamicModelEffects effects) {
        this.models = models;
        this.selector = selector;
        this.effects = effects;

        this.activeModelIndices = new int[selector.displayedModelCount];
        this.displayedModels = new BakedModel[selector.displayedModelCount];
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter view, BlockState state, BlockPos blockPos, Supplier<RandomSource> rng, GeomRenderContext context) {
        QuadEmitter emitter = context.getEmitter();
        MaterialLookup mat = IndigoRenderer.INSTANCE.materialById(IndigoRenderer.DEFAULT);

        this.getSelector().writeModelIndices(view, state, blockPos, rng, context, this.activeModelIndices);
        for (int i = 0; i < this.activeModelIndices.length; i++) {
            int modelIndex = this.activeModelIndices[i];

            if (modelIndex >= 0) {
                this.displayedModels[i] = this.models[modelIndex];
            } else {
                this.displayedModels[i] = null;
            }
        }

        for (int i = 0; i <= 6; i++) {
            Direction dir = EBEOtherUtils.FACES[i];
            for (BakedModel model : this.displayedModels) if (model != null) {
                for (BakedQuad quad : model.getQuads(state, dir, rng.get())) {
                    emitter.fromVanilla(quad, mat, dir);
                    emitter.emit();
                }
            }
        }
    }

    @Override
    public void emitItemQuads(ItemStack itemStack, Supplier<RandomSource> supplier, GeomRenderContext renderContext) {}

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource random) {
        return this.models[0].getQuads(state, face, random, ModelData.EMPTY, null);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return getEffects().ambientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.models[getSelector().getParticleModelIndex()].getParticleIcon(ModelData.EMPTY);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ItemTransforms getTransforms() {
        return null;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ItemOverrides getOverrides() {
        return null;
    }

    public BakedModel[] getModels() {
        return this.models;
    }

    public ModelSelector getSelector() {
        return this.selector;
    }

    public DynamicModelEffects getEffects() {
        return this.effects;
    }
}
