package foundationgames.enhancedblockentities.core.mixin.ffapi;

import foundationgames.enhancedblockentities.common.util.ffapi.indigo.UltimateBakedModel;
import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BakedModel.class)
public interface BakedModelMixin extends UltimateBakedModel {}
