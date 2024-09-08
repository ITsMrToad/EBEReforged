package foundationgames.enhancedblockentities.common.util.mfrapi.block;

import com.mojang.blaze3d.vertex.VertexConsumer;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.UltimateBakedModel;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.ao.AoCalculator;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.api.TriState;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.api.geom.material.MaterialLookup;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.api.geom.quad.QuadEmitter;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.helper.ColorHelper;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.helper.GeometryHelper;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.work.AbstractGeomRenderContext;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.work.quad.OpenMutableQuadLookup;
import foundationgames.enhancedblockentities.common.util.mfrapi.indigo.work.quad.OpenQuadLookup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;
import org.joml.Vector4f;

import org.jetbrains.annotations.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class ModernBlockRenderer extends AbstractGeomRenderContext {

    public final Info blockInfo = new Info();

    private final Vector4f posVec = new Vector4f();
    private final Vector3f normalVec = new Vector3f();

    private final BlockPos.MutableBlockPos lightPos = new BlockPos.MutableBlockPos();

    private final OpenMutableQuadLookup editorQuad = new OpenMutableQuadLookup() {
        {
            this.data = new int[OpenQuadLookup.TOTAL_STRIDE];
            this.clear();
        }

        @Override
        public void emitDirectly() {
            ModernBlockRenderer.this.quad(this);
        }
    };

    public final BiConsumer<BakedModel, BlockState> emitter = (model, state) -> UltimateBakedModel.emitBlockQuads(model, state, blockInfo.randomSupplier, ModernBlockRenderer.this, this.editorQuad);

    protected final AoCalculator aoCalc;

    public ModernBlockRenderer() {
        this.aoCalc = this.createAoCalc(this.blockInfo);
    }

    protected abstract VertexConsumer getVertexConsumer();

    protected abstract AoCalculator createAoCalc(Info blockInfo);

    @Override
    public QuadEmitter getEmitter() {
        this.editorQuad.clear();
        return this.editorQuad;
    }

    @Override
    public boolean isFaceCulled(@Nullable Direction face) {
        return !this.blockInfo.shouldDrawFace(face);
    }

    protected void quad(OpenMutableQuadLookup quad) {
        if (!this.transform(quad)) {
            return;
        }

        if (this.isFaceCulled(quad.cullFace())) {
            return;
        }

        MaterialLookup mat = quad.material();
        int colorIndex = mat.disableColorIndex() ? -1 : quad.colorIndex();
        TriState aoMode = mat.ambientOcclusion();
        boolean ao = blockInfo.useAo && (aoMode == TriState.TRUE || (aoMode == TriState.DEFAULT && blockInfo.defaultAo));
        boolean emissive = mat.emissive();
        VertexConsumer vertexConsumer = this.getVertexConsumer();

        if (colorIndex != -1) {
            final int blockColor = blockInfo.blockColor(colorIndex);
            for (int i = 0; i < 4; i++) {
                quad.color(i, ColorHelper.multiplyColor(blockColor, quad.color(i)));
            }
        }

        Vector4f posVec = this.posVec;
        Vector3f normalVec = this.normalVec;
        boolean useNormals = quad.hasVertexNormals();

        if (useNormals) {
            quad.populateMissingNormals();
        } else {
            normalVec.set(quad.faceNormal());
            normalVec.mul(normalMatrix);
        }

        for (int i = 0; i < 4; i++) {
            posVec.set(quad.x(i), quad.y(i), quad.z(i), 1.0f);
            posVec.mul(matrix);
            vertexConsumer.vertex(posVec.x(), posVec.y(), posVec.z());

            final int color = quad.color(i);
            vertexConsumer.color((color >>> 16) & 0xFF, (color >>> 8) & 0xFF, color & 0xFF, (color >>> 24) & 0xFF);
            vertexConsumer.uv(quad.u(i), quad.v(i));
            vertexConsumer.overlayCoords(this.overlay);
            vertexConsumer.uv2(quad.lightmap(i));

            if (useNormals) {
                quad.copyNormal(i, normalVec);
                normalVec.mul(this.normalMatrix);
            }

            vertexConsumer.normal(normalVec.x(), normalVec.y(), normalVec.z());
            vertexConsumer.endVertex();
        }

        if (ao) {
            this.aoCalc.compute(quad);
            if (emissive) {
                for (int i = 0; i < 4; i++) {
                    quad.color(i, ColorHelper.multiplyRGB(quad.color(i), this.aoCalc.ao[i]));
                    quad.lightmap(i, LightTexture.FULL_BRIGHT);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    quad.color(i, ColorHelper.multiplyRGB(quad.color(i), this.aoCalc.ao[i]));
                    quad.lightmap(i, ColorHelper.maxBrightness(quad.lightmap(i), this.aoCalc.light[i]));
                }
            }
        } else {
            boolean hasShade = quad.hasShade();

            if (quad.hasAllVertexNormals()) {
                for (int i = 0; i < 4; i++) {
                    float shade = this.normalShade(quad.normalX(i), quad.normalY(i), quad.normalZ(i), hasShade);
                    quad.color(i, ColorHelper.multiplyRGB(quad.color(i), shade));
                }
            } else {
                float faceShade;
                if ((quad.geometryFlags() & GeometryHelper.AXIS_ALIGNED_FLAG) != 0) {
                    faceShade = this.blockInfo.blockView.getShade(quad.lightFace(), hasShade);
                } else {
                    Vector3f faceNormal = quad.faceNormal();
                    faceShade = this.normalShade(faceNormal.x, faceNormal.y, faceNormal.z, hasShade);
                }

                if (quad.hasVertexNormals()) {
                    for (int i = 0; i < 4; i++) {
                        float shade;

                        if (quad.hasNormal(i)) {
                            shade = this.normalShade(quad.normalX(i), quad.normalY(i), quad.normalZ(i), hasShade);
                        } else {
                            shade = faceShade;
                        }

                        quad.color(i, ColorHelper.multiplyRGB(quad.color(i), shade));
                    }
                } else {
                    if (faceShade != 1.0f) {
                        for (int i = 0; i < 4; i++) {
                            quad.color(i, ColorHelper.multiplyRGB(quad.color(i), faceShade));
                        }
                    }
                }
            }
            if (emissive) {
                for (int i = 0; i < 4; i++) {
                    quad.lightmap(i, LightTexture.FULL_BRIGHT);
                }
            } else {
                int brightness = this.flatBrightness(quad, this.blockInfo.blockState, this.blockInfo.blockPos);
                for (int i = 0; i < 4; i++) {
                    quad.lightmap(i, ColorHelper.maxBrightness(quad.lightmap(i), brightness));
                }
            }
        }
    }

    private float normalShade(float normalX, float normalY, float normalZ, boolean hasShade) {
        float sum = 0;
        float div = 0;

        if (normalX > 0) {
            sum += normalX * blockInfo.blockView.getShade(Direction.EAST, hasShade);
            div += normalX;
        } else if (normalX < 0) {
            sum += -normalX * blockInfo.blockView.getShade(Direction.WEST, hasShade);
            div -= normalX;
        }

        if (normalY > 0) {
            sum += normalY * blockInfo.blockView.getShade(Direction.UP, hasShade);
            div += normalY;
        } else if (normalY < 0) {
            sum += -normalY * blockInfo.blockView.getShade(Direction.DOWN, hasShade);
            div -= normalY;
        }

        if (normalZ > 0) {
            sum += normalZ * blockInfo.blockView.getShade(Direction.SOUTH, hasShade);
            div += normalZ;
        } else if (normalZ < 0) {
            sum += -normalZ * blockInfo.blockView.getShade(Direction.NORTH, hasShade);
            div -= normalZ;
        }

        return sum / div;
    }

    private int flatBrightness(OpenMutableQuadLookup quad, BlockState state, BlockPos pos) {
        this.lightPos.set(pos);

        if (quad.cullFace() != null) {
            this.lightPos.move(quad.cullFace());
        } else {
            final int flags = quad.geometryFlags();

            if ((flags & GeometryHelper.LIGHT_FACE_FLAG) != 0 || ((flags & GeometryHelper.AXIS_ALIGNED_FLAG) != 0 && state.isSolidRender(this.blockInfo.blockView, pos))) {
                this.lightPos.move(quad.lightFace());
            }
        }
        return LevelRenderer.getLightColor(this.blockInfo.blockView, state, this.lightPos);
    }

    public static class Info {

        private final BlockColors blockColorMap = Minecraft.getInstance().getBlockColors();
        private final BlockPos.MutableBlockPos searchPos = new BlockPos.MutableBlockPos();

        public BlockAndTintGetter blockView;
        public BlockPos blockPos;
        public BlockState blockState;

        public boolean useAo;
        public boolean defaultAo;
        public boolean recomputeSeed;
        public RandomSource random;
        public long seed;

        public final Supplier<RandomSource> randomSupplier = () -> {
            long seed = this.seed;

            if (this.recomputeSeed) {
                seed = this.blockState.getSeed(this.blockPos);
                this.seed = seed;
                this.recomputeSeed = false;
            }

            RandomSource random = this.random;
            random.setSeed(seed);
            return random;
        };

        private boolean enableCulling;
        private int cullCompletionFlags;
        private int cullResultFlags;

        public void prepareForWorld(BlockAndTintGetter blockView, boolean enableCulling) {
            this.blockView = blockView;
            this.enableCulling = enableCulling;
        }

        public void prepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAo) {
            this.blockPos = blockPos;
            this.blockState = blockState;
            this.useAo = Minecraft.useAmbientOcclusion();
            this.defaultAo = useAo && modelAo && blockState.getLightEmission(this.blockView, blockPos) == 0;
            this.cullCompletionFlags = 0;
            this.cullResultFlags = 0;
        }

        public void release() {
            this.blockView = null;
            this.blockPos = null;
            this.blockState = null;
        }

        public int blockColor(int colorIndex) {
            return 0xFF000000 | this.blockColorMap.getColor(this.blockState, this.blockView, this.blockPos, colorIndex);
        }

        public boolean shouldDrawFace(@Nullable Direction face) {
            if (face == null || !this.enableCulling) {
                return true;
            }

            final int mask = 1 << face.get3DDataValue();

            if ((this.cullCompletionFlags & mask) == 0) {
                this.cullCompletionFlags |= mask;

                if (Block.shouldRenderFace(this.blockState, this.blockView, this.blockPos, face, this.searchPos.setWithOffset(this.blockPos, face))) {
                    this.cullResultFlags |= mask;
                    return true;
                } else {
                    return false;
                }
            } else {
                return (this.cullResultFlags & mask) != 0;
            }
        }
    }
}
