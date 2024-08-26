package foundationgames.enhancedblockentities.common.util.ffapi.indigo.api;

import net.minecraft.client.renderer.RenderType;

public enum FBlendMode {

    DEFAULT(null),
    SOLID(RenderType.solid()),
    CUTOUT_MIPPED(RenderType.cutoutMipped()),
    CUTOUT(RenderType.cutout()),
    TRANSLUCENT(RenderType.translucent());

    public final RenderType blockRenderLayer;

    FBlendMode(RenderType blockRenderLayer) {
        this.blockRenderLayer = blockRenderLayer;
    }

    public static FBlendMode fromRenderLayer(RenderType renderLayer) {
        if (renderLayer == RenderType.solid()) {
            return SOLID;
        } else if (renderLayer == RenderType.cutoutMipped()) {
            return CUTOUT_MIPPED;
        } else if (renderLayer == RenderType.cutout()) {
            return CUTOUT;
        } else if (renderLayer == RenderType.translucent()) {
            return TRANSLUCENT;
        } else {
            return DEFAULT;
        }
    }
}
