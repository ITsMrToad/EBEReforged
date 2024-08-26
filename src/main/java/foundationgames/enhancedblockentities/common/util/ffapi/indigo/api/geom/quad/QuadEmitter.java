package foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material.MaterialLookup;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector2f;
import org.joml.Vector3f;

import org.jetbrains.annotations.Nullable;

public interface QuadEmitter extends MutableQuadLookup {

    float CULL_FACE_EPSILON = 0.00001f;

    @Override
    QuadEmitter pos(int vertexIndex, float x, float y, float z);

    @Override
    QuadEmitter color(int vertexIndex, int color);

    @Override
    QuadEmitter uv(int vertexIndex, float u, float v);

    @Override
    QuadEmitter spriteBake(TextureAtlasSprite sprite, int bakeFlags);

    @Override
    QuadEmitter lightmap(int vertexIndex, int lightmap);

    @Override
    QuadEmitter normal(int vertexIndex, float x, float y, float z);

    @Override
    QuadEmitter cullFace(@Nullable Direction face);

    @Override
    QuadEmitter nominalFace(@Nullable Direction face);

    @Override
    QuadEmitter material(MaterialLookup material);

    @Override
    QuadEmitter colorIndex(int colorIndex);

    @Override
    QuadEmitter tag(int tag);

    @Override
    QuadEmitter fromVanilla(int[] quadData, int startIndex);

    @Override
    QuadEmitter fromVanilla(BakedQuad quad, MaterialLookup material, @Nullable Direction cullFace);

    @Override
    default QuadEmitter pos(int vertexIndex, Vector3f pos) {
        MutableQuadLookup.super.pos(vertexIndex, pos);
        return this;
    }

    @Override
    default QuadEmitter color(int c0, int c1, int c2, int c3) {
        MutableQuadLookup.super.color(c0, c1, c2, c3);
        return this;
    }

    @Override
    default QuadEmitter uv(int vertexIndex, Vector2f uv) {
        MutableQuadLookup.super.uv(vertexIndex, uv);
        return this;
    }

    @Override
    default QuadEmitter lightmap(int b0, int b1, int b2, int b3) {
        MutableQuadLookup.super.lightmap(b0, b1, b2, b3);
        return this;
    }

    @Override
    default QuadEmitter normal(int vertexIndex, Vector3f normal) {
        MutableQuadLookup.super.normal(vertexIndex, normal);
        return this;
    }

    QuadEmitter copyFrom(QuadLookup quad);

    QuadEmitter emit();

    default QuadEmitter uvUnitSquare() {
        this.uv(0, 0, 0);
        this.uv(1, 0, 1);
        this.uv(2, 1, 1);
        this.uv(3, 1, 0);
        return this;
    }

    default QuadEmitter square(Direction nominalFace, float left, float bottom, float right, float top, float depth) {
        if (Math.abs(depth) < CULL_FACE_EPSILON) {
            this.cullFace(nominalFace);
            depth = 0;
        } else {
            this.cullFace(null);
        }

        nominalFace(nominalFace);
        switch (nominalFace) {
            case UP:
                depth = 1 - depth;
                top = 1 - top;
                bottom = 1 - bottom;

            case DOWN:
                this.pos(0, left, depth, top);
                this.pos(1, left, depth, bottom);
                this.pos(2, right, depth, bottom);
                this.pos(3, right, depth, top);
                break;

            case EAST:
                depth = 1 - depth;
                left = 1 - left;
                right = 1 - right;

            case WEST:
                this.pos(0, depth, top, left);
                this.pos(1, depth, bottom, left);
                this.pos(2, depth, bottom, right);
                this.pos(3, depth, top, right);
                break;

            case SOUTH:
                depth = 1 - depth;
                left = 1 - left;
                right = 1 - right;

            case NORTH:
                this.pos(0, 1 - left, top, depth);
                this.pos(1, 1 - left, bottom, depth);
                this.pos(2, 1 - right, bottom, depth);
                this.pos(3, 1 - right, top, depth);
                break;
        }

        return this;
    }
}
