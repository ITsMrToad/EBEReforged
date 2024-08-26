package foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.mesh;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.mesh.Mesh;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadEmitter;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.quad.OpenMutableQuadLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.quad.OpenQuadLookup;

import java.util.function.Consumer;

public class SimpleMesh implements Mesh {

    private final ThreadLocal<OpenQuadLookup> cursorPool = ThreadLocal.withInitial(OpenQuadLookup::new);

    public final int[] data;

    public SimpleMesh(int[] data) {
        this.data = data;
    }

    @Override
    public void forEach(Consumer<QuadLookup> consumer) {
        forEach(consumer, this.cursorPool.get());
    }

    public void forEach(Consumer<QuadLookup> consumer, OpenQuadLookup cursor) {
        final int limit = data.length;
        int index = 0;
        cursor.data = this.data;

        while (index < limit) {
            cursor.baseIndex = index;
            cursor.load();
            consumer.accept(cursor);
            index += OpenQuadLookup.TOTAL_STRIDE;
        }
    }

    @Override
    public void outputTo(QuadEmitter emitter) {
        OpenMutableQuadLookup e = (OpenMutableQuadLookup) emitter;
        final int[] data = this.data;
        final int limit = data.length;
        int index = 0;

        while (index < limit) {
            System.arraycopy(data, index, e.data, e.baseIndex, OpenQuadLookup.TOTAL_STRIDE);
            e.load();
            e.emitDirectly();
            index += OpenQuadLookup.TOTAL_STRIDE;
        }
        e.clear();
    }

}
