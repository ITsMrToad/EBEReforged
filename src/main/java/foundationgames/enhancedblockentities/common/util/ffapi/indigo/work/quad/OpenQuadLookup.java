package foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.quad;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper.ColorHelper;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper.EncodingHelper;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper.GeometryHelper;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper.NormalHelper;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.material.OpenMaterialRenderLookup;
import net.minecraft.core.Direction;
import org.joml.Vector2f;
import org.joml.Vector3f;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OpenQuadLookup implements QuadLookup {

    public static final VertexFormat FORMAT = DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP;

    public static final int HEADER_BITS = 0;
    public static final int HEADER_FACE_NORMAL = 1;
    public static final int HEADER_COLOR_INDEX = 2;
    public static final int HEADER_TAG = 3;
    public static final int HEADER_STRIDE = 4;

    public static final int VERTEX_X = HEADER_STRIDE;
    public static final int VERTEX_Y = HEADER_STRIDE + 1;
    public static final int VERTEX_Z = HEADER_STRIDE + 2;
    public static final int VERTEX_COLOR = HEADER_STRIDE + 3;
    public static final int VERTEX_U = HEADER_STRIDE + 4;
    public static final int VERTEX_V = VERTEX_U + 1;
    public static final int VERTEX_LIGHTMAP = HEADER_STRIDE + 6;
    public static final int VERTEX_NORMAL = HEADER_STRIDE + 7;
    public static final int VERTEX_STRIDE = FORMAT.getVertexSize();
    public static final int QUAD_STRIDE = VERTEX_STRIDE * 4;
    public static final int QUAD_STRIDE_BYTES = QUAD_STRIDE * 4;
    public static final int TOTAL_STRIDE = HEADER_STRIDE + QUAD_STRIDE;

    protected final Vector3f faceNormal = new Vector3f();

    @Nullable protected Direction nominalFace;
    protected boolean isGeometryInvalid = true;
    public int[] data;
    public int baseIndex = 0;

    @Override
    public float x(int vertexIndex) {
        return Float.intBitsToFloat(this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X]);
    }

    @Override
    public float y(int vertexIndex) {
        return Float.intBitsToFloat(this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_Y]);
    }

    @Override
    public float z(int vertexIndex) {
        return Float.intBitsToFloat(this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_Z]);
    }

    @Override
    public float posByIndex(int vertexIndex, int coordinateIndex) {
        return Float.intBitsToFloat(this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X + coordinateIndex]);
    }

    @Override
    public Vector3f copyPos(int vertexIndex, @Nullable Vector3f target) {
        if (target == null) {
            target = new Vector3f();
        }

        int index = this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_X;
        target.set(Float.intBitsToFloat(this.data[index]), Float.intBitsToFloat(this.data[index + 1]), Float.intBitsToFloat(data[index + 2]));
        return target;
    }

    @Override
    public int color(int vertexIndex) {
        return this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_COLOR];
    }

    @Override
    public float u(int vertexIndex) {
        return Float.intBitsToFloat(this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_U]);
    }

    @Override
    public float v(int vertexIndex) {
        return Float.intBitsToFloat(this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_V]);
    }

    @Override
    public Vector2f copyUv(int vertexIndex, @Nullable Vector2f target) {
        if (target == null) {
            target = new Vector2f();
        }

        final int index = this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_U;
        target.set(Float.intBitsToFloat(this.data[index]), Float.intBitsToFloat(this.data[index + 1]));
        return target;
    }

    @Override
    public int lightmap(int vertexIndex) {
        return this.data[this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_LIGHTMAP];
    }

    @Override
    public boolean hasNormal(int vertexIndex) {
        return (normalFlags() & (1 << vertexIndex)) != 0;
    }

    @Override
    public float normalX(int vertexIndex) {
        return hasNormal(vertexIndex) ? NormalHelper.unpackNormalX(this.data[normalIndex(vertexIndex)]) : Float.NaN;
    }

    @Override
    public float normalY(int vertexIndex) {
        return hasNormal(vertexIndex) ? NormalHelper.unpackNormalY(this.data[normalIndex(vertexIndex)]) : Float.NaN;
    }

    @Override
    public float normalZ(int vertexIndex) {
        return hasNormal(vertexIndex) ? NormalHelper.unpackNormalZ(this.data[normalIndex(vertexIndex)]) : Float.NaN;
    }

    @Override
    @Nullable
    public Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target) {
        if (hasNormal(vertexIndex)) {
            if (target == null) {
                target = new Vector3f();
            }

            int normal = this.data[normalIndex(vertexIndex)];
            NormalHelper.unpackNormal(normal, target);
            return target;
        } else {
            return null;
        }
    }

    @Override
    @Nullable
    public final Direction cullFace() {
        return EncodingHelper.cullFace(this.data[this.baseIndex + HEADER_BITS]);
    }

    @Override
    @NotNull
    public final Direction lightFace() {
        this.computeGeometry();
        return EncodingHelper.lightFace(this.data[this.baseIndex + HEADER_BITS]);
    }

    @Override
    @Nullable
    public final Direction nominalFace() {
        return nominalFace;
    }

    @Override
    public final Vector3f faceNormal() {
        this.computeGeometry();
        return this.faceNormal;
    }

    @Override
    public final OpenMaterialRenderLookup material() {
        return EncodingHelper.material(this.data[this.baseIndex + HEADER_BITS]);
    }

    @Override
    public final int colorIndex() {
        return this.data[this.baseIndex + HEADER_COLOR_INDEX];
    }

    @Override
    public final int tag() {
        return this.data[this.baseIndex + HEADER_TAG];
    }

    @Override
    public final void toVanilla(int[] target, int targetIndex) {
        System.arraycopy(this.data, this.baseIndex + HEADER_STRIDE, target, targetIndex, QUAD_STRIDE);
        int colorIndex = targetIndex + 3;

        for (int i = 0; i < 4; i++) {
            target[colorIndex] = ColorHelper.toVanillaColor(target[colorIndex]);
            colorIndex += VANILLA_VERTEX_STRIDE;
        }
    }

    public int normalFlags() {
        return EncodingHelper.normalFlags(this.data[this.baseIndex + HEADER_BITS]);
    }

    public void load() {
        this.isGeometryInvalid = false;
        this.nominalFace = this.lightFace();
        NormalHelper.unpackNormal(this.packedFaceNormal(), this.faceNormal);
    }

    protected void computeGeometry() {
        if (this.isGeometryInvalid) {
            this.isGeometryInvalid = false;

            NormalHelper.computeFaceNormal(this.faceNormal, this);

            this.data[this.baseIndex + HEADER_FACE_NORMAL] = NormalHelper.packNormal(this.faceNormal);
            this.data[this.baseIndex + HEADER_BITS] = EncodingHelper.lightFace(this.data[this.baseIndex + HEADER_BITS], GeometryHelper.lightFace(this));
            this.data[this.baseIndex + HEADER_BITS] = EncodingHelper.geometryFlags(this.data[this.baseIndex + HEADER_BITS], GeometryHelper.computeShapeFlags(this));
        }
    }

    public int geometryFlags() {
        this.computeGeometry();
        return EncodingHelper.geometryFlags(this.data[this.baseIndex + HEADER_BITS]);
    }

    public boolean hasShade() {
        return !material().disableDiffuse();
    }

    public boolean hasVertexNormals() {
        return normalFlags() != 0;
    }

    public boolean hasAllVertexNormals() {
        return (normalFlags() & 0b1111) == 0b1111;
    }

    protected final int normalIndex(int vertexIndex) {
        return this.baseIndex + vertexIndex * VERTEX_STRIDE + VERTEX_NORMAL;
    }

    public final int packedFaceNormal() {
        this.computeGeometry();
        return this.data[this.baseIndex + HEADER_FACE_NORMAL];
    }
}
