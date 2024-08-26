package foundationgames.enhancedblockentities.core.mixin.ffapi;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.UltimateBakedModel;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.GeomRenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.function.Supplier;

@Mixin(WeightedBakedModel.class)
public abstract class WeightedBakedModelMixin implements UltimateBakedModel {

    @Shadow @Final private List<WeightedEntry.Wrapper<BakedModel>> list;
    @Shadow @Final private int totalWeight;

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, GeomRenderContext context) {
        WeightedRandom.getWeightedItem(this.list, Math.abs((int) randomSupplier.get().nextLong()) % this.totalWeight).ifPresent(selected -> ((UltimateBakedModel) selected.getData()).emitBlockQuads(blockView, state, pos, () -> {
            RandomSource random = randomSupplier.get();
            random.nextLong();
            return random;
        }, context));
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, GeomRenderContext context) {
        WeightedRandom.getWeightedItem(this.list, Math.abs((int) randomSupplier.get().nextLong()) % this.totalWeight).ifPresent(selected -> ((UltimateBakedModel) selected.getData()).emitItemQuads(stack, () -> {
            RandomSource random = randomSupplier.get();
            random.nextLong();
            return random;
        }, context));
    }

}
