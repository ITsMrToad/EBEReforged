package foundationgames.enhancedblockentities.common.util;

public interface ChunkRenderTaskAccess {

    Runnable ebe$getTaskAfterRebuild();

    void ebe$setTaskAfterRebuild(Runnable task);

    default void runAfterRebuildTask() {
        Runnable task = this.ebe$getTaskAfterRebuild();
        if (task != null) {
            task.run();
            this.ebe$setTaskAfterRebuild(null);
        }
    }
}
