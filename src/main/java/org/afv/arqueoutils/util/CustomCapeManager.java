package org.afv.arqueoutils.util;

import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

import java.util.List;
import java.util.Optional;

public class CustomCapeManager {
    public static final List<String> CUSTOM_CAPE = List.of("AFVSuper");

    public static final Identifier MOD_CAPE_TEXTURE = Identifier.of(ArqueoUtils.ModID, "entity/cape/test_cape");

    public static SkinTextures overrideCape(SkinTextures original, CustomCape cape) {
        AssetInfo.TextureAssetInfo customCape = new AssetInfo.TextureAssetInfo(Identifier.of(ArqueoUtils.ModID, cape.getPath()));

        // Build a SkinOverride with the custom cape
        SkinTextures.SkinOverride override = new SkinTextures.SkinOverride(
                Optional.empty(),                  // body unchanged
                Optional.of(customCape),           // replace cape
                Optional.empty(),                  // elytra unchanged
                Optional.empty()                   // model unchanged
        );

        // Return a new SkinTextures instance with the override applied
        return original.withOverride(override);
    }

    public static boolean hasCustomCape(String playerName) {
        return CUSTOM_CAPE.contains(playerName);
    }
}