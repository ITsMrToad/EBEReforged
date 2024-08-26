package foundationgames.enhancedblockentities.common.util.ffapi.indigo.helper;

import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.material.OpenMaterialRenderLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.quad.OpenQuadLookup;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

import org.jetbrains.annotations.Nullable;

public interface EncodingHelper {

    int[] EMPTY = new int[OpenQuadLookup.TOTAL_STRIDE];

    int DIRECTION_MASK = Mth.smallestEncompassingPowerOfTwo(6) - 1;
    int DIRECTION_BIT_COUNT = Integer.bitCount(DIRECTION_MASK);
    int CULL_SHIFT = 0;
    int CULL_INVERSE_MASK = ~(DIRECTION_MASK << CULL_SHIFT);
    int LIGHT_SHIFT = CULL_SHIFT + DIRECTION_BIT_COUNT;
    int LIGHT_INVERSE_MASK = ~(DIRECTION_MASK << LIGHT_SHIFT);
    int NORMALS_SHIFT = LIGHT_SHIFT + DIRECTION_BIT_COUNT;
    int NORMALS_COUNT = 4;
    int NORMALS_MASK = (1 << NORMALS_COUNT) - 1;
    int NORMALS_INVERSE_MASK = ~(NORMALS_MASK << NORMALS_SHIFT);
    int GEOMETRY_SHIFT = NORMALS_SHIFT + NORMALS_COUNT;
    int GEOMETRY_MASK = (1 << GeometryHelper.FLAG_BIT_COUNT) - 1;
    int GEOMETRY_INVERSE_MASK = ~(GEOMETRY_MASK << GEOMETRY_SHIFT);
    int MATERIAL_SHIFT = GEOMETRY_SHIFT + GeometryHelper.FLAG_BIT_COUNT;
    int MATERIAL_MASK = Mth.smallestEncompassingPowerOfTwo(OpenMaterialRenderLookup.VALUE_COUNT) - 1;
    int MATERIAL_BIT_COUNT = Integer.bitCount(MATERIAL_MASK);
    int MATERIAL_INVERSE_MASK = ~(MATERIAL_MASK << MATERIAL_SHIFT);

    static Direction cullFace(int bits) {
        return EBEOtherUtils.FACES[(bits >>> CULL_SHIFT) & DIRECTION_MASK];
    }

    static int cullFace(int bits, @Nullable Direction face) {
        int i = face == null ? 6 : face.get3DDataValue();
        return (bits & CULL_INVERSE_MASK) | (i << CULL_SHIFT);
    }

    static Direction lightFace(int bits) {
        return EBEOtherUtils.FACES[((bits >>> LIGHT_SHIFT) & DIRECTION_MASK)];
    }

    static int lightFace(int bits, Direction face) {
        return (bits & LIGHT_INVERSE_MASK) | (face.get3DDataValue()) << LIGHT_SHIFT;
    }

    static int normalFlags(int bits) {
        return (bits >>> NORMALS_SHIFT) & NORMALS_MASK;
    }

    static int normalFlags(int bits, int normalFlags) {
        return (bits & NORMALS_INVERSE_MASK) | ((normalFlags & NORMALS_MASK) << NORMALS_SHIFT);
    }

    static int geometryFlags(int bits) {
        return (bits >>> GEOMETRY_SHIFT) & GEOMETRY_MASK;
    }

    static int geometryFlags(int bits, int geometryFlags) {
        return (bits & GEOMETRY_INVERSE_MASK) | ((geometryFlags & GEOMETRY_MASK) << GEOMETRY_SHIFT);
    }

    static OpenMaterialRenderLookup material(int bits) {
        return OpenMaterialRenderLookup.byIndex((bits >>> MATERIAL_SHIFT) & MATERIAL_MASK);
    }

    static int material(int bits, OpenMaterialRenderLookup material) {
        return (bits & MATERIAL_INVERSE_MASK) | (material.index() << MATERIAL_SHIFT);
    }
}
