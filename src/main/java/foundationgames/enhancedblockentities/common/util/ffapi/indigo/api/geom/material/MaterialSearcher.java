package foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.FBlendMode;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.TriState;

public interface MaterialSearcher extends MaterialLookup {

    MaterialSearcher blendMode(FBlendMode blendMode);

    MaterialSearcher disableColorIndex(boolean disable);

    MaterialSearcher emissive(boolean isEmissive);

    MaterialSearcher disableDiffuse(boolean disable);

    MaterialSearcher ambientOcclusion(TriState mode);

    MaterialSearcher glint(TriState mode);

    MaterialSearcher copyFrom(MaterialLookup material);

    MaterialSearcher clear();

    MaterialLookup find();

}
