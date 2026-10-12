package org.afv.arqueoutils.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.afv.arqueoutils.holder.AirRiptideHolder;
import org.afv.arqueoutils.holder.ElytraHolder;
import org.afv.arqueoutils.holder.MagnetHolder;
import org.afv.arqueoutils.init.ModAttributes;
import org.afv.arqueoutils.world.DisguiseState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;

import static org.afv.arqueoutils.util.SoulboundUtil.DEAD_PLAYERS;
import static org.afv.arqueoutils.util.SoulboundUtil.isStackSoulbound;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity implements MagnetHolder, AirRiptideHolder, ElytraHolder {
    private int magnetTicks = 0;
    private int airRiptideTicks = 0;
    private boolean hasElytraAdv = false;

    public PlayerEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "createPlayerAttributes", at = @At("RETURN"), cancellable = true)
    private static void injectBreakSpeed(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
        cir.setReturnValue(
                cir.getReturnValue()
                        .add(ModAttributes.MAGNETIC_POWER)
                        .add(ModAttributes.MAGNETIC_RADIUS)
                        .add(ModAttributes.EXPERIENCE_GAIN)
        );
    }

    @ModifyVariable(method = "addExperience", at = @At("HEAD"), argsOnly = true)
    private int arqueoUtils$scaleExperienceGained(int experience) {
        if (experience == 0) return 0;

        PlayerEntity self = (PlayerEntity) (Object) this;
        double multiplier = self.getAttributeValue(ModAttributes.EXPERIENCE_GAIN);

        if (multiplier == 1.0D) return experience;
        if (multiplier <= 0.0D) return 0;

        double scaled = (double) experience * multiplier;
        int roundedDown = MathHelper.floor(scaled);

        // If vanilla gave XP, never make it negative due to overflow/rounding quirks.
        if (experience > 0 && roundedDown < 0) return Integer.MAX_VALUE;

        double decimal = scaled - roundedDown;
        if (decimal != 0 && this.random.nextFloat() < decimal) return roundedDown + 1;
        else return roundedDown;
    }

    @Shadow
    public abstract PlayerInventory getInventory();

    @Shadow
    public abstract ActionResult interact(Entity entity, Hand hand);

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void dropInventory(ServerWorld world, CallbackInfo ci) {
        HashMap<ItemStack, Integer> soulboundStacks = new HashMap<>();
        for (int i = 0; i < 43; i++) {
            ItemStack stack = getInventory().getStack(i);
            if (isStackSoulbound(stack)) soulboundStacks.put(stack, i);
        }
        DEAD_PLAYERS.put(getUuid(), soulboundStacks);
    }

    @Inject(method = "getName", at = @At("HEAD"), cancellable = true)
    public void disguisedName(CallbackInfoReturnable<Text> cir) {
        MinecraftServer server = this.getEntityWorld().getServer();
        if (server == null) return;
        DisguiseState state = DisguiseState.get(server);

        if (state.isDisguised(this.getUuid())) {
            cir.setReturnValue(Text.literal("AJSDHWAGS")
                    .formatted(Formatting.GRAY)
                    .formatted(Formatting.OBFUSCATED));
        }
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void disguisedDisplayName(CallbackInfoReturnable<Text> cir) {
        MinecraftServer server = this.getEntityWorld().getServer();
        if (server == null) return;
        DisguiseState state = DisguiseState.get(server);

        if (state.isDisguised(this.getUuid())) {
            cir.setReturnValue(Text.literal("AJSDHWAGS")
                    .formatted(Formatting.GRAY)
                    .formatted(Formatting.OBFUSCATED));
        }
    }

    @Override
    public void setElytraAdvancement(boolean bl) {
        this.hasElytraAdv = bl;
    }

    @Override
    public boolean hasElytraAdvancement() {
        return hasElytraAdv;
    }

    @Override
    public int getMagnetTicks() {
        return magnetTicks;
    }

    @Override
    public void setMagnetTicks(int ticks) {
        magnetTicks = ticks;
    }

    @Override
    public void incrementMagnetTicks() {
        magnetTicks++;
    }

    @Override
    public int getAirRiptideTicks() {
        return airRiptideTicks;
    }

    @Override
    public void setAirRiptideTicks(int ticks) {
        airRiptideTicks = ticks;
    }

    @Override
    public void decreaseAirRiptideTicks() {
        airRiptideTicks--;
    }

    /* -------------------- NBT SAVE -------------------- */

    @Inject(method = "writeCustomData", at = @At("HEAD"))
    private void writeData(WriteView view, CallbackInfo ci) {
        view.putBoolean("arqueoutils:elytra_adv", this.hasElytraAdv);
    }

    /* -------------------- NBT LOAD -------------------- */

    @Inject(method = "readCustomData", at = @At("HEAD"))
    private void readData(ReadView view, CallbackInfo ci) {
        if (view.contains("arqueoutils:elytra_adv")) {
            this.hasElytraAdv = view.getBoolean("arqueoutils:elytra_adv", false);
        }
    }
}
