package dev.toma.engineermod;

import dev.toma.engineermod.common.init.ModdedItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

/**
 * Class containg all references to mod item groups
 *
 * @author Toma
 * @version 1.0
 */
public final class EngineerGroups {

    /**
     * Main item group for all modded features
     */
    public static final ItemGroup ENGINEER = new ItemGroup("tf2.engineer") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModdedItems.WRENCH);
        }
    };

    /**
     * Private constructor
     */
    private EngineerGroups() {}
}
