package foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadEmitter;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.quad.QuadTransform;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;

import org.jetbrains.annotations.Nullable;

public interface GeomRenderContext {

    QuadEmitter getEmitter();

    void pushTransform(QuadTransform transform);

    void popTransform();

    default boolean hasTransform() {
        return true;
    }

    default boolean isFaceCulled(@Nullable Direction face) {
        return false;
    }

    default ItemDisplayContext itemTransformationMode() {
        return ItemDisplayContext.NONE;
    }

}
