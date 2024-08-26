package foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.MutableQuadLookup;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

public class TextureHelper {

    private static final VertexModifier[] ROTATIONS = new VertexModifier[] {null, (q, i) -> q.uv(i, q.v(i), 1 - q.u(i)), (q, i) -> q.uv(i, 1 - q.u(i), 1 - q.v(i)), (q, i) -> q.uv(i, 1 - q.v(i), q.u(i))};
    private static final VertexModifier[] UVLOCKERS = new VertexModifier[6];
    private static final float NORMALIZER = 1f / 16f;

    static {
        UVLOCKERS[Direction.EAST.get3DDataValue()] = (q, i) -> q.uv(i, 1 - q.z(i), 1 - q.y(i));
        UVLOCKERS[Direction.WEST.get3DDataValue()] = (q, i) -> q.uv(i, q.z(i), 1 - q.y(i));
        UVLOCKERS[Direction.NORTH.get3DDataValue()] = (q, i) -> q.uv(i, 1 - q.x(i), 1 - q.y(i));
        UVLOCKERS[Direction.SOUTH.get3DDataValue()] = (q, i) -> q.uv(i, q.x(i), 1 - q.y(i));
        UVLOCKERS[Direction.DOWN.get3DDataValue()] = (q, i) -> q.uv(i, q.x(i), 1 - q.z(i));
        UVLOCKERS[Direction.UP.get3DDataValue()] = (q, i) -> q.uv(i, q.x(i), q.z(i));
    }

    public static void bakeSprite(MutableQuadLookup quad, TextureAtlasSprite sprite, int bakeFlags) {
        Direction dq = quad.nominalFace();
        if (dq != null && (MutableQuadLookup.BAKE_LOCK_UV & bakeFlags) != 0) {
            applyModifier(quad, UVLOCKERS[dq.get3DDataValue()]);
        } else if ((MutableQuadLookup.BAKE_NORMALIZED & bakeFlags) == 0) {
            applyModifier(quad, (q, i) -> q.uv(i, q.u(i) * NORMALIZER, q.v(i) * NORMALIZER));
        }

        int rotation = bakeFlags & 3;

        if (rotation != 0) {
            applyModifier(quad, ROTATIONS[rotation]);
        }

        if ((MutableQuadLookup.BAKE_FLIP_U & bakeFlags) != 0) {
            applyModifier(quad, (q, i) -> q.uv(i, 1 - q.u(i), q.v(i)));
        }

        if ((MutableQuadLookup.BAKE_FLIP_V & bakeFlags) != 0) {
            applyModifier(quad, (q, i) -> q.uv(i, q.u(i), 1 - q.v(i)));
        }

        interpolate(quad, sprite);
    }

    private static void interpolate(MutableQuadLookup q, TextureAtlasSprite sprite) {
        final float uMin = sprite.getU0();
        final float uSpan = sprite.getU1() - uMin;
        final float vMin = sprite.getV0();
        final float vSpan = sprite.getV1() - vMin;

        for (int i = 0; i < 4; i++) {
            q.uv(i, uMin + q.u(i) * uSpan, vMin + q.v(i) * vSpan);
        }
    }

    private static void applyModifier(MutableQuadLookup quad, VertexModifier modifier) {
        for (int i = 0; i < 4; i++) {
            modifier.apply(quad, i);
        }
    }

    @FunctionalInterface
    private interface VertexModifier {
        void apply(MutableQuadLookup quad, int vertexIndex);
    }
}
