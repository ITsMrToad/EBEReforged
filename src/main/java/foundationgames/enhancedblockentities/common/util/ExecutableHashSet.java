package foundationgames.enhancedblockentities.common.util;

import java.util.HashSet;

public class ExecutableHashSet extends HashSet<Runnable> implements Runnable {
    @Override
    public void run() {
        this.forEach(Runnable::run);
    }
}
