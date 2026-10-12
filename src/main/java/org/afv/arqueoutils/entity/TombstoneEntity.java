package org.afv.arqueoutils.entity;

import com.mojang.serialization.Codec;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.util.EnchHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TombstoneEntity extends Entity {

    private UUID ownerUuid;
    private final List<TombSlot> storedItems = new ArrayList<>();
    private UUID displayUuid;
    private int age;
    private static final int DESPAWN_AGE = 72000; // 1 hora

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    public TombstoneEntity(EntityType<? extends TombstoneEntity> type, World world) {
        super(type, world);
    }

    @Override
    public void tick() {
        if (!getEntityWorld().isClient()) {
            age++;

            if (age >= DESPAWN_AGE) {
                if (getEntityWorld() instanceof ServerWorld world) {
                    expire(world);
                }
            }
        }
        Vec3d pos = getEntityPos();
        getEntityWorld().addParticleClient(
                ParticleTypes.SOUL_FIRE_FLAME,
                pos.x + random.nextFloat() - 0.5,
                pos.y + getHeight()/2 + random.nextFloat() - 0.5,
                pos.z + random.nextFloat() - 0.5,
                0, 0, 0
        );
        super.tick();
    }

    private void expire(ServerWorld world) {
        // Drop stored items or whatever behavior you want

        if (world.getEntity(displayUuid) instanceof DisplayEntity display) {
            display.discard();
        }

        discard();
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) {
            return false;
        }

        // Only owner can retrieve
        if (!player.getUuid().equals(ownerUuid)) {
            return false;
        }

        restoreInventory(player);

        if (world.getEntity(displayUuid) instanceof DisplayEntity display) display.discard();

        world.spawnParticles(
                ParticleTypes.SOUL,
                getX(),
                getY() + getHeight()/2,
                getZ(),
                20,
                0.25, 0.25, 0.25,
                0.01
        );

        discard();

        return true;
    }

    @Override
    protected void readCustomData(ReadView view) {
        age = view.getInt("Age", 0);
        ownerUuid = view.read("owner", Uuids.INT_STREAM_CODEC)
                .orElse(null);

        view.read("displayUuid", Codec.STRING).ifPresent(displayUuidStr -> this.displayUuid = UUID.fromString(displayUuidStr));

        storedItems.clear();

        view.read("items", TombstoneCodecs.TOMB_SLOT_LIST_CODEC)
                .ifPresent(storedItems::addAll);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.putInt("Age", age);
        if (ownerUuid != null) {
            view.put("owner", Uuids.INT_STREAM_CODEC, ownerUuid);
        }

        if (displayUuid != null) {
            view.putString("displayUuid", displayUuid.toString());
        }

        view.put("items", TombstoneCodecs.TOMB_SLOT_LIST_CODEC, storedItems);
    }

    private void restoreInventory(ServerPlayerEntity player) {
        PlayerInventory inv = player.getInventory();
        ArrayList<ItemStack> retained = new ArrayList<>();

        for (TombSlot slotData : storedItems) {
            int slot = slotData.slot();
            ItemStack stack = slotData.stack();

            if (EnchHelper.getLevel(stack, Enchantments.VANISHING_CURSE) > 0) continue;

            ItemStack existing = inv.getStack(slot);

            if (slot > 35 && slot < 40 && EnchHelper.getLevel(stack, Enchantments.BINDING_CURSE) > 0) {
                if (!player.giveItemStack(stack.copy())) retained.add(stack.copy());
            } else if (!existing.isEmpty()) {
                if (slot > 35 && slot < 40 && EnchHelper.getLevel(existing, Enchantments.BINDING_CURSE) > 0) {
                    if (!player.giveItemStack(stack.copy())) retained.add(stack.copy());
                } else {
                    inv.setStack(slot, stack.copy());
                    if (!player.giveItemStack(existing.copy())) retained.add(existing.copy());
                }
            } else {
                inv.setStack(slot, stack.copy());
            }
        }

        if (!retained.isEmpty()) {
            ItemStack deathPouch = new ItemStack(ModItems.DEATH_POUCH);

            DefaultedList<ItemStack> list = DefaultedList.ofSize(retained.size(), ItemStack.EMPTY);
            for (int i = 0; i < retained.size(); i++) {
                list.set(i, retained.get(i));
            }

            deathPouch.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(list));

            ItemEntity pouchEntity = new ItemEntity(getEntityWorld(), getX(), getY(), getZ(), deathPouch);
            pouchEntity.setNoGravity(true);
            pouchEntity.setVelocity(0,0,0);
            pouchEntity.setInvulnerable(true);
            getEntityWorld().spawnEntity(pouchEntity);
        }

        storedItems.clear();
    }

    public void setOwner(ServerPlayerEntity player) {
        this.ownerUuid = player.getUuid();

        if (getEntityWorld() instanceof ServerWorld world) {
            DisplayEntity.ItemDisplayEntity display = EntityType.ITEM_DISPLAY.spawn(world, this.getBlockPos(), SpawnReason.TRIGGERED);
            if (display != null) {
                Vec3d pos = this.getEntityPos();
                display.teleport(world, pos.x, pos.y + 0.25 + this.getHeight()/2, pos.z, Set.of(), player.getHeadYaw() + 180, 0, false);
                this.displayUuid = display.getUuid();
                ItemStack head = new ItemStack(Items.PLAYER_HEAD);
                head.set(DataComponentTypes.PROFILE, ProfileComponent.ofDynamic(ownerUuid));
                display.setItemStack(head);
            }
        }
    }

    public void setStoredItems(List<TombSlot> list) {
        this.storedItems.clear();
        this.storedItems.addAll(list);
    }

    @Override
    public boolean canHit() {
        return !this.isRemoved();
    }

    @Override
    public PistonBehavior getPistonBehavior() {
        return PistonBehavior.IGNORE;
    }
}
