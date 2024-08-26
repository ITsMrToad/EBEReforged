package foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.FBlendMode;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.TriState;

public interface MaterialLookup {

    FBlendMode blendMode();

    boolean disableColorIndex();

    boolean emissive();

    boolean disableDiffuse();

    TriState ambientOcclusion();

    TriState glint();

}
