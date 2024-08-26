package foundationgames.enhancedblockentities.core.event;

import com.mojang.datafixers.util.Pair;
import foundationgames.enhancedblockentities.client.resource.EBEManager;
import foundationgames.enhancedblockentities.core.EBE;
import foundationgames.enhancedblockentities.core.event.custom.ModelReloadEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = EBE.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EBECommonEvents {

    @SubscribeEvent
    public static void registerEBEManager(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new EBEManager());
    }

    @Mod.EventBusSubscriber(modid = EBE.MODID)
    public static class NoBus {
        @SubscribeEvent
        public static void onReload(ModelReloadEvent event) {
            EBE.ENTITIES.values().stream().map(Pair::getSecond).forEach(override -> event.register(override::onModelsReload));
        }
    }
}
