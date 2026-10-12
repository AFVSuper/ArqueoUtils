package org.afv.arqueoutils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import org.afv.arqueoutils.enchantment.ModEnchantmentEffects;
import org.afv.arqueoutils.init.*;
import org.afv.arqueoutils.items.CauldronClean;
import org.afv.arqueoutils.network.ModVersionAckPayload;
import org.afv.arqueoutils.network.ModVersionPayload;
import org.afv.arqueoutils.potion.BrewingRecipes;
import org.afv.arqueoutils.potion.ModEffects;
import org.afv.arqueoutils.potion.ModPotions;
import org.afv.arqueoutils.util.ModpackCheckConstants;
import org.afv.arqueoutils.util.ProfileUtils;
import org.afv.arqueoutils.util.ServerCapeStorage;
import org.afv.arqueoutils.world.PlaytimeState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class ArqueoUtils implements ModInitializer {

    public static final boolean ENABLED_COLYTRA = false;
    public static final String ModID = "arqueoutils";
    public static final Logger LOGGER = LoggerFactory.getLogger(ModID);

    public static final String EXPECTED_VERSION = "1.0.17";

    @Override
    public void onInitialize() {
        ModNetworking.init();
        ModComponents.init();
        ModSounds.init();
        ModEffects.init();
        ModEntities.init();
        ModItems.initialize();
        ModRecipes.initialize();
        ModScreenHandlers.init();
        ModBlocks.initialize();
        ModBlockEntities.init();
        ModItemGroups.init();
        ModAttributes.initialize();
        ModEnchantmentEffects.register();
        ModCriteria.init();
        ModEvents.register();
        PlaytimeState.registerEvent();

        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (ServerConfigurationNetworking.canSend(handler, ModVersionPayload.ID)) {
                ServerConfigurationNetworking.send(handler, new ModVersionPayload(EXPECTED_VERSION));
            } else {
                handler.disconnect(Text.literal("This server requires the modpack. Either you don't have it or it is outdated."));
            }
        });

        ServerConfigurationNetworking.registerGlobalReceiver(ModVersionAckPayload.ID, (payload, context) -> {
            if (!payload.matches()) {
                context.networkHandler().disconnect(
                        Text.translatable(ModpackCheckConstants.VERSION_MISMATCH_KEY)
                );
            }
        });

        CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            ModCommands.register(commandDispatcher, commandRegistryAccess);
        }));

        ProfileUtils.init();
        ModPotions.register();
        BrewingRecipes.init();
        CauldronClean.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            Path file = server.getSavePath(WorldSavePath.ROOT).resolve("arqueo_capes.json");
            ServerCapeStorage.setPath(file);
            ServerCapeStorage.load();
        });
    }
}
