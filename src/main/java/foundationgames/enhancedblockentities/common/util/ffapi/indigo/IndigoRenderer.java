package foundationgames.enhancedblockentities.common.util.ffapi.indigo;

import com.google.common.collect.Maps;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material.MaterialLookup;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.material.MaterialSearcher;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.api.geom.mesh.MeshBuilder;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.material.OpenMaterialSearcher;
import foundationgames.enhancedblockentities.common.util.ffapi.indigo.work.mesh.SimpleMeshBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public class IndigoRenderer {

    public static final IndigoRenderer INSTANCE = new IndigoRenderer();
    public static final MaterialLookup MATERIAL_STANDARD = INSTANCE.materialFinder().find();
    public static final ResourceLocation DEFAULT = new ResourceLocation("indigo", "material");

    private final HashMap<ResourceLocation, MaterialLookup> materialMap = Maps.newHashMap();

    static {
        INSTANCE.registerMaterial(DEFAULT, MATERIAL_STANDARD);
    }

    public MeshBuilder meshBuilder() {
        return new SimpleMeshBuilder();
    }

    public MaterialSearcher materialFinder() {
        return new OpenMaterialSearcher();
    }

    public MaterialLookup materialById(ResourceLocation id) {
        return this.materialMap.get(id);
    }

    @CanIgnoreReturnValue
    public boolean registerMaterial(ResourceLocation id, MaterialLookup material) {
        if (this.materialMap.containsKey(id)) {
            return false;
        }
        this.materialMap.put(id, material);
        return true;
    }

}
