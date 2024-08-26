package foundationgames.enhancedblockentities.client.model.dynamic;

import com.google.common.collect.Lists;
import foundationgames.enhancedblockentities.core.EBE;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class DynamicModelEffects {

    private static final List<DynamicModelEffects> REGISTRY = Lists.newArrayList();

    public static final DynamicModelEffects DEFAULT = new DynamicModelEffects() {};

    public static final DynamicModelEffects CHEST = new DynamicModelEffects() {
        @Override
        public boolean ambientOcclusion() {
            return EBE.checkValue(cfg -> cfg.chestAO);
        }
    };

    public static final DynamicModelEffects BELL = new DynamicModelEffects() {
        @Override
        public boolean ambientOcclusion() {
            return EBE.checkValue(cfg -> cfg.bellAO);
        }
    };

    public static final DynamicModelEffects SHULKER_BOX = new DynamicModelEffects() {
        @Override
        public boolean ambientOcclusion() {
            return EBE.checkValue(cfg -> cfg.shulkerBoxAO);
        }
    };

    public static final DynamicModelEffects DECORATED_POT = new DynamicModelEffects() {
        @Override
        public boolean ambientOcclusion() {
            return EBE.checkValue(cfg -> cfg.decoratedPotAO);
        }
    };

    public final int id;

    public DynamicModelEffects() {
        this.id = REGISTRY.size();
        REGISTRY.add(this);
    }

    public boolean ambientOcclusion() {
        return true;
    }

    public static DynamicModelEffects fromId(int id) {
        return REGISTRY.get(id);
    }

}
