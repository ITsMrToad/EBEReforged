package foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material.MaterialLookup;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector2f;
import org.joml.Vector3f;

import org.jetbrains.annotations.Nullable;

public interface MutableQuadLookup extends QuadLookup {

    int BAKE_ROTATE_NONE = 0;
    int BAKE_ROTATE_90 = 1;
    int BAKE_ROTATE_180 = 2;
    int BAKE_ROTATE_270 = 3;
    int BAKE_LOCK_UV = 4;
    int BAKE_FLIP_U = 8;
    int BAKE_FLIP_V = 16;
    int BAKE_NORMALIZED = 32;

    @CanIgnoreReturnValue
    MutableQuadLookup pos(int vertexIndex, float x, float y, float z);

    @CanIgnoreReturnValue
    MutableQuadLookup color(int vertexIndex, int color);

    @CanIgnoreReturnValue
    MutableQuadLookup uv(int vertexIndex, float u, float v);

    @CanIgnoreReturnValue
    MutableQuadLookup spriteBake(TextureAtlasSprite sprite, int bakeFlags);

    @CanIgnoreReturnValue
    MutableQuadLookup lightmap(int vertexIndex, int lightmap);

    @CanIgnoreReturnValue
    MutableQuadLookup normal(int vertexIndex, float x, float y, float z);

    @CanIgnoreReturnValue
    MutableQuadLookup cullFace(@Nullable Direction face);

    @CanIgnoreReturnValue
    MutableQuadLookup nominalFace(@Nullable Direction face);

    @CanIgnoreReturnValue
    MutableQuadLookup material(MaterialLookup material);

    @CanIgnoreReturnValue
    MutableQuadLookup colorIndex(int colorIndex);

    @CanIgnoreReturnValue
    MutableQuadLookup tag(int tag);

    @CanIgnoreReturnValue
    MutableQuadLookup copyFrom(QuadLookup quad);

    @CanIgnoreReturnValue
    MutableQuadLookup fromVanilla(int[] quadData, int startIndex);

    @CanIgnoreReturnValue
    MutableQuadLookup fromVanilla(BakedQuad quad, MaterialLookup material, @Nullable Direction cullFace);

    default MutableQuadLookup pos(int vertexIndex, Vector3f pos) {
        return pos(vertexIndex, pos.x(), pos.y(), pos.z());
    }

    default MutableQuadLookup color(int c0, int c1, int c2, int c3) {
        this.color(0, c0);
        this.color(1, c1);
        this.color(2, c2);
        this.color(3, c3);
        return this;
    }

    default MutableQuadLookup uv(int vertexIndex, Vector2f uv) {
        return uv(vertexIndex, uv.x, uv.y);
    }

    default MutableQuadLookup lightmap(int b0, int b1, int b2, int b3) {
        this.lightmap(0, b0);
        this.lightmap(1, b1);
        this.lightmap(2, b2);
        this.lightmap(3, b3);
        return this;
    }

    default MutableQuadLookup normal(int vertexIndex, Vector3f normal) {
        return normal(vertexIndex, normal.x(), normal.y(), normal.z());
    }

}
