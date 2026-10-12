package org.afv.arqueoutils.client.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import org.afv.arqueoutils.client.render.ShaderManager;
import org.afv.arqueoutils.client.screen.ArqueoMainConfigScreen;
import org.afv.arqueoutils.client.screen.DeathBookScreen;
import org.afv.arqueoutils.client.screen.ModelCardScreen;
import org.afv.arqueoutils.client.utils.*;
import org.afv.arqueoutils.holder.ElytraHolder;
import org.afv.arqueoutils.network.*;
import org.afv.arqueoutils.util.CustomCape;
import org.afv.arqueoutils.util.ModInfo;
import org.afv.arqueoutils.util.ResourceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientNetwork {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(ElytraSyncPayload.ID,
                (payload, context) -> {
                    boolean value = payload.elytraAdv();

                    context.client().execute(() -> {
                        if (context.client().player instanceof ElytraHolder holder) {
                            holder.setElytraAdvancement(value);
                        }
                });
        });

        ClientPlayNetworking.registerGlobalReceiver(
                DeathBookOpenScreenPayload.ID,
                (payload, context) -> {
                    MinecraftClient client = context.client();
                    client.setScreen(new DeathBookScreen(context.player().getName().getString()));
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                ModelCardOpenScreenPayload.ID,
                (payload, context) -> context.client().execute(() ->
                        context.client().setScreen(new ModelCardScreen(payload.cardHand(), payload.models()))
                )
        );

        ClientPlayNetworking.registerGlobalReceiver(
                DeathBookValidResponse.ID,
                (payload, context) -> {
                    MinecraftClient client = context.client();
                    if (client.currentScreen instanceof DeathBookScreen deathBookScreen) {
                        deathBookScreen.setValid(payload.valid());
                    }
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                CapeSyncPayload.ID,
                (payload, context) -> {
                    UUID uuid = payload.playerUuid();
                    String capeId = payload.capeId();
                    CustomCape cape = CustomCape.getById(capeId);
                    if (cape != null) {
                        ClientCapeStorage.setCape(uuid, cape);
                    } else ClientCapeStorage.clearCape(uuid);
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(
                DisguiseSyncPayload.ID,
                (payload, context) -> context.client().execute(() -> {
                    DisguiseClientState.set(payload.playerUuid(), payload.disguised());
                    ClientPlayNetworkHandler handler = context.client().getNetworkHandler();
                    if (handler != null) {
                        TabListHelper.unlistDisguisedPlayers(handler);
                    }
                })
        );

        ClientPlayNetworking.registerGlobalReceiver(ClientModsRequest.ID, (payload, context) -> {
            // Build hierarchical mod info
            List<ModInfo> mods = new ArrayList<>();
            assert MinecraftClient.getInstance().player != null;

            // Map to temporarily collapse Fabric API children into one parent
            ModInfo fabricApiMod = null;

            for (ModContainer parent : FabricLoader.getInstance().getAllMods()) {
                String id = parent.getMetadata().getId();

                // Skip base runtime mods
                if (id.equals("minecraft") || id.equals("fabricloader") || id.equals("java"))
                    continue;

                if (parent.getContainingMod().isPresent()) continue;

                List<ModInfo> children = parent.getContainedMods().stream()
                        .map(child -> new ModInfo(
                                child.getMetadata().getName(),
                                child.getMetadata().getVersion().getFriendlyString(),
                                List.of()
                        ))
                        .toList();

                // Collapse Fabric API modules into one parent
                if (id.startsWith("fabric-")) {
                    if (fabricApiMod == null) {
                        fabricApiMod = new ModInfo(
                                "Fabric API",
                                parent.getMetadata().getVersion().getFriendlyString(),
                                children
                        );
                    } else {
                        // Add children from other Fabric API modules
                        List<ModInfo> combinedChildren = new ArrayList<>(fabricApiMod.children());
                        combinedChildren.addAll(children);
                        fabricApiMod = new ModInfo(fabricApiMod.name(), fabricApiMod.version(), combinedChildren);
                    }
                    continue; // Skip adding this parent individually
                }

                mods.add(new ModInfo(
                        parent.getMetadata().getName(),
                        parent.getMetadata().getVersion().getFriendlyString(),
                        children
                ));
            }

            // Add Fabric API parent if present
            if (fabricApiMod != null) {
                mods.add(fabricApiMod);
            }

            ClientPlayNetworking.send(new ClientModsResponse(MinecraftClient.getInstance().player.getUuid(), mods));
        });

        ClientPlayNetworking.registerGlobalReceiver(ResourcesRequest.ID, (payload, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            client.execute(() -> {
                if (client.player == null) return;

                var packManager = client.getResourcePackManager();

                // Enabled packs (ordered)
                var enabledProfiles = packManager.getEnabledProfiles();

                List<ResourceInfo> resources = new ArrayList<>();

                for (var profile : packManager.getProfiles()) {
                    boolean enabled = enabledProfiles.contains(profile);

                    resources.add(new ResourceInfo(
                            profile.getDisplayName().getString(),
                            enabled
                    ));
                }

                ClientPlayNetworking.send(
                        new ResourcesResponse(
                                client.player.getUuid(),
                                resources
                        )
                );
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(OpenArqueoMenuPayload.ID,
                (payload, context) ->
                        context.client().execute(() ->
                                context.client().setScreen(ArqueoMainConfigScreen.create(context.client().currentScreen))
                        )
        );

        ClientPlayNetworking.registerGlobalReceiver(GlobalBWPayload.ID,
                (payload, context) -> {
                    GlobalBWStateClient.setEnabled(payload.enabled());
                    // Apply shader
                    ShaderManager.setBWEnabled(payload.enabled());
                }
        );

        ClientPlayNetworking.registerGlobalReceiver(ArqueoWorldStateS2CPayload.ID, (payload, context) -> {
            NbtCompound data = payload.data();
            WorldStateClient.completedCores = data.getInt("completed_cores").orElse(0);
            WorldStateClient.endEnabled = data.getBoolean("end_enabled").orElse(false);
            WorldStateClient.fastXpPickup = data.getBoolean("fast_xp_pickup").orElse(false);
        });
    }
}
