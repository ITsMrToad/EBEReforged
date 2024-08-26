package foundationgames.enhancedblockentities.common.util.hacks;

import foundationgames.enhancedblockentities.client.resource.EBEPack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

public class ResourceHacks {

    private static void cropAndPutTexture(ResourceLocation source, ResourceLocation result, ResourceManager manager, EBEPack pack, float u0, float v0, float u1, float v1) throws IOException {
        InputStream image;
        try {
            image = manager.getResource(source).orElseThrow().open();
        } catch (IOException | NoSuchElementException e) {
            return;
        }

        TextureHacks.cropImage(image, u0, v0, u1, v1).ifPresent(imgBytes -> pack.addResource(result, imgBytes));
        image.close();
    }

    public static void addChestParticleTexture(String chestName, String chestTexture, ResourceManager manager, EBEPack pack) throws IOException {
        cropAndPutTexture(new ResourceLocation("textures/"+chestTexture+".png"), new ResourceLocation("textures/block/"+chestName+"_particle.png"), manager, pack, 42f/64, 33f/64, 58f/64, 49f/64);
    }

    public static void addBedParticleTexture(String bedColor, String bedTexture, ResourceManager manager, EBEPack pack) throws IOException {
        cropAndPutTexture(new ResourceLocation("textures/"+bedTexture+".png"), new ResourceLocation("textures/block/"+bedColor+"_bed_particle.png"), manager, pack, 18f/64, 6f/64, 34f/64, 22f/64);
    }

    public static void addSignParticleTexture(String signType, String signTexture, ResourceManager manager, EBEPack pack) throws IOException {
        cropAndPutTexture(new ResourceLocation("textures/"+signTexture+".png"), new ResourceLocation("textures/block/"+signType+"_sign_particle.png"), manager, pack, 0, 3f/32, 16f/64, 19f/32);
    }

}
