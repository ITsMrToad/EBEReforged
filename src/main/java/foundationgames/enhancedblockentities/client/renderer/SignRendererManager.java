package foundationgames.enhancedblockentities.client.renderer;

public class SignRendererManager {

    private static int LAST_RENDERED = 0;
    public static int RENDERED = 0;

    public static int getRenderedSignAmount() {
        return LAST_RENDERED;
    }

    public static void endFrame() {
        LAST_RENDERED = RENDERED;
        RENDERED = 0;
    }
}
