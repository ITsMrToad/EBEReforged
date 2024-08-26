package foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.material;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.FBlendMode;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.TriState;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material.MaterialLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material.MaterialSearcher;

import java.util.Objects;

public class OpenMaterialSearcher extends OpenMaterialLookup implements MaterialSearcher {

    private static final int DEFAULT_BITS;

    static {
        OpenMaterialSearcher finder = new OpenMaterialSearcher();
        finder.ambientOcclusion(TriState.DEFAULT);
        finder.glint(TriState.DEFAULT);
        DEFAULT_BITS = finder.bits;

        if (!areBitsValid(DEFAULT_BITS)) {
            throw new AssertionError("Default MaterialFinder bits are not valid!");
        }
    }

    public OpenMaterialSearcher() {
        super(DEFAULT_BITS);
    }

    @Override
    public MaterialSearcher blendMode(FBlendMode blendMode) {
        Objects.requireNonNull(blendMode, "BlendMode may not be null");
        this.bits = (this.bits & ~BLEND_MODE_MASK) | (blendMode.ordinal() << BLEND_MODE_BIT_OFFSET);
        return this;
    }

    @Override
    public MaterialSearcher disableColorIndex(boolean disable) {
        this.bits = disable ? (this.bits | COLOR_DISABLE_FLAG) : (this.bits & ~COLOR_DISABLE_FLAG);
        return this;
    }

    @Override
    public MaterialSearcher emissive(boolean isEmissive) {
        this.bits = isEmissive ? (this.bits | EMISSIVE_FLAG) : (this.bits & ~EMISSIVE_FLAG);
        return this;
    }

    @Override
    public MaterialSearcher disableDiffuse(boolean disable) {
        this.bits = disable ? (this.bits | DIFFUSE_FLAG) : (this.bits & ~DIFFUSE_FLAG);
        return this;
    }

    @Override
    public MaterialSearcher ambientOcclusion(TriState mode) {
        Objects.requireNonNull(mode, "ambient occlusion TriState may not be null");
        this.bits = (this.bits & ~AO_MASK) | (mode.ordinal() << AO_BIT_OFFSET);
        return this;
    }

    @Override
    public MaterialSearcher glint(TriState mode) {
        Objects.requireNonNull(mode, "glint TriState may not be null");
        this.bits = (this.bits & ~GLINT_MASK) | (mode.ordinal() << GLINT_BIT_OFFSET);
        return this;
    }

    @Override
    public MaterialSearcher copyFrom(MaterialLookup material) {
        this.bits = ((OpenMaterialLookup) material).bits;
        return this;
    }

    @Override
    public MaterialSearcher clear() {
        this.bits = DEFAULT_BITS;
        return this;
    }

    @Override
    public MaterialLookup find() {
        return OpenMaterialRenderLookup.byIndex(this.bits);
    }
}
