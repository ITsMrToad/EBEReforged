package foundationgames.enhancedblockentities.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import foundationgames.enhancedblockentities.client.model.ModelIdentifiers;
import foundationgames.enhancedblockentities.client.renderer.BlockEntityRendererOverride;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotBlockEntity;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;

import java.util.Map;
import java.util.Optional;

public class DecoratedPotBlockEntityRendererOverride extends BlockEntityRendererOverride {

    public static final float WOBBLE_STRENGTH = 1f / 64;

    private BakedModel baseModel = null;
    private Map<ResourceKey<String>, BakedModel[]> potPatternModels = null;


    @Override
    public void render(BlockEntityRenderer<BlockEntity> renderer, BlockEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        this.tryGetModels();

        if (blockEntity instanceof DecoratedPotBlockEntity pot) {
            matrices.pushPose();

            Direction dir = pot.getDirection();

            matrices.translate(0.5f, 0, 0.5f);
            matrices.mulPose(Axis.YP.rotationDegrees(180 - dir.toYRot()));
            matrices.translate(-0.5f, 0, -0.5f);

            DecoratedPotBlockEntity.Decorations sherds = pot.getDecorations();

            EBEOtherUtils.renderBakedModel(vertexConsumers, blockEntity.getBlockState(), matrices, this.baseModel, light, overlay);
            EBEOtherUtils.renderBakedModel(vertexConsumers, blockEntity.getBlockState(), matrices, this.potPatternModels.get(this.getKey(sherds.back()).orElse(DecoratedPotPatterns.BASE))[0], light, overlay);
            EBEOtherUtils.renderBakedModel(vertexConsumers, blockEntity.getBlockState(), matrices, this.potPatternModels.get(this.getKey(sherds.left()).orElse(DecoratedPotPatterns.BASE))[1], light, overlay);
            EBEOtherUtils.renderBakedModel(vertexConsumers, blockEntity.getBlockState(), matrices, this.potPatternModels.get(this.getKey(sherds.right()).orElse(DecoratedPotPatterns.BASE))[2], light, overlay);
            EBEOtherUtils.renderBakedModel(vertexConsumers, blockEntity.getBlockState(), matrices, this.potPatternModels.get(this.getKey(sherds.front()).orElse(DecoratedPotPatterns.BASE))[3], light, overlay);

            matrices.popPose();
        }
    }

    @Override
    public void onModelsReload() {
        this.baseModel = null;
        this.potPatternModels = null;
    }

    private Optional<ResourceKey<String>> getKey(Item item) {
        if (item == Items.BRICK) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(DecoratedPotPatterns.getResourceKey(item));
        }
    }

    private void tryGetModels() {
        ModelManager models = Minecraft.getInstance().getModelManager().getBlockModelShaper().getModelManager();

        if (this.baseModel == null) {
            this.baseModel = models.getModel(ModelIdentifiers.DECORATED_POT_BASE);
        }

        if (this.potPatternModels == null) {
            ImmutableMap.Builder<ResourceKey<String>, BakedModel[]> builder = ImmutableMap.builder();

            BuiltInRegistries.DECORATED_POT_PATTERNS.entrySet().stream().map(Map.Entry::getKey).forEach(k -> {
                ResourceLocation[] patternModelIDs = ModelIdentifiers.POTTERY_PATTERNS.get(k);
                BakedModel[] patternPerFaceModels = new BakedModel[patternModelIDs.length];
                for (int i = 0; i < patternModelIDs.length; i++) {
                    patternPerFaceModels[i] = models.getModel(patternModelIDs[i]);
                }
                builder.put(k, patternPerFaceModels);
            });
            this.potPatternModels = builder.build();
        }
    }
}
