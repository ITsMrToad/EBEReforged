package foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadLookup;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

public class GeometryHelper {

    public static final int CUBIC_FLAG = 1;
    public static final int AXIS_ALIGNED_FLAG = CUBIC_FLAG << 1;
    public static final int LIGHT_FACE_FLAG = AXIS_ALIGNED_FLAG << 1;
    public static final int FLAG_BIT_COUNT = 3;

    private static final float EPS_MIN = 0.0001f;
    private static final float EPS_MAX = 1.0f - EPS_MIN;

    public static int computeShapeFlags(QuadLookup quad) {
        Direction lightFace = quad.lightFace();
        int bits = 0;

        if (isQuadParallelToFace(lightFace, quad)) {
            bits |= AXIS_ALIGNED_FLAG;

            if (isParallelQuadOnFace(lightFace, quad)) {
                bits |= LIGHT_FACE_FLAG;
            }
        }

        if (isQuadCubic(lightFace, quad)) {
            bits |= CUBIC_FLAG;
        }

        return bits;
    }

    public static boolean isQuadParallelToFace(Direction face, QuadLookup quad) {
        int i = face.getAxis().ordinal();
        float val = quad.posByIndex(0, i);
        return Mth.equal(val, quad.posByIndex(1, i)) && Mth.equal(val, quad.posByIndex(2, i)) && Mth.equal(val, quad.posByIndex(3, i));
    }

    public static boolean isParallelQuadOnFace(Direction lightFace, QuadLookup quad) {
        final float x = quad.posByIndex(0, lightFace.getAxis().ordinal());
        return lightFace.getAxisDirection() == Direction.AxisDirection.POSITIVE ? x >= EPS_MAX : x <= EPS_MIN;
    }

    public static boolean isQuadCubic(Direction lightFace, QuadLookup quad) {
        int a, b;

        switch (lightFace) {
            case EAST:
            case WEST:
                a = 1;
                b = 2;
                break;
            case UP:
            case DOWN:
                a = 0;
                b = 2;
                break;
            case SOUTH:
            case NORTH:
                a = 1;
                b = 0;
                break;
            default:
                return false;
        }

        return confirmSquareCorners(a, b, quad);
    }

    private static boolean confirmSquareCorners(int aCoordinate, int bCoordinate, QuadLookup quad) {
        int flags = 0;

        for (int i = 0; i < 4; i++) {
            final float a = quad.posByIndex(i, aCoordinate);
            final float b = quad.posByIndex(i, bCoordinate);

            if (a <= EPS_MIN) {
                if (b <= EPS_MIN) {
                    flags |= 1;
                } else if (b >= EPS_MAX) {
                    flags |= 2;
                } else {
                    return false;
                }
            } else if (a >= EPS_MAX) {
                if (b <= EPS_MIN) {
                    flags |= 4;
                } else if (b >= EPS_MAX) {
                    flags |= 8;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        return flags == 15;
    }

    public static Direction lightFace(QuadLookup quad) {
        final Vector3f normal = quad.faceNormal();
        return switch (GeometryHelper.longestAxis(normal)) {
            case X -> normal.x() > 0 ? Direction.EAST : Direction.WEST;
            case Y -> normal.y() > 0 ? Direction.UP : Direction.DOWN;
            case Z -> normal.z() > 0 ? Direction.SOUTH : Direction.NORTH;
        };
    }

    public static float min(float a, float b, float c, float d) {
        float x = Math.min(a, b);
        float y = Math.min(c, d);
        return Math.min(x, y);
    }

    public static float max(float a, float b, float c, float d) {
        float x = Math.max(a, b);
        float y = Math.max(c, d);
        return Math.max(x, y);
    }

    public static Direction.Axis longestAxis(Vector3f vec) {
        return longestAxis(vec.x(), vec.y(), vec.z());
    }

    public static Direction.Axis longestAxis(float normalX, float normalY, float normalZ) {
        Direction.Axis result = Direction.Axis.Y;
        float longest = Math.abs(normalY);
        float a = Math.abs(normalX);

        if (a > longest) {
            result = Direction.Axis.X;
            longest = a;
        }

        return Math.abs(normalZ) > longest ? Direction.Axis.Z : result;
    }

}
