package foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.mesh;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadEmitter;

public interface MeshBuilder {

    QuadEmitter getEmitter();

    Mesh build();

}
