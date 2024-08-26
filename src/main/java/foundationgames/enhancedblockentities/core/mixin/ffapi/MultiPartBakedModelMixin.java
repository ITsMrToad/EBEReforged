package foundationgames.enhancedblockentities.core.mixin.ffapi;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.UltimateBakedModel;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.GeomRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mixin(MultiPartBakedModel.class)
public abstract class MultiPartBakedModelMixin implements UltimateBakedModel {

    @Shadow @Final private Map<BlockState, BitSet> selectorCache;
    @Shadow @Final private List<Pair<Predicate<BlockState>, BakedModel>> selectors;

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, GeomRenderContext context) {
        AtomicReference<BitSet> bitSet = new AtomicReference<>(this.selectorCache.get(state));
        if (bitSet.get() == null) {
            bitSet.set(new BitSet());
            this.selectors.forEach(pair -> {
                if (pair.getLeft().test(state)) {
                    bitSet.get().set(this.selectors.indexOf(pair));
                }
            });
            this.selectorCache.put(state, bitSet.get());
        }

        RandomSource random = randomSupplier.get();
        long randomSeed = random.nextLong();
        Supplier<RandomSource> subModelRandomSupplier = () -> {
            random.setSeed(randomSeed);
            return random;
        };

        for (int i = 0; i < this.selectors.size(); i++) {
            if (bitSet.get().get(i)) {
                ((UltimateBakedModel) this.selectors.get(i).getRight()).emitBlockQuads(blockView, state, pos, subModelRandomSupplier, context);
            }
        }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, GeomRenderContext context) {}

}
