package foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.mesh;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadEmitter;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadLookup;

import java.util.function.Consumer;

public interface Mesh {

    void forEach(Consumer<QuadLookup> consumer);

    default void outputTo(QuadEmitter emitter) {
        this.forEach(quad -> {
            emitter.copyFrom(quad);
            emitter.emit();
        });
    };
}
