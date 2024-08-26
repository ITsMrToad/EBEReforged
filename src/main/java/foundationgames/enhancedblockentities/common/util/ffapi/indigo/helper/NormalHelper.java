package foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadLookup;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import org.jetbrains.annotations.NotNull;

public class NormalHelper {

    private static final float PACK = 127.0f;
    private static final float UNPACK = 1.0f / PACK;

    public static int packNormal(float x, float y, float z, float w) {
        x = Mth.clamp(x, -1, 1);
        y = Mth.clamp(y, -1, 1);
        z = Mth.clamp(z, -1, 1);
        w = Mth.clamp(w, -1, 1);

        return ((int) (x * PACK) & 0xFF) | (((int) (y * PACK) & 0xFF) << 8) | (((int) (z * PACK) & 0xFF) << 16) | (((int) (w * PACK) & 0xFF) << 24);
    }

    public static int packNormal(Vector3f normal, float w) {
        return packNormal(normal.x(), normal.y(), normal.z(), w);
    }

    public static int packNormal(float x, float y, float z) {
        x = Mth.clamp(x, -1, 1);
        y = Mth.clamp(y, -1, 1);
        z = Mth.clamp(z, -1, 1);

        return ((int) (x * PACK) & 0xFF) | (((int) (y * PACK) & 0xFF) << 8) | (((int) (z * PACK) & 0xFF) << 16);
    }

    public static int packNormal(Vector3f normal) {
        return packNormal(normal.x(), normal.y(), normal.z());
    }

    public static float unpackNormalX(int packedNormal) {
        return ((byte) (packedNormal & 0xFF)) * UNPACK;
    }

    public static float unpackNormalY(int packedNormal) {
        return ((byte) ((packedNormal >>> 8) & 0xFF)) * UNPACK;
    }

    public static float unpackNormalZ(int packedNormal) {
        return ((byte) ((packedNormal >>> 16) & 0xFF)) * UNPACK;
    }

    public static float unpackNormalW(int packedNormal) {
        return ((byte) ((packedNormal >>> 24) & 0xFF)) * UNPACK;
    }

    public static void unpackNormal(int packedNormal, Vector3f target) {
        target.set(unpackNormalX(packedNormal), unpackNormalY(packedNormal), unpackNormalZ(packedNormal));
    }


    public static void computeFaceNormal(@NotNull Vector3f saveTo, QuadLookup q) {
        Direction nominalFace = q.nominalFace();

        if (nominalFace != null && GeometryHelper.isQuadParallelToFace(nominalFace, q)) {
            Vec3i vec = nominalFace.getNormal();
            saveTo.set(vec.getX(), vec.getY(), vec.getZ());
            return;
        }

        final float x0 = q.x(0);
        final float y0 = q.y(0);
        final float z0 = q.z(0);
        final float x1 = q.x(1);
        final float y1 = q.y(1);
        final float z1 = q.z(1);
        final float x2 = q.x(2);
        final float y2 = q.y(2);
        final float z2 = q.z(2);
        final float x3 = q.x(3);
        final float y3 = q.y(3);
        final float z3 = q.z(3);

        final float dx0 = x2 - x0;
        final float dy0 = y2 - y0;
        final float dz0 = z2 - z0;
        final float dx1 = x3 - x1;
        final float dy1 = y3 - y1;
        final float dz1 = z3 - z1;

        float normX = dy0 * dz1 - dz0 * dy1;
        float normY = dz0 * dx1 - dx0 * dz1;
        float normZ = dx0 * dy1 - dy0 * dx1;

        float l = (float) Math.sqrt(normX * normX + normY * normY + normZ * normZ);

        if (l != 0) {
            normX /= l;
            normY /= l;
            normZ /= l;
        }

        saveTo.set(normX, normY, normZ);
    }
}
