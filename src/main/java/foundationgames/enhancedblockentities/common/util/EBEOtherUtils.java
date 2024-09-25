package foundationgames.enhancedblockentities.common.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;

public class EBEOtherUtils {

    public static final Map<SectionPos, ExecutableHashSet> CHUNK_UPDATE_TASKS = Maps.newHashMap();

    public static Set<SectionPos> FORCE_SYNCHRONOUS_CHUNK_REBUILD = Sets.newHashSet();

    public static final Direction[] FACES = Arrays.copyOf(Direction.values(), 7);
    private static final RandomSource DUMMY = RandomSource.create();

    public static final DyeColor[] DEFAULTED_DYE_COLORS;
    public static final Direction[] HORIZONTAL_DIRECTIONS;
    public static final String DUMP_FOLDER_NAME = "enhanced_bes_dump";

    static {
        DyeColor[] dColors = DyeColor.values();
        DEFAULTED_DYE_COLORS = new DyeColor[dColors.length + 1];
        System.arraycopy(dColors, 0, DEFAULTED_DYE_COLORS, 0, dColors.length);
        HORIZONTAL_DIRECTIONS = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    }

    @SuppressWarnings("deprecation")
    public static void renderBakedModel(MultiBufferSource vertexConsumers, BlockState state, PoseStack matrices, BakedModel model, int light, int overlay) {
        VertexConsumer vertices = vertexConsumers.getBuffer(ItemBlockRenderTypes.getRenderType(state, false));
        for (int i = 0; i <= 6; i++) {
            for(BakedQuad q : model.getQuads(null, FACES[i], DUMMY)) {
                vertices.putBulkData(matrices.last(), q, 1, 1, 1, 1, light, overlay);
            }
        }
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(EBE.MODID, path);
    }

    public static void dumpResources() throws IOException {
        Path path = Minecraft.getInstance().gameDirectory.toPath().resolve(DUMP_FOLDER_NAME);
        if (!Files.exists(path)) {
            Files.createDirectory(path);
        }
        ResourceUtil.dumpAllPacks(path);
    }

    public static Component shorten(String text, int maxLength, ChatFormatting... formats) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            line.append(word).append(" ");
            if (line.length() > maxLength) {
                if (i < words.length - 1) {
                    line.append("\n");
                }
                result.append(line);
                line = new StringBuilder();
            }
        }
        if (!line.isEmpty()) {
            result.append(line);
        }

        return Component.literal(result.toString()).withStyle(formats);
    }

    public static void rebuildChunkAndThen(Level world, BlockPos pos, Runnable action) {
        CHUNK_UPDATE_TASKS.computeIfAbsent(SectionPos.of(pos), k -> new ExecutableHashSet()).add(action);
        rebuildChunk(world, pos);
    }

    public static void rebuildChunk(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Minecraft.getInstance().levelRenderer.blockChanged(world, pos, state, state, 8);
    }

    public static boolean isChristmas() {
        String config = EBE.ebeCfgEnumValue(cfg -> cfg.christmasChests).name();
        if(config.equals("disabled")) return false;
        if(config.equals("forced")) return true;
        Calendar calendar = Calendar.getInstance();
        return (calendar.get(Calendar.MONTH) + 1 == 12 && calendar.get(Calendar.DATE) >= 24 && calendar.get(Calendar.DATE) <= 26);
    }
}
