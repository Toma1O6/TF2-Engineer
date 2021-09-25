package dev.toma.engineermod.common.init;

import dev.toma.engineermod.EngineerGroups;
import dev.toma.engineermod.EngineerMod;
import dev.toma.engineermod.common.block.DispenserBlock;
import dev.toma.engineermod.common.block.TeleporterBlock;
import dev.toma.engineermod.common.entity.SentryEntity;
import dev.toma.engineermod.common.item.ItemMachineCreator;
import dev.toma.engineermod.common.item.WrenchItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Registration handler for common entries, such as items and blocks
 *
 * @author Toma
 * @version 1.0
 */
@Mod.EventBusSubscriber(modid = EngineerMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CommonRegistrationHandler {

    /**
     * Handles item registration
     * @param event Registry event
     */
    @SubscribeEvent
    public static void doItemRegistration(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.registerAll(
                new WrenchItem("wrench"),
                new ItemMachineCreator<>("sentry", new Item.Properties().tab(EngineerGroups.ENGINEER).stacksTo(1), (level, player, stack) -> new SentryEntity(Entities.SENTRY.get(), level, player, stack.getTag()))
        );
        registerBlockItems(registry);
    }

    /**
     * Handles block registration
     * @param event Registry event
     */
    @SubscribeEvent
    public static void doBlockRegistration(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();
        registry.registerAll(
                new DispenserBlock("dispenser", AbstractBlock.Properties.of(Material.STONE).noOcclusion().strength(2.2F).lightLevel(st -> 15).harvestTool(ToolType.PICKAXE)),
                new TeleporterBlock("teleporter", AbstractBlock.Properties.of(Material.METAL).noOcclusion().strength(2.2F).lightLevel(st -> 15))
        );
    }

    /**
     * Handles sound registration
     * @param event Registry event
     */
    @SubscribeEvent
    public static void doSoundRegistration(RegistryEvent.Register<SoundEvent> event) {
        IForgeRegistry<SoundEvent> registry = event.getRegistry();
        registry.registerAll(
                newSound("sentry_deploy"),
                newSound("sentry_scan"),
                newSound("sentry_shoot"),
                newSound("sentry_acquire_target"),
                newSound("teleporter_send"),
                newSound("dispenser_heal"),
                newSound("wrench_hit_build_success"),
                newSound("wrench_swing")
        );
    }

    /**
     * Handles entity attribute registration
     * @param event Attribute creation event
     */
    @SubscribeEvent
    public static void createAttributes(EntityAttributeCreationEvent event) {
        event.put(Entities.SENTRY.get(), SentryEntity.createAttributes().build());
    }

    /**
     * Registers items for all modded blocks
     * @param registry The item registry
     */
    private static void registerBlockItems(IForgeRegistry<Item> registry) {
        ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> block.getRegistryName().getNamespace().equals(EngineerMod.MODID)) // finds modded blocks
                .map(block -> new BlockItem(block, new Item.Properties().tab(EngineerGroups.ENGINEER)).setRegistryName(block.getRegistryName())) // convert blocks to items
                .forEach(registry::register); // do registration
    }

    /**
     * Creates new sound event instance with set registry name.
     * @param soundName The sound id
     * @return New sound instance
     */
    private static SoundEvent newSound(String soundName) {
        ResourceLocation rl = EngineerMod.createModPath(soundName);
        SoundEvent event = new SoundEvent(rl);
        event.setRegistryName(rl);
        return event;
    }

    // private constructor, no instances needed
    private CommonRegistrationHandler() {}
}
