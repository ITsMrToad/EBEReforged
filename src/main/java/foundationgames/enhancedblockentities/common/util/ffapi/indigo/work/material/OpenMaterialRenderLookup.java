package foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.material;

public class OpenMaterialRenderLookup extends OpenMaterialLookup {

    public static final int VALUE_COUNT = 1 << TOTAL_BIT_LENGTH;
    private static final OpenMaterialRenderLookup[] BY_INDEX = new OpenMaterialRenderLookup[VALUE_COUNT];

    static {
        for (int i = 0; i < VALUE_COUNT; i++) {
            if (areBitsValid(i)) {
                BY_INDEX[i] = new OpenMaterialRenderLookup(i);
            }
        }
    }

    private OpenMaterialRenderLookup(int bits) {
        super(bits);
    }

    public int index() {
        return bits;
    }

    public static OpenMaterialRenderLookup byIndex(int index) {
        return BY_INDEX[index];
    }

    public static OpenMaterialRenderLookup setDisableDiffuse(OpenMaterialRenderLookup material, boolean disable) {
        if (material.disableDiffuse() != disable) {
            return byIndex(disable ? (material.bits | DIFFUSE_FLAG) : (material.bits & ~DIFFUSE_FLAG));
        }
        return material;
    }

}
