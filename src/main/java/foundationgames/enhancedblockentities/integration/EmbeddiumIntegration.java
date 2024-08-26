package foundationgames.enhancedblockentities.integration;

import net.minecraftforge.fml.ModList;

public class EmbeddiumIntegration {

    public static boolean hasEmbeddium() {
        return ModList.get().isLoaded("xenon") || ModList.get().isLoaded("embeddium");
    }


}
