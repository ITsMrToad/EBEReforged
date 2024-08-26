package foundationgames.enhancedblockentities.client.model;

import com.google.common.collect.Lists;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.GeomRenderContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class DecoratedPotModelSelector extends ModelSelector {

    private final List<ResourceKey<String>> potteryPatterns;

    public DecoratedPotModelSelector() {
        super(5);

        this.potteryPatterns = Lists.newArrayList(BuiltInRegistries.DECORATED_POT_PATTERNS.entrySet().stream().map(Map.Entry::getKey).toList());
    }

    public ResourceLocation[] createModelIDs() {
        ModelIdentifiers.refreshPotteryPatterns();

        ResourceLocation[] ids = new ResourceLocation[1 + potteryPatterns.size() * 4];
        ids[0] = ModelIdentifiers.DECORATED_POT_BASE;

        int idIndex = 1;
        for (int dirIndex = 0; dirIndex < 4; dirIndex++) {
            for (var pattern : this.potteryPatterns) {
                ids[idIndex] = ModelIdentifiers.POTTERY_PATTERNS.get(pattern)[dirIndex];

                idIndex++;
            }
        }

        return ids;
    }

    @Override
    public void writeModelIndices(BlockGetter view, BlockState state, BlockPos pos, Supplier<RandomSource> rand, @Nullable GeomRenderContext ctx, int[] indices) {
        int patternCount = potteryPatterns.size();

        indices[0] = 0;
        if (view.getBlockEntity(pos) instanceof DecoratedPotBlockEntity pot) {
            DecoratedPotBlockEntity.Decorations sherds = pot.getDecorations();
            indices[1] = 1 + this.getPatternIndex(sherds.back(), patternCount);
            indices[2] = 1 + this.getPatternIndex(sherds.left(), patternCount) + patternCount;
            indices[3] = 1 + this.getPatternIndex(sherds.right(), patternCount) + patternCount * 2;
            indices[4] = 1 + this.getPatternIndex(sherds.front(), patternCount) + patternCount * 3;
            return;
        }

        for (int i = 0; i < 4; i++) {
            indices[1 + i] = 1 + patternCount * i;
        }
    }

    private int getPatternIndex(Item sherd, int max) {
        return Mth.clamp(this.potteryPatterns.indexOf(DecoratedPotPatterns.getResourceKey(sherd)), 0, max - 1);
    }
}
