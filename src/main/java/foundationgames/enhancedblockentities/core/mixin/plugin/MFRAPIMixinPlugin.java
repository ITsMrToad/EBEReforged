package foundationgames.enhancedblockentities.core.mixin.plugin;

import foundationgames.enhancedblockentities.core.EBE;
import foundationgames.enhancedblockentities.core.config.EBEConfig;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MFRAPIMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (EBE.CONFIG == null) { //Now config creates here...
            EBE.CONFIG = EBEConfig.loadOrCreate();
        }

        //Disabling embedded FFAPI
        if (mixinClassName.startsWith("foundationgames.enhancedblockentities.core.mixin.mfrapi.") && EBE.CONFIG.forceMFAPICompat) {
            EBE.LOGGER.debug(EBE.FFAPI, "Sinytra detected. Force disabling MFRAPI.");
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}
