package foundationgames.enhancedblockentities.client.resource;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class AtlasResourceBuilder {

    private static final Gson GSON = new Gson();
    private final List<SpriteSource> sources = Lists.newArrayList();

    public void put(SpriteSource source) {
        this.sources.add(source);
    }

    public byte[] toBytes() {
        return GSON.toJson(SpriteSources.FILE_CODEC.encode(this.sources, JsonOps.INSTANCE, new JsonObject()).getOrThrow(false, EBE.LOGGER::error)).getBytes(StandardCharsets.UTF_8);
    }
}


