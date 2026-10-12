package org.afv.arqueoutils.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.afv.arqueoutils.init.ModBlockEntities;

import java.util.Objects;
import java.util.Optional;

public class ElytraDispenserBlockEntity extends BlockEntity {

    private final ElytraDispenserServerData serverData = new ElytraDispenserServerData();

    public ElytraDispenserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELYTRA_DISPENSER, pos, state);
    }

    public ActionResult tryDispense(ServerPlayerEntity player, Direction side, BlockPos blockPos) {
        if (serverData.hasRewardedPlayer(player)) {
            if (world != null) {
                world.playSound(null, this.getPos(), SoundEvents.ENTITY_ENDERMAN_SCREAM, SoundCategory.BLOCKS, 1, 0.75f);
            }
        }

        if (world != null) {
            world.playSound(null, this.getPos(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.BLOCKS, 1, 1.25f);
            world.playSound(null, this.getPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 0.5f, 1.25f);
            Vec3d vec = Vec3d.of(side.getVector());
            Vec3d pos = (new Vec3d(blockPos)).add(0.5, 0.45, 0.5).add(vec.multiply(0.5f));
            if (world instanceof ServerWorld server) {
                server.spawnParticles(ParticleTypes.CLOUD, pos.x, pos.y, pos.z, 4, 0, 0, 0, 0.01);
            }

            if (serverData.hasRewardedPlayer(player)) {
                world.playSound(null, this.getPos(), SoundEvents.ENTITY_ENDERMAN_SCREAM, SoundCategory.BLOCKS, 1, 0.75f);
                ShulkerBulletEntity bullet = new ShulkerBulletEntity(world, player, player, side.getAxis());
                pos = pos.add(vec.multiply(0.25f));
                bullet.setPos(pos.x, pos.y, pos.z);
                bullet.setVelocity(vec.multiply(0.2f));
                world.spawnEntity(bullet);
                return ActionResult.SUCCESS;
            }

            ItemEntity item = new ItemEntity(world, pos.x, pos.y, pos.z, new ItemStack(Items.ELYTRA));
            item.setVelocity(vec.multiply(0.75f));

            world.spawnEntity(item);
        }

        serverData.markPlayerAsRewarded(player);
        markDirty();

        return ActionResult.SUCCESS;
    }

    /* ---------- Persistence ---------- */

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        Optional<ElytraDispenserServerData> var10000 = view.read("server_data", ElytraDispenserServerData.codec);
        ElytraDispenserServerData var10001 = this.serverData;
        Objects.requireNonNull(var10001);
        var10000.ifPresent(var10001::copyFrom);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("server_data", ElytraDispenserServerData.codec, this.serverData);
    }
}