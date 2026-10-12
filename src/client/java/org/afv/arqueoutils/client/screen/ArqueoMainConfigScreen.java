package org.afv.arqueoutils.client.screen;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.afv.arqueoutils.client.utils.GlobalBWStateClient;
import org.afv.arqueoutils.client.utils.WorldStateClient;
import org.afv.arqueoutils.network.ArqueoWorldStateC2SPayload;
import org.afv.arqueoutils.network.GlobalBWPayload;

public class ArqueoMainConfigScreen {

    public static Screen create(Screen parent) {
        // Main builder
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("ArqueoUtils Admin"))
                .setTransparentBackground(true);

        // Main categories (tabs)
        ConfigCategory effectsCategory = builder.getOrCreateCategory(Text.literal("Effects"));
        ConfigCategory gameplayCategory = builder.getOrCreateCategory(Text.literal("Gameplay")); // example
        ConfigCategory experimentalCategory = builder.getOrCreateCategory(Text.literal("Experimental"));

        boolean initialBWValue = GlobalBWStateClient.getEnabled();
        // Add a boolean toggle in Effects tab
        effectsCategory.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Text.literal("Global B&W"), initialBWValue)
                        .setDefaultValue(false)
                        .setSaveConsumer(newValue -> {
                            // Send payload request to server
                            ClientPlayNetworking.send(new GlobalBWPayload(newValue));
                        })
                        .build()
        );

        gameplayCategory.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Text.literal("Enabled End"), WorldStateClient.endEnabled)
                        .setDefaultValue(false)
                        .setSaveConsumer(newValue -> {
                            ClientPlayNetworking.send(new ArqueoWorldStateC2SPayload("end_enabled", newValue));
                        })
                        .build()
        );

        experimentalCategory.addEntry(
                builder.entryBuilder()
                        .startBooleanToggle(Text.literal("Fast XP Pickup"), WorldStateClient.fastXpPickup)
                        .setDefaultValue(false)
                        .setSaveConsumer(newValue -> {
                            ClientPlayNetworking.send(new ArqueoWorldStateC2SPayload("fast_xp_pickup", newValue));
                        })
                        .build()
        );

        // You can add more entries in Effects
        // effectsCategory.addEntry(...);

        return builder.build();
    }
}