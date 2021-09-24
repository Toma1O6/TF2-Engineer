package dev.toma.engineermod.common.entity;

import dev.toma.engineermod.client.EngineerModClient;
import dev.toma.engineermod.common.ILiquidIronStorage;
import dev.toma.engineermod.common.entity.task.SentryFindTargetTask;
import dev.toma.engineermod.common.entity.task.SentryShootTask;
import dev.toma.engineermod.common.init.ModdedItems;
import dev.toma.engineermod.common.init.Sounds;
import dev.toma.engineermod.network.NetworkHandler;
import dev.toma.engineermod.network.packet.SC_UpdateSentryData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Sentry entity class.
 *
 * @author Toma
 * @version 1.0
 */
public class SentryEntity extends MobEntity implements ILiquidIronStorage, IEntityAdditionalSpawnData {

    private static final int IRON_CAPACITY = 300;
    private final Set<UUID> whitelist = new HashSet<>();
    private UUID owner;
    private SentryTargetType targetType = SentryTargetType.HOSTILE;
    private int liquidIron;
    private int idleTicks;

    public SentryEntity(EntityType<? extends SentryEntity> type, World level) {
        this(type, level, null);
    }

    public SentryEntity(EntityType<? extends SentryEntity> type, World level, PlayerEntity player, @Nullable CompoundNBT data) {
        this(type, level, player);
        if (data != null) {
            loadInternalDataFrom(data);
        }
    }

    public SentryEntity(EntityType<? extends SentryEntity> type, World level, @Nullable PlayerEntity owner) {
        super(type, level);
        setOwner(owner);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createLivingAttributes().add(Attributes.MOVEMENT_SPEED, 0.0).add(Attributes.KNOCKBACK_RESISTANCE, 100.0).add(Attributes.MAX_HEALTH, 12.0).add(Attributes.FOLLOW_RANGE, 30.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (getTarget() != null) {
            idleTicks = 1;
        } else {
            idleTicks++;
        }
        if (idleTicks % 200 == 0) {
            level.playSound(null, getX(), getY(), getZ(), Sounds.SENTRY_SCAN, SoundCategory.MASTER, 1.0F, 1.0F);
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer) {
        CompoundNBT nbt = new CompoundNBT();
        saveInternalDataTo(nbt);
        buffer.writeNbt(nbt);
    }

    @Override
    public void readSpawnData(PacketBuffer additionalData) {
        loadInternalDataFrom(additionalData.readNbt());
    }

    @Override
    public void setTarget(@Nullable LivingEntity entity) {
        LivingEntity oldTarget = getTarget();
        super.setTarget(entity);
        if (!Objects.equals(entity, oldTarget) && entity != null) {
            level.playSound(null, getX(), getY(), getZ(), Sounds.SENTRY_ACQUIRE_TARGET, SoundCategory.MASTER, 1.0F, 1.0F);
        }
    }

    @Override
    public int getFluidVolume() {
        return liquidIron;
    }

    @Override
    public void setIronVolume(int volume) {
        liquidIron = MathHelper.clamp(volume, 0, IRON_CAPACITY);
    }

    @Override
    public int insertIron(int amount) {
        int over = Math.max(0, liquidIron + amount - IRON_CAPACITY);
        setIronVolume(liquidIron + amount);
        return over;
    }

    @Override
    public int extractIron(int amount) {
        int extract = Math.min(amount, liquidIron);
        setIronVolume(liquidIron - amount);
        return extract;
    }

    public boolean hasAmmo() {
        return liquidIron >= 2;
    }

    public void consumeAmmo() {
        setIronVolume(liquidIron - 2);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void push(Entity p_70108_1_) {
    }

    @Override
    public void push(double p_70024_1_, double p_70024_3_, double p_70024_5_) {
    }

    public boolean isOwner(UUID uuid) {
        return uuid != null && Objects.equals(owner, uuid);
    }

    public boolean isWhitelisted(UUID uuid) {
        return whitelist.contains(uuid);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        saveInternalDataTo(nbt);
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        loadInternalDataFrom(nbt);
    }

    public void addToWhitelist(UUID uuid) {
        whitelist.add(uuid);
    }

    public void removeFromWhitelist(UUID uuid) {
        whitelist.remove(uuid);
    }

    public Set<UUID> getWhitelist() {
        return whitelist;
    }

    public void saveInternalDataTo(CompoundNBT nbt) {
        if (owner != null)
            nbt.putUUID("owner", owner);
        nbt.putByte("targetType", (byte) targetType.ordinal());
        nbt.putInt("liquidIron", liquidIron);
        ListNBT whitelisted = new ListNBT();
        for (UUID uuid : whitelist) {
            whitelisted.add(NBTUtil.createUUID(uuid));
        }
        nbt.put("whitelist", whitelisted);
        nbt.putFloat("healthStat", getHealth());
    }

    public void loadInternalDataFrom(CompoundNBT nbt) {
        if (nbt.contains("owner")) {
            owner = nbt.getUUID("owner");
        }
        liquidIron = nbt.getInt("liquidIron");
        targetType = SentryTargetType.fromNbt(nbt);
        ListNBT list = nbt.getList("whitelist", Constants.NBT.TAG_INT_ARRAY);
        whitelist.clear();
        for (INBT inbt : list) {
            whitelist.add(NBTUtil.loadUUID(inbt));
        }
        setHealth(nbt.getFloat("healthStat"));
    }

    public SentryTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(SentryTargetType targetType) {
        this.targetType = Objects.requireNonNull(targetType);
    }

    public int getTargettingId() {
        return targetType.ordinal();
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new SentryShootTask(this));
        targetSelector.addGoal(0, new SentryFindTargetTask(this, true));
    }

    @Override
    protected ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == ModdedItems.WRENCH) {
            if (!level.isClientSide) {
                ItemStack result = new ItemStack(ModdedItems.SENTRY);
                CompoundNBT cnbt = new CompoundNBT();
                saveInternalDataTo(cnbt);
                result.setTag(cnbt);
                Vector3d position = position();
                ItemEntity entity = new ItemEntity(level, position.x, position.y + 0.1, position.z, result);
                level.addFreshEntity(entity);
                remove();
                return ActionResultType.SUCCESS;
            }
        } else if (stack.isEmpty()) {
            if (level.isClientSide) {
                EngineerModClient.instance().openSentryScreen(this);
            } else {
                CompoundNBT nbt = new CompoundNBT();
                saveInternalDataTo(nbt);
                NetworkHandler.SC_sendClientPacket((ServerPlayerEntity) player, new SC_UpdateSentryData(getId(), nbt));
            }
        }
        return ActionResultType.PASS;
    }

    private void setOwner(@Nullable PlayerEntity owner) {
        if (owner == null)
            this.owner = null;
        else
            this.owner = owner.getUUID();
    }
}
