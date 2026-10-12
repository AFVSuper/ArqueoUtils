package org.afv.arqueoutils.client.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.client.elytra.ElytraRenderCache;
import org.afv.arqueoutils.client.utils.ClientCapeStorage;
import org.afv.arqueoutils.client.utils.DisguiseClientState;
import org.afv.arqueoutils.util.CustomCape;
import org.afv.arqueoutils.util.CustomCapeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin<T extends PlayerLikeEntity & ClientPlayerLikeEntity> {
    private static final AssetInfo.TextureAssetInfo DISGUISE_BODY =
            new AssetInfo.TextureAssetInfo(Identifier.of(ArqueoUtils.ModID, "entity/disguise/dark_cloak"));
    private static final Identifier DISGUISE_BODY_ID = Identifier.of(ArqueoUtils.ModID, "entity/disguise/dark_cloak");
    private static final Identifier DISGUISE_BODY_PNG = Identifier.of(ArqueoUtils.ModID, "textures/entity/disguise/dark_cloak.png");

    @Inject(
        method = "updateRenderState(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/render/entity/state/EntityRenderState;F)V",
        at = @At("TAIL")
    )
    private void arqueo$captureCustomElytra(
            Entity entity, EntityRenderState state, float tickProgress, CallbackInfo ci
    ) {
        if (!ArqueoUtils.ENABLED_COLYTRA) return;
        if (entity instanceof PlayerEntity holder) {
            ItemStack elytra = holder.getInventory().getStack(42);
            if (!elytra.isEmpty()) {
                ElytraRenderCache.put((BipedEntityRenderState) state, elytra);
            }
        }
    }

    @Inject(
            method = "updateRenderState*",
            at = @At("TAIL")
    )
    private void onUpdateRenderState(T player, PlayerEntityRenderState state, float tickDelta, CallbackInfo ci) {
        boolean disguised = DisguiseClientState.isDisguised(player.getUuid());
        DisguiseClientState.bind(state, player.getUuid());
        if (disguised) {
            SkinTextures.SkinOverride override = new SkinTextures.SkinOverride(
                    java.util.Optional.of(DISGUISE_BODY),
                    java.util.Optional.empty(),
                    java.util.Optional.empty(),
                    java.util.Optional.empty()
            );
            state.skinTextures = state.skinTextures.withOverride(override);
        }

        CustomCape cape = ClientCapeStorage.getCape(player.getUuid());
        if (!disguised && cape != null) {
            state.skinTextures = CustomCapeManager.overrideCape(state.skinTextures, cape);
        }
    }

    @Inject(method = "hasLabel(Lnet/minecraft/entity/PlayerLikeEntity;D)Z", at = @At("HEAD"), cancellable = true)
    private void arqueo$hideDisguisedNametag(T player, double squaredDistanceToCamera, CallbackInfoReturnable<Boolean> cir) {
        if (DisguiseClientState.isDisguised(player.getUuid())) {
            cir.setReturnValue(false);
        }
    }

    @ModifyVariable(method = "renderRightArm", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Identifier arqueo$overrideRightArmSkinTexture(Identifier original) {
        return shouldUseDisguiseFirstPersonArms() && hasDisguiseTexture() ? DISGUISE_BODY_PNG : original;
    }

    @ModifyVariable(method = "renderLeftArm", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Identifier arqueo$overrideLeftArmSkinTexture(Identifier original) {
        return shouldUseDisguiseFirstPersonArms() && hasDisguiseTexture() ? DISGUISE_BODY_PNG : original;
    }

    private boolean shouldUseDisguiseFirstPersonArms() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player != null && DisguiseClientState.isDisguised(client.player.getUuid());
    }

    private boolean hasDisguiseTexture() {
        ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
        return resourceManager.getResource(DISGUISE_BODY_PNG).isPresent();
    }
}