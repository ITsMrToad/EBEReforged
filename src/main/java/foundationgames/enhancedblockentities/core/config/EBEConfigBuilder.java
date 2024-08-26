package foundationgames.enhancedblockentities.core.config;

import com.google.common.collect.ImmutableList;
import foundationgames.enhancedblockentities.common.util.EBEOtherUtils;
import me.jellysquid.mods.sodium.client.gui.options.OptionGroup;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpact;
import me.jellysquid.mods.sodium.client.gui.options.OptionImpl;
import me.jellysquid.mods.sodium.client.gui.options.OptionPage;
import me.jellysquid.mods.sodium.client.gui.options.control.CyclingControl;
import me.jellysquid.mods.sodium.client.gui.options.control.TickBoxControl;
import net.minecraft.network.chat.Component;
import org.embeddedt.embeddium.client.gui.options.OptionIdentifier;

import org.jetbrains.annotations.ApiStatus;
import java.util.List;

@ApiStatus.Internal
public class EBEConfigBuilder {

    private final EBEConfigStorage storage;

    public EBEConfigBuilder(EBEConfig cfg) {
        this.storage = new EBEConfigStorage(cfg);
    }

    public void build(List<OptionPage> pages) {

        OptionImpl<EBEConfig, Boolean> eCh = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.render_enhanced_chests"))
                .setTooltip(Component.translatable("ebe.config.render_enhanced_chests.comment"))
                .setFlags().setImpact(OptionImpact.HIGH).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.renderEnhancedChests = b, cfg -> cfg.renderEnhancedChests).build();

