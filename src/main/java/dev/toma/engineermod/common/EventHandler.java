package dev.toma.engineermod.common;

import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.common.entity.SentryEntity;
import dev.toma.engineermod.common.init.ModdedBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Event handler.
 *
 * @author Toma
 * @version 1.0
 */
@Mod.EventBusSubscriber(modid = EngineerMod.MODID)
public final class EventHandler {

    /**
     * Restricts block placement above dispensers
     * @param event The interact event
     */
    @SubscribeEvent
    public static void onPlayerTryPlaceBlock(PlayerInteractEvent.RightClickBlock event) {
        BlockPos pos = event.getPos();
        World level = event.getWorld();
        BlockState below = level.getBlockState(pos.relative(event.getHitVec().getDirection()).below());
        if (below.getBlock() == ModdedBlocks.DISPENSER) {
            event.setCanceled(true);
        }
    }

    /**
     * Adds entries as attack target
     * @param event The entity creation event
     */
    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof MobEntity && entity.getClassification(false) == EntityClassification.MONSTER) {
            MobEntity monster = (MobEntity) entity;
            monster.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(monster, SentryEntity.class, false));
        }
    }
}
