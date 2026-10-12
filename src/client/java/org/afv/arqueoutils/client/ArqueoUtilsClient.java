package org.afv.arqueoutils.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.client.elytra.ElytraRenderCache;
import org.afv.arqueoutils.client.elytra.ElytraSlotLayer;
import org.afv.arqueoutils.client.network.ClientNetwork;
import org.afv.arqueoutils.client.render.ShaderManager;
import org.afv.arqueoutils.client.render.entity.TombstoneRenderer;
import org.afv.arqueoutils.client.screen.OrePowderBagScreen;
import org.afv.arqueoutils.client.utils.DisguiseClientState;
import org.afv.arqueoutils.client.utils.GlobalBWStateClient;
import org.afv.arqueoutils.init.ModEntities;
import org.afv.arqueoutils.init.ModScreenHandlers;
import org.afv.arqueoutils.network.ModVersionAckPayload;
import org.afv.arqueoutils.network.ModVersionPayload;

public class ArqueoUtilsClient implements ClientModInitializer {

    public static final String CLIENT_VERSION = "1.0.17";

    @Override
    public void onInitializeClient() {
        ClientNetwork.init();

        HandledScreens.register(ModScreenHandlers.ORE_POWDER_BAG, OrePowderBagScreen::new);

        EntityRendererRegistry.register(ModEntities.TOMBSTONE, TombstoneRenderer::new);

        ClientConfigurationNetworking.registerGlobalReceiver(ModVersionPayload.ID, (payload, context) -> {
            boolean matches = CLIENT_VERSION.equals(payload.version());
            context.responseSender().sendPacket(new ModVersionAckPayload(matches));
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.gameRenderer != null && GlobalBWStateClient.getEnabled()) {
                ShaderManager.setBWEnabled(true);
            }
        });

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register(
                (entityType, entityRenderer, registrationHelper, context) -> {

                    if (!ArqueoUtils.ENABLED_COLYTRA) return;
                    if (entityRenderer instanceof PlayerEntityRenderer playerRenderer) {

                        registrationHelper.register(
                                new ElytraSlotLayer<>(
                                        playerRenderer,                  // FeatureRendererContext
                                        context.getEntityModels(),         // LoadedEntityModels
                                        context.getEquipmentRenderer()    // EquipmentRenderer
                                )
                        );
                    }
                }
        );

        LivingEntityFeatureRenderEvents.ALLOW_CAPE_RENDER.register(renderState -> {
            if (DisguiseClientState.isDisguised(renderState)) {
                return false;
            }
            ItemStack elytra = ElytraRenderCache.get(renderState);
            // If there’s an Elytra in the custom slot, prevent cape rendering
            return elytra.isEmpty() || elytra.getItem() != Items.ELYTRA;
        });
    }
}
