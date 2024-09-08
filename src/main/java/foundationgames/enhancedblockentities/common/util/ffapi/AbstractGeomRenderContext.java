package foundationgames.enhancedblockentities.common.util.ffapi.indigo.work;

import com.mojang.blaze3d.vertex.VertexConsumer;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.GeomRenderContext;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.MutableQuadLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadTransform;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.quad.OpenMutableQuadLookup;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public abstract class AbstractGeomRenderContext implements GeomRenderContext {

    private static final QuadTransform NO_TRANSFORM = q -> true;

    private final ObjectArrayList<QuadTransform> transformStack = new ObjectArrayList<>();
    private final QuadTransform stackTransform = q -> {
        int i = transformStack.size() - 1;
        while (i >= 0) {
            if (!transformStack.get(i--).transform(q)) {
                return false;
            }
        }
        return true;
    };

    private QuadTransform activeTransform = NO_TRANSFORM;

    protected Matrix4f matrix;
    protected Matrix3f normalMatrix;
    protected int overlay;
    private final Vector4f posVec = new Vector4f();
    private final Vector3f normalVec = new Vector3f();

    @Override
    public boolean hasTransform() {
        return activeTransform != NO_TRANSFORM;
    }

    @Override
    public void pushTransform(QuadTransform transform) {
        if (transform == null) {
            throw new NullPointerException("Renderer received null QuadTransform.");
        }

        this.transformStack.push(transform);

        if (this.transformStack.size() == 1) {
            this.activeTransform = transform;
        } else if (this.transformStack.size() == 2) {
            this.activeTransform = this.stackTransform;
        }
    }

    @Override
    public void popTransform() {
        this.transformStack.pop();
        if (this.transformStack.isEmpty()) {
            this.activeTransform = NO_TRANSFORM;
        } else if (transformStack.size() == 1) {
            this.activeTransform = this.transformStack.get(0);
        }
    }

    protected final boolean transform(MutableQuadLookup q) {
        return this.activeTransform.transform(q);
    }

    protected void bufferQuad(OpenMutableQuadLookup quad, VertexConsumer vertexConsumer) {
        Vector4f posVec = this.posVec;
        Vector3f normalVec = this.normalVec;

        boolean useNormals = quad.hasVertexNormals();

        if (useNormals) {
            quad.populateMissingNormals();
        } else {
            normalVec.set(quad.faceNormal());
            normalVec.mul(this.normalMatrix);
        }

        for (int i = 0; i < 4; i++) {
            posVec.set(quad.x(i), quad.y(i), quad.z(i), 1.0f);
            posVec.mul(this.matrix);
            vertexConsumer.vertex(posVec.x(), posVec.y(), posVec.z());

            final int color = quad.color(i);
            vertexConsumer.color((color >>> 16) & 0xFF, (color >>> 8) & 0xFF, color & 0xFF, (color >>> 24) & 0xFF);
            vertexConsumer.uv(quad.u(i), quad.v(i));
            vertexConsumer.overlayCoords(this.overlay);
            vertexConsumer.uv2(quad.lightmap(i));

            if (useNormals) {
                quad.copyNormal(i, normalVec);
                normalVec.mul(this.normalMatrix);
            }

            vertexConsumer.normal(normalVec.x(), normalVec.y(), normalVec.z());
            vertexConsumer.endVertex();
        }
    }

}
