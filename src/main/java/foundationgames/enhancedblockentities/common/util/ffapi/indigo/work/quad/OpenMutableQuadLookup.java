package foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.quad;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.IndigoRenderer;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material.MaterialLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadEmitter;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper.ColorHelper;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper.EncodingHelper;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper.NormalHelper;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper.TextureHelper;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.material.OpenMaterialRenderLookup;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import org.jetbrains.annotations.Nullable;

public abstract class OpenMutableQuadLookup extends OpenQuadLookup implements QuadEmitter {

    public abstract void emitDirectly();

    @Override
    public OpenMutableQuadLookup pos(int vertexIndex, float x, float y, float z) {
        int index = this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X;
        this.data[index] = Float.floatToRawIntBits(x);
        this.data[index + 1] = Float.floatToRawIntBits(y);
        this.data[index + 2] = Float.floatToRawIntBits(z);
        this.isGeometryInvalid = true;
        return this;
    }

    @Override
    public OpenMutableQuadLookup color(int vertexIndex, int color) {
        this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_COLOR] = color;
        return this;
    }

    @Override
    public OpenMutableQuadLookup uv(int vertexIndex, float u, float v) {
        final int i = this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_U;
        this.data[i] = Float.floatToRawIntBits(u);
        this.data[i + 1] = Float.floatToRawIntBits(v);
        return this;
    }

    @Override
    public OpenMutableQuadLookup spriteBake(TextureAtlasSprite sprite, int bakeFlags) {
        TextureHelper.bakeSprite(this, sprite, bakeFlags);
        return this;
    }

    @Override
    public OpenMutableQuadLookup lightmap(int vertexIndex, int lightmap) {
        this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_LIGHTMAP] = lightmap;
        return this;
    }

    protected void normalFlags(int flags) {
        this.data[this.baseIndex + HEADER_BITS] = EncodingHelper.normalFlags(this.data[this.baseIndex + HEADER_BITS], flags);
    }

    @Override
    public OpenMutableQuadLookup normal(int vertexIndex, float x, float y, float z) {
        this.normalFlags(normalFlags() | (1 << vertexIndex));
        this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL] = NormalHelper.packNormal(x, y, z);
        return this;
    }

    public final void populateMissingNormals() {
        int normalFlags = this.normalFlags();

        if (normalFlags == 0b1111) return;

        int packedFaceNormal = this.packedFaceNormal();

        for (int v = 0; v < 4; v++) {
            if ((normalFlags & (1 << v)) == 0) {
                this.data[this.baseIndex + v * VERTEX_STRIDE + VERTEX_NORMAL] = packedFaceNormal;
            }
        }

        this.normalFlags(0b1111);
    }

    @Override
    public final OpenMutableQuadLookup cullFace(@Nullable Direction face) {
        this.data[this.baseIndex + HEADER_BITS] = EncodingHelper.cullFace(this.data[this.baseIndex + HEADER_BITS], face);
        this.nominalFace(face);
        return this;
    }

    @Override
    public final OpenMutableQuadLookup nominalFace(@Nullable Direction face) {
        this.nominalFace = face;
        return this;
    }

    @Override
    public final OpenMutableQuadLookup material(MaterialLookup material) {
        if (material == null) {
            material = IndigoRenderer.MATERIAL_STANDARD;
        }

        this.data[this.baseIndex + HEADER_BITS] = EncodingHelper.material(data[baseIndex + HEADER_BITS], (OpenMaterialRenderLookup) material);
        return this;
    }

    @Override
    public final OpenMutableQuadLookup colorIndex(int colorIndex) {
        this.data[this.baseIndex + HEADER_COLOR_INDEX] = colorIndex;
        return this;
    }

    @Override
    public final OpenMutableQuadLookup tag(int tag) {
        this.data[this.baseIndex + HEADER_TAG] = tag;
        return this;
    }

    @Override
    public OpenMutableQuadLookup copyFrom(QuadLookup quad) {
        OpenQuadLookup q = (OpenQuadLookup) quad;
        q.computeGeometry();

        System.arraycopy(q.data, q.baseIndex, data, baseIndex, TOTAL_STRIDE);
        this.faceNormal.set(q.faceNormal);
        this.nominalFace = q.nominalFace;
        this.isGeometryInvalid = false;
        return this;
    }

    @Override
    public final OpenMutableQuadLookup fromVanilla(int[] quadData, int startIndex) {
        System.arraycopy(quadData, startIndex, data, baseIndex + HEADER_STRIDE, VANILLA_QUAD_STRIDE);
        this.isGeometryInvalid = true;

        int colorIndex = this.baseIndex + VERTEX_COLOR;

        for (int i = 0; i < 4; i++) {
            this.data[colorIndex] = ColorHelper.fromVanillaColor(data[colorIndex]);
            colorIndex += VERTEX_STRIDE;
        }

        return this;
    }

    @Override
    public final OpenMutableQuadLookup fromVanilla(BakedQuad quad, MaterialLookup material, @Nullable Direction cullFace) {
        this.fromVanilla(quad.getVertices(), 0);
        this.data[this.baseIndex + HEADER_BITS] = EncodingHelper.cullFace(0, cullFace);
        this.nominalFace(quad.getDirection());
        this.colorIndex(quad.getTintIndex());

        if (!quad.isShade()) {
            material = OpenMaterialRenderLookup.setDisableDiffuse((OpenMaterialRenderLookup) material, true);
        }

        this.material(material);
        this.tag(0);
        return this;
    }

    @Override
    public final OpenMutableQuadLookup emit() {
        this.emitDirectly();
        this.clear();
        return this;
    }

    public void clear() {
        System.arraycopy(EncodingHelper.EMPTY, 0, data, baseIndex, TOTAL_STRIDE);
        this.isGeometryInvalid = true;
        this.nominalFace = null;
        this.normalFlags(0);
        this.tag(0);
        this.colorIndex(-1);
        this.cullFace(null);
        this.material(IndigoRenderer.MATERIAL_STANDARD);
    }
}
