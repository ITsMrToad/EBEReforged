package foundationgames.enhancedblockentities.integration;

import com.mr_toad.lib.api.integration.Integration;
import foundationgames.enhancedblockentities.client.model.ModelSelector;
import foundationgames.enhancedblockentities.client.model.dynamic.DynamicModelEffects;
import foundationgames.enhancedblockentities.client.model.dynamic.DynamicModelHandler;
import foundationgames.enhancedblockentities.client.model.dynamic.DynamicUnbakedModel;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import foundationgames.enhancedblockentities.common.util.SinytraNotFoundException;
import foundationgames.enhancedblockentities.common.util.mfrapi.loader.plugin.MFModelLoadingPluginManager;
import foundationgames.enhancedblockentities.core.EBE;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.IndigoRenderer;
import net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public class SinytraIntegration {

    public static final Integration SINYTRA = () -> "connectormod";

    public static void registerPlugin(ResourceLocation id, Supplier<DynamicUnbakedModel> model) {
        if (!SINYTRA.isLoaded()) {
            MFModelLoadingPluginManager.registerPlugin(new DynamicModelHandler(id, model));
            return;
        }

        try {
            Class<?> clazz = Class.forName("net.fabricmc.fabric.impl.client.model.loading.ModelLoadingPluginManager");
            Class<?> clazz1 = Class.forName("net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin");

            SinytraModelHandler sinytraModelHandler = new SinytraModelHandler(id, model);
            ModelLoadingPlugin sinytraPlugin = sinytraModelHandler::onInitializeModelLoader;
            ModelLoadingPluginManager.registerPlugin(sinytraPlugin);
        } catch (ClassNotFoundException ignored) {}
    }

    public static void tryDetectSinytra() {
        boolean flag = SINYTRA.isLoaded();
        if (!flag && EBE.checkValue(cfg -> cfg.forceMFAPICompat)) {
            throw new SinytraNotFoundException("Sinytra not found! Disable 'forceMFAPICompat' in 'config/enhanced_bes.json'");
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SinytraModelHandler implements  ModelResolver {
        private final Supplier<DynamicUnbakedModel> model;
        private final ResourceLocation id;

        public SinytraModelHandler(ResourceLocation id, Supplier<DynamicUnbakedModel> model) {
            this.model = model;
            this.id = id;
        }

        public void onInitializeModelLoader(ModelLoadingPlugin.Context ctx) {
            ctx.resolveModel().register(this);
        }

        @Override
        public @Nullable UnbakedModel resolveModel(ModelResolver.Context ctx) {
            ModelManager manager = Minecraft.getInstance().getModelManager();
            BakedModel model = manager.getModel(this.id);
            if (model != manager.getMissingModel()) {
                return this.model.get();
            }
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    @MethodsReturnNonnullByDefault
    @OnlyIn(Dist.CLIENT)
    public static class SinytraBakedModel implements BakedModel, FabricBakedModel {

        private final BakedModel[] models;
        private final ModelSelector selector;
        private final DynamicModelEffects effects;

        private final int[] activeModelIndices;
        private final BakedModel[] displayedModels;

        public SinytraBakedModel(BakedModel[] models, ModelSelector selector, DynamicModelEffects effects) {
            this.models = models;
            this.selector = selector;
            this.effects = effects;

            this.activeModelIndices = new int[selector.displayedModelCount];
            this.displayedModels = new BakedModel[selector.displayedModelCount];
        }

        @Override
        public void emitBlockQuads(BlockAndTintGetter view, BlockState state, BlockPos blockPos, Supplier<RandomSource> rng, RenderContext context) {
            QuadEmitter emitter = context.getEmitter();
            RenderMaterial mat = IndigoRenderer.MATERIAL_STANDARD;

            this.getSelector().writeModelIndices(view, state, blockPos, rng, this.activeModelIndices);
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
        public void emitItemQuads(ItemStack itemStack, Supplier<RandomSource> supplier, RenderContext renderContext) {}

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
            return this.models[0].getQuads(state, side, rand, data, renderType);
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            return this.models[0].getQuads(state, side, rand);
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
            return this.models[this.getSelector().getParticleModelIndex()].getParticleIcon(ModelData.EMPTY);
        }

        @Override
        public ItemTransforms getTransforms() {
            return ItemTransforms.NO_TRANSFORMS;
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
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
}
