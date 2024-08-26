package foundationgames.enhancedblockentities.core.mixin;

import com.google.common.collect.Lists;
import foundationgames.enhancedblockentities.common.util.ResourceUtil;
import foundationgames.enhancedblockentities.core.loader.ExperimentalSetup;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(MultiPackResourceManager.class)
public abstract class MultiPackResourceManagerMixin {

    @Shadow @Final private Map<String, FallbackResourceManager> namespacedManagers;

    @ModifyVariable(method = "<init>", at = @At(value = "INVOKE_ASSIGN", target = "Ljava/util/List;copyOf(Ljava/util/Collection;)Ljava/util/List;", shift = At.Shift.BEFORE), ordinal = 0, argsOnly = true)
    private List<PackResources> enhanced_bes$injectBasePack(List<PackResources> old) {
        List<PackResources> packs = Lists.newArrayList(old);
        int idx = 0;
        if (!packs.isEmpty()) do {
            idx++;
        } while (idx < packs.size() && !(packs.get(idx - 1) instanceof VanillaPackResources));
        packs.add(idx, ResourceUtil.getBasePack());
        return packs;
    }

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void enhanced_bes$injectTopLevelPack(PackType type, List<PackResources> packs, CallbackInfo ci) {
        ExperimentalSetup.cacheResources((ResourceManager) this);
        ExperimentalSetup.setup();
        this.ebe$addPack(type, ResourceUtil.getTopLevelPack());
    }

    @Unique
    private void ebe$addPack(PackType type, PackResources pack) {
        for (var namespace : pack.getNamespaces(type)) {
            this.namespacedManagers.computeIfAbsent(namespace, n -> new FallbackResourceManager(type, n)).push(pack);
        }
    }
}
