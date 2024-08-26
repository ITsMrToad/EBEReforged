package foundationgames.enhancedblockentities.core.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class EBEConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().excludeFieldsWithModifiers(Modifier.PRIVATE).create();

    private static final String RENDER_ENHANCED_CHESTS_KEY = "render_enhanced_chests";
    private static final String RENDER_ENHANCED_SIGNS_KEY = "render_enhanced_signs";
    private static final String RENDER_ENHANCED_BELLS_KEY = "render_enhanced_bells";
    private static final String RENDER_ENHANCED_BEDS_KEY = "render_enhanced_beds";
    private static final String RENDER_ENHANCED_SHULKER_BOXES_KEY = "render_enhanced_shulker_boxes";
    private static final String CHEST_AO_KEY = "chest_ao";
    private static final String SIGN_AO_KEY = "sign_ao";
    private static final String BELL_AO_KEY = "bell_ao";
    private static final String BED_AO_KEY = "bed_ao";
    private static final String SHULKER_BOX_AO_KEY = "shulker_box_ao";
    private static final String CHRISTMAS_CHESTS_KEY = "christmas_chests";
    private static final String SIGN_TEXT_RENDERING_KEY = "sign_text_rendering";
    private static final String EXPERIMENTAL_CHESTS_KEY = "experimental_chests";
    private static final String EXPERIMENTAL_BEDS_KEY = "experimental_beds";
    private static final String EXPERIMENTAL_SIGNS_KEY = "experimental_signs";

    public boolean renderEnhancedChests = true;
    public boolean renderEnhancedSigns = true;
    public boolean renderEnhancedBells = true;
    public boolean renderEnhancedBeds = true;
    public boolean renderEnhancedShulkerBoxes = true;
    public boolean renderEnhancedDecoratedPots = true;

    public boolean chestAO = false;
    public boolean signAO = false;
    public boolean bellAO = true;
    public boolean bedAO = false;
    public boolean shulkerBoxAO = false;
    public boolean decoratedPotAO = false;

    public boolean experimentalChests = true;
    public boolean experimentalBeds = true;
    public boolean experimentalSigns = true;

    public boolean forceResourcePackCompat = false;

    public EBEConfigEnumValue christmasChests = EBEConfigEnumValue.ALLOWED;
    public EBEConfigEnumValue signTextRendering = EBEConfigEnumValue.SMART;

    private EBEConfig() {}

    public static EBEConfig loadOrCreate() {
        if (Files.exists(getPath())) {
            try (BufferedReader bufferedreader = Files.newBufferedReader(getPath(), StandardCharsets.UTF_8)) {
                return GSON.fromJson(bufferedreader, EBEConfig.class);
            } catch (IOException e) {
                EBE.LOGGER.error("Could not load config", e);
            }
        }
        return new EBEConfig();
    }

    public void save() {
        try (final PrintWriter printwriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getPath().toFile()), StandardCharsets.UTF_8))) {
            printwriter.println(GSON.toJson(this));
        } catch (FileNotFoundException e) {
            EBE.LOGGER.error("Config file not found", e);
        }
    }

    private static Path getPath() {
        return Minecraft.getInstance().gameDirectory.toPath().resolve("config/enhanced_bes.properties");
    }
}
