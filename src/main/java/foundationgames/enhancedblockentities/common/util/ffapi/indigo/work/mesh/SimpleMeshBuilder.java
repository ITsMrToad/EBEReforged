package foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.mesh;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.mesh.MeshBuilder;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadEmitter;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.quad.OpenMutableQuadLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.quad.OpenQuadLookup;

public class SimpleMeshBuilder implements MeshBuilder {

    private final Maker maker = new Maker();

    private int[] data = new int[256];
    private int index = 0;
    private int limit = data.length;

    public SimpleMeshBuilder() {
        this.ensureCapacity(OpenQuadLookup.TOTAL_STRIDE);
        this.maker.data = this.data;
        this.maker.baseIndex = this.index;
        this.maker.clear();
    }

    @Override
    public QuadEmitter getEmitter() {
        this.maker.clear();
        return this.maker;
    }

    @Override
    public SimpleMesh build() {
        int[] packed = new int[this.index];
        System.arraycopy(data, 0, packed, 0, this.index);
        this.index = 0;
        this.maker.baseIndex = this.index;
        this.maker.clear();
        return new SimpleMesh(packed);
    }

    protected void ensureCapacity(int stride) {
        if (stride > this.limit - this.index) {
            this.limit *= 2;
            final int[] bigger = new int[limit];
            System.arraycopy(this.data, 0, bigger, 0, this.index);
            this.data = bigger;
            this.maker.data = this.data;
        }
    }

    private class Maker extends OpenMutableQuadLookup {
        @Override
        public void emitDirectly() {
            this.computeGeometry();
            SimpleMeshBuilder.this.index += OpenQuadLookup.TOTAL_STRIDE;
            SimpleMeshBuilder.this.ensureCapacity(OpenQuadLookup.TOTAL_STRIDE);
            this.baseIndex = SimpleMeshBuilder.this.index;
        }
    }

}