        OptionImpl<EBEConfig, Boolean> eSg = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.render_enhanced_signs"))
                .setTooltip(Component.translatable("ebe.config.render_enhanced_signs.comment"))
                .setFlags().setImpact(OptionImpact.MEDIUM).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.renderEnhancedSigns = b, cfg -> cfg.renderEnhancedSigns).build();

        OptionImpl<EBEConfig, Boolean> eBl = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.render_enhanced_bells"))
                .setTooltip(Component.translatable("ebe.config.render_enhanced_bells.comment"))
                .setFlags().setImpact(OptionImpact.LOW).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.renderEnhancedBells = b, cfg -> cfg.renderEnhancedBells).build();

        OptionImpl<EBEConfig, Boolean> eBd = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.render_enhanced_beds"))
                .setTooltip(Component.translatable("ebe.config.render_enhanced_beds.comment"))
                .setFlags().setImpact(OptionImpact.MEDIUM).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.renderEnhancedBeds = b, cfg -> cfg.renderEnhancedBeds).build();

        OptionImpl<EBEConfig, Boolean> eSh = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.render_enhanced_shulker_boxes"))
                .setTooltip(Component.translatable("ebe.config.render_enhanced_shulker_boxes.comment"))
                .setFlags().setImpact(OptionImpact.VARIES).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.renderEnhancedShulkerBoxes = b, cfg -> cfg.renderEnhancedShulkerBoxes).build();

        OptionImpl<EBEConfig, Boolean> eDp = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.render_enhanced_decorated_pots"))
                .setTooltip(Component.translatable("ebe.config.render_enhanced_decorated_pots.comment"))
                .setFlags().setImpact(OptionImpact.VARIES).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.renderEnhancedShulkerBoxes = b, cfg -> cfg.renderEnhancedShulkerBoxes).build();

        OptionGroup g1 = OptionGroup.createBuilder().add(eCh).add(eSg).add(eBl).add(eBd).add(eSh).add(eDp).build();

        OptionImpl<EBEConfig, Boolean> chAo = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.chest_ao"))
                .setTooltip(Component.translatable("ebe.config.chest_ao.comment"))
                .setFlags().setImpact(OptionImpact.HIGH).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.chestAO = b, cfg -> cfg.chestAO).build();

        OptionImpl<EBEConfig, Boolean> sgAo = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.sign_ao"))
                .setTooltip(Component.translatable("ebe.config.sign_ao.comment"))
                .setFlags().setImpact(OptionImpact.MEDIUM).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.signAO = b, cfg -> cfg.signAO).build();

        OptionImpl<EBEConfig, Boolean> blAo = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.bell_ao"))
                .setTooltip(Component.translatable("ebe.config.bell_ao.comment"))
                .setFlags().setImpact(OptionImpact.LOW).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.bellAO = b, cfg -> cfg.bellAO).build();

        OptionImpl<EBEConfig, Boolean> bdAo = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.bed_ao"))
                .setTooltip(Component.translatable("ebe.config.bed_ao.comment"))
                .setFlags().setImpact(OptionImpact.MEDIUM).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.bedAO = b, cfg -> cfg.bedAO).build();

        OptionImpl<EBEConfig, Boolean> shAo = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.shulker_box_ao"))
                .setTooltip(Component.translatable("ebe.config.shulker_box_ao.comment"))
                .setFlags().setImpact(OptionImpact.MEDIUM).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.shulkerBoxAO = b, cfg -> cfg.shulkerBoxAO).build();

        OptionImpl<EBEConfig, Boolean> dpAO = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.decorated_pot_ao"))
                .setTooltip(Component.translatable("ebe.config.decorated_pot_ao.comment"))
                .setFlags().setImpact(OptionImpact.LOW).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.decoratedPotAO = b, cfg -> cfg.decoratedPotAO).build();

        OptionGroup g2 = OptionGroup.createBuilder().add(chAo).add(sgAo).add(blAo).add(bdAo).add(shAo).add(dpAO).build();

        OptionImpl<EBEConfig, Boolean> expCh = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.experimental_chests"))
                .setTooltip(Component.translatable("ebe.config.experimental_chests.comment"))
                .setFlags().setImpact(OptionImpact.MEDIUM).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.experimentalChests = b, cfg -> cfg.experimentalChests).build();

        OptionImpl<EBEConfig, Boolean> expBd = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.experimental_beds"))
                .setTooltip(Component.translatable("ebe.config.experimental_beds.comment"))
                .setFlags().setImpact(OptionImpact.MEDIUM).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.experimentalBeds = b, cfg -> cfg.experimentalBeds).build();

        OptionImpl<EBEConfig, Boolean> expSg = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.experimental_signs"))
                .setTooltip(Component.translatable("ebe.config.experimental_signs.comment"))
                .setFlags().setImpact(OptionImpact.MEDIUM).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.experimentalSigns = b, cfg -> cfg.experimentalSigns).build();


        OptionGroup g3 = OptionGroup.createBuilder().add(expCh).add(expBd).add(expSg).build();

        Component[] cArray = new Component[]{
                Component.translatable("value.ebe.true"),
                Component.translatable("value.ebe.false"),
                Component.translatable("value.ebe.allowed"),
                Component.translatable("value.ebe.forced"),
                Component.translatable("value.ebe.disabled"),
                Component.translatable("value.ebe.smart"),
                Component.translatable("value.ebe.all"),
                Component.translatable("value.ebe.most"),
                Component.translatable("value.ebe.some"),
                Component.translatable("value.ebe.few")
        };


        OptionImpl<EBEConfig, EBEConfigEnumValue> christmasChests = OptionImpl.createBuilder(EBEConfigEnumValue.class, this.storage)
                .setName(Component.translatable("ebe.config.christmas_chests"))
                .setTooltip(Component.translatable("ebe.config.christmas_chests.comment"))
                .setFlags().setImpact(OptionImpact.LOW).setControl(cfg -> new CyclingControl<>(cfg, EBEConfigEnumValue.class, cArray))
                .setBinding((cfg, b) -> cfg.christmasChests = b, cfg -> cfg.christmasChests).build();

        OptionImpl<EBEConfig, EBEConfigEnumValue> signTextRendering = OptionImpl.createBuilder(EBEConfigEnumValue.class, this.storage)
                .setName(Component.translatable("ebe.config.sign_text_rendering"))
                .setTooltip(Component.translatable("ebe.config.sign_text_rendering.comment"))
                .setFlags().setImpact(OptionImpact.MEDIUM).setControl(cfg -> new CyclingControl<>(cfg, EBEConfigEnumValue.class, cArray))
                .setBinding((cfg, b) -> cfg.signTextRendering = b, cfg -> cfg.signTextRendering).build();

        OptionGroup g4 = OptionGroup.createBuilder().add(christmasChests).add(signTextRendering).build();

        OptionImpl<EBEConfig, Boolean> force = OptionImpl.createBuilder(boolean.class, this.storage)
                .setName(Component.translatable("ebe.config.force_resource_pack_compat"))
                .setTooltip(Component.translatable("ebe.config.force_resource_pack_compat.comment"))
                .setFlags().setImpact(OptionImpact.VARIES).setControl(TickBoxControl::new)
                .setBinding((cfg, b) -> cfg.forceResourcePackCompat = b, cfg -> cfg.forceResourcePackCompat).build();

        OptionGroup g5 = OptionGroup.createBuilder().add(force).build();

        pages.add(new OptionPage(OptionIdentifier.create(EBEOtherUtils.id("general")), Component.translatable("stat.generalButton"), ImmutableList.of(g1, g2, g3, g4, g5)));
    }

}
