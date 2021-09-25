package dev.toma.engineermod.common.container;

import dev.toma.engineermod.common.init.Containers;
import dev.toma.engineermod.common.item.WrenchItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.Tags;

/**
 * Simple container with single slot inventory for inputs.
 *
 * @author Toma
 * @version 1.0
 */
public class WrenchContainer extends Container {

    public static final int NUGGET_VOLUME = 20;
    public static final int INGOT_VOLUME = 9 * NUGGET_VOLUME;
    public static final int BLOCK_VOLUME = 9 * INGOT_VOLUME;

    /**
     * Single slot inventory for iron resource inputs
     */
    private final Inventory simpleInventory = new Inventory(1);
    private final ItemStack stack;
    private final PlayerEntity owner;

    public WrenchContainer(int windowID, PlayerInventory inventory, PacketBuffer buffer) {
        this(Containers.WRENCH_CONTAINER.get(), windowID, inventory, inventory.player.getMainHandItem());
    }

    public WrenchContainer(ContainerType<?> type, int containerID, PlayerInventory playerInventory, ItemStack stack) {
        super(type, containerID);
        this.owner = playerInventory.player;
        this.stack = stack;

        addSlot(new Slot(simpleInventory, 0, 62, 13) {
            @Override
            public boolean mayPlace(ItemStack st) {
                return isValidInput(st);
            }
        });

        // player inventory
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 9; ++x) {
                addSlot(new Slot(playerInventory, 9 + y * 9 + x, 8 + x * 18, 90 + y * 18));
            }
        }
        // hotbar
        for (int x = 0; x < 9; ++x) {
            addSlot(new Slot(playerInventory, x, 8 + x * 18, 148));
        }
        addSlotListener(new ContainerListener(this::clearInventory));
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        if (slot != null && slot.hasItem() && index > 0) {
            ItemStack stack = slot.getItem();
            if (!moveItemStackTo(stack, 0, 1, false)) {
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return true;
    }

    private void clearInventory() {
        if (stack.getItem() instanceof WrenchItem) {
            ItemStack input = simpleInventory.getItem(0);
            if (!input.isEmpty()) {
                Item in = input.getItem();
                int count = input.getCount();
                if (in.is(Tags.Items.INGOTS_IRON)) {
                    WrenchItem.growIronVolume(stack, INGOT_VOLUME * count);
                } else if (in.is(Tags.Items.NUGGETS_IRON)) {
                    WrenchItem.growIronVolume(stack, NUGGET_VOLUME * count);
                } else if (in == Items.IRON_BLOCK) {
                    WrenchItem.growIronVolume(stack, BLOCK_VOLUME * count);
                }
                if (owner instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayer = (ServerPlayerEntity) owner;
                    forceNbtUpdate(serverPlayer.connection);
                    serverPlayer.connection.send(new SSetSlotPacket(containerId, 0, slots.get(0).getItem().copy()));
                }
            }
            simpleInventory.clearContent();
        } else {
            InventoryHelper.dropContents(owner.level, owner, simpleInventory);
        }
    }

    private void forceNbtUpdate(ServerPlayNetHandler netHandler) {
        for (int i = 0; i < slots.size(); i++) {
            ItemStack itemstack = this.slots.get(i).getItem();
            ItemStack itemstack1 = this.lastSlots.get(i);
            if (!ItemStack.matches(itemstack1, itemstack)) {
                boolean clientStackChanged = !itemstack1.equals(itemstack, true);
                ItemStack itemstack2 = itemstack.copy();
                if (clientStackChanged) {
                    netHandler.send(new SSetSlotPacket(containerId, i, itemstack2));
                }
            }
        }
    }

    private static boolean isValidInput(ItemStack stack) {
        Item item = stack.getItem();
        return item == Items.IRON_BLOCK || item.is(Tags.Items.INGOTS_IRON) || item.is(Tags.Items.NUGGETS_IRON);
    }

    private static class ContainerListener implements IContainerListener {

        private final IClearCallback callback;

        public ContainerListener(IClearCallback callback) {
            this.callback = callback;
        }

        @Override
        public void refreshContainer(Container container, NonNullList<ItemStack> stacks) {}

        @Override
        public void slotChanged(Container container, int slot, ItemStack stack) {
            if (slot == 0) {
                callback.clearInventory();
            }
        }

        @Override
        public void setContainerData(Container container, int index, int data) {}
    }

    private interface IClearCallback {
        void clearInventory();
    }
}
