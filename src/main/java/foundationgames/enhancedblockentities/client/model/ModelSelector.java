package foundationgames.enhancedblockentities.client.model;

import com.google.common.collect.Lists;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import foundationgames.enhancedblockentities.common.util.ModelStateHolder;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.GeomRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public abstract class ModelSelector {

    private static final List<ModelSelector> REGISTRY = Lists.newArrayList();

    public static final ModelSelector STATE_HOLDER_SELECTOR = new ModelSelector() {
        @Override
        public void writeModelIndices(BlockGetter view, BlockState state, BlockPos pos, Supplier<RandomSource> rand, @Nullable GeomRenderContext ctx, int[] indices) {
            if(view.getBlockEntity(pos) instanceof ModelStateHolder stateHolder) {
                indices[0] = stateHolder.ebe$getModelState();
                return;
            }
            indices[0] = 0;
        }
    };

    public static final ModelSelector CHEST_WITH_CHRISTMAS = new ModelSelector() {
        @Override
        public int getParticleModelIndex() {
            return EBEOtherUtils.isChristmas() ? 2 : 0;
        }

        @Override
        public void writeModelIndices(BlockGetter view, BlockState state, BlockPos pos, Supplier<RandomSource> rand, @Nullable GeomRenderContext ctx, int[] indices) {
            if(view.getBlockEntity(pos) instanceof ModelStateHolder stateHolder) {
                indices[0] = stateHolder.ebe$getModelState() + this.getParticleModelIndex();
                return;
            }
            indices[0] = this.getParticleModelIndex();
        }
    };

    public static final ModelSelector BELL = STATE_HOLDER_SELECTOR;

    public static final ModelSelector SHULKER_BOX = STATE_HOLDER_SELECTOR;

    public int getParticleModelIndex() {
        return 0;
    }

    public abstract void writeModelIndices(BlockGetter view, BlockState state, BlockPos pos, Supplier<RandomSource> rand, @Nullable GeomRenderContext ctx, int[] indices);

    public final int id;
    public final int displayedModelCount;

    public ModelSelector(int displayedModelCount) {
        this.id = REGISTRY.size();
        this.displayedModelCount = displayedModelCount;
        REGISTRY.add(this);
    }

    public ModelSelector() {
        this(1);
    }

    public static ModelSelector fromId(int id) {
        return REGISTRY.get(id);
    }


}
