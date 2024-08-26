package foundationgames.enhancedblockentities.core.mixin;

import foundationgames.enhancedblockentities.core.event.custom.ModelReloadEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "reloadResourcePacks()Ljava/util/concurrent/CompletableFuture;", at = @At("RETURN"))
    public void ebeRun(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        cir.getReturnValue().thenRun(() -> {
            ModelReloadEvent event = new ModelReloadEvent();
            MinecraftForge.EVENT_BUS.post(event);
            event.runAll();
        });
    }

}
