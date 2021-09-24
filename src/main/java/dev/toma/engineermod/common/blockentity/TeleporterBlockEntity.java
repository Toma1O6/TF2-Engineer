package dev.toma.engineermod.common.blockentity;

import dev.toma.engineermod.common.init.BlockEntities;
import dev.toma.engineermod.common.init.Sounds;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

/**
 * @author Toma
 * @version 1.0
 */
public class TeleporterBlockEntity extends TileEntity implements ITickableTileEntity {

    private static final int TELEPORT_INTERVAL = 24;

    private BlockPos linkedDest;
    private UUID teleportTarget;
    private int teleportTicks = TELEPORT_INTERVAL;
    private int teleportDelay;

    public TeleporterBlockEntity(TileEntityType<?> type) {
        super(type);
    }

    public TeleporterBlockEntity() {
        this(BlockEntities.TELEPORTER_TYPE.get());
    }

    public void breakLink() {
        linkedDest = null;
    }

    public void linkTo(BlockPos pos) {
        linkedDest = pos;
    }

    public void requestTeleportation(UUID uuid) {
        if (!validateOn(uuid)) {
            teleportTarget = uuid;
            teleportTicks = TELEPORT_INTERVAL;
        }
    }

    @Override
    public void tick() {
        if (teleportDelay > 0) {
            --teleportDelay;
            return;
        }
        if (level.getGameTime() % 55L == 0L) {
            BlockPos pos = worldPosition;
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), Sounds.TELEPORTER_SPIN, SoundCategory.MASTER, 1.0F, 1.0F);
        }
        if (linkedDest == null || teleportTarget == null)
            return;
        if (validateOn(teleportTarget)) {
            if (--teleportTicks <= 0) {
                doTeleport();
            }
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        if (linkedDest != null) {
            nbt.put("linkedTarget", NBTUtil.writeBlockPos(linkedDest));
        }
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        if (nbt.contains("linkedTarget", Constants.NBT.TAG_COMPOUND)) {
            linkedDest = NBTUtil.readBlockPos(nbt.getCompound("linkedTarget"));
        }
    }

    private void doTeleport() {
        if (!level.isClientSide && linkedDest != null) {
            TileEntity tile = level.getBlockEntity(linkedDest);
            if (!(tile instanceof TeleporterBlockEntity)) {
                return;
            }
            TeleporterBlockEntity teleporter = (TeleporterBlockEntity) tile;
            boolean canTp = true;
            if (!teleporter.linkedDest.equals(worldPosition)) {
                teleporter.breakLink();
                this.breakLink();
                canTp = false;
            }
            teleportDelay = 100;
            teleporter.teleportDelay = 100;
            if (canTp) {
                PlayerEntity player = level.getPlayerByUUID(teleportTarget);
                if (player != null) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.TELEPORTER_SEND, SoundCategory.MASTER, 1.0F, 1.0F);
                    player.teleportTo(linkedDest.getX() + 0.5, linkedDest.getY() + 0.1, linkedDest.getZ() + 0.5);
                }
            }
        }
    }

    private boolean validateOn(UUID uuid) {
        if (uuid == null)
            return false;
        PlayerEntity player = level.getPlayerByUUID(uuid);
        if (player == null)
            return false;
        return player.blockPosition().equals(worldPosition);
    }
}
