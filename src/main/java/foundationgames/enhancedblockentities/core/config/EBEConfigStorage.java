package foundationgames.enhancedblockentities.core.config;

import me.jellysquid.mods.sodium.client.gui.options.storage.OptionStorage;

public class EBEConfigStorage implements OptionStorage<EBEConfig> {

    private final EBEConfig config;

    public EBEConfigStorage(EBEConfig config) {
        this.config = config;
    }

    @Override
    public EBEConfig getData() {
        return this.config;
    }

    @Override
    public void save() {
        this.config.save();
    }
}
