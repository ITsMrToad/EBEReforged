package foundationgames.enhancedblockentities.integration;

import com.mr_toad.lib.api.integration.Integration;
import foundationgames.enhancedblockentities.client.model.dynamic.DynamicModelHandler;
import foundationgames.enhancedblockentities.client.model.dynamic.DynamicUnbakedModel;
import foundationgames.enhancedblockentities.common.util.mfrapi.loader.plugin.MFModelLoadingPluginManager;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class SinytraIntegration {

    public static final Integration SINYTRA = () -> "connectormod";

    public static void registerPlugin(ResourceLocation id, Supplier<DynamicUnbakedModel> model) {
        MFModelLoadingPluginManager.registerPlugin(new DynamicModelHandler(id, model));
    }
}
