package foundationgames.enhancedblockentities.client.renderer;

import foundationgames.enhancedblockentities.common.util.ModelStateHolder;
import foundationgames.enhancedblockentities.core.EBE;
import foundationgames.enhancedblockentities.core.config.EBEConfigEnumValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface BlockEntityRendererCondition {

    BlockEntityRendererCondition STATE_GREATER_THAN_1 = entity -> {
        if(entity instanceof ModelStateHolder stateHolder) {
            return stateHolder.ebe$getModelState() > 0;
        }
        return false;
    };

    BlockEntityRendererCondition CHEST = STATE_GREATER_THAN_1;

    BlockEntityRendererCondition BELL = STATE_GREATER_THAN_1;

    BlockEntityRendererCondition SHULKER_BOX = STATE_GREATER_THAN_1;

    BlockEntityRendererCondition SIGN = entity -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            if (EBE.ebeCfgEnumValue(cfg -> cfg.signTextRendering) == EBEConfigEnumValue.ALL) {
                return true;
            }
            double playerDistance = 0;
            playerDistance = player.blockPosition().distSqr(entity.getBlockPos());

            if (EBE.ebeCfgEnumValue(cfg -> cfg.signTextRendering) == EBEConfigEnumValue.SMART) {
                SignRendererManager.RENDERED++;
                return playerDistance < 80 + Math.max(0, 580 - (SignRendererManager.getRenderedSignAmount() * 0.7));
            }
            double dist = Mth.square(16);
            Vec3 blockPos = Vec3.atCenterOf(entity.getBlockPos());
            Vec3 playerPos = player.position();
            if (EBE.ebeCfgEnumValue(cfg -> cfg.signTextRendering) == EBEConfigEnumValue.MOST) {
                return blockPos.closerThan(playerPos, dist * 0.6);
            }
            if (EBE.ebeCfgEnumValue(cfg -> cfg.signTextRendering) == EBEConfigEnumValue.SOME) {
                return blockPos.closerThan(playerPos, dist * 0.3);
            }
            if (EBE.ebeCfgEnumValue(cfg -> cfg.signTextRendering) == EBEConfigEnumValue.FEW) {
                return blockPos.closerThan(playerPos, dist * 0.15);
            }
        }
        return false;
    };

    BlockEntityRendererCondition NEVER = entity -> false;

    BlockEntityRendererCondition ALWAYS = entity -> true;

    boolean shouldRender(BlockEntity entity);

}
