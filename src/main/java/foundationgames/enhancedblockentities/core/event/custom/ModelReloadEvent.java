package foundationgames.enhancedblockentities.core.event.custom;

import com.google.common.collect.Lists;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import org.jetbrains.annotations.ApiStatus;
import java.util.List;

@Cancelable
public class ModelReloadEvent extends Event {

    private final List<Runnable> runnables = Lists.newArrayList();

    @ApiStatus.Internal
    public void runAll() {
        this.runnables.forEach(Runnable::run);
    }

    public void register(Runnable runnable) {
        this.runnables.add(runnable);
    }
}
