package foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material.MaterialLookup;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector2f;
import org.joml.Vector3f;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface QuadLookup {

    int VANILLA_VERTEX_STRIDE = DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP.getVertexSize();
    int VANILLA_QUAD_STRIDE = VANILLA_VERTEX_STRIDE * 4;

    float x(int vertexIndex);

    float y(int vertexIndex);

    float z(int vertexIndex);

    float posByIndex(int vertexIndex, int coordinateIndex);

    Vector3f copyPos(int vertexIndex, @Nullable Vector3f target);

    int color(int vertexIndex);

    float u(int vertexIndex);

    float v(int vertexIndex);

    Vector2f copyUv(int vertexIndex, @Nullable Vector2f target);

    int lightmap(int vertexIndex);

    boolean hasNormal(int vertexIndex);

    float normalX(int vertexIndex);

    float normalY(int vertexIndex);

    float normalZ(int vertexIndex);

    @Nullable
    Vector3f copyNormal(int vertexIndex, @Nullable Vector3f target);

    @Nullable Direction cullFace();

    @NotNull Direction lightFace();

    @Nullable Direction nominalFace();

    Vector3f faceNormal();

    MaterialLookup material();

    int colorIndex();

    int tag();

    void toVanilla(int[] target, int targetIndex);

    default BakedQuad toBakedQuad(TextureAtlasSprite sprite) {
        int[] vertexData = new int[VANILLA_QUAD_STRIDE];
        toVanilla(vertexData, 0);
        int outputColorIndex = material().disableColorIndex() ? -1 : colorIndex();
        boolean outputShade = !material().disableDiffuse();
        return new BakedQuad(vertexData, outputColorIndex, lightFace(), sprite, outputShade);
    }

}
