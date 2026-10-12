package org.afv.arqueoutils.client.models;

import net.minecraft.client.data.Model;
import net.minecraft.client.data.TextureKey;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

import java.util.Optional;

public class ModModels {
    private static Model itemModel(Identifier parent, Optional<String> variant, TextureKey key) {
        return new Model(Optional.of(parent), variant, key);
    }
    public static final Model AMULET_MODEL = itemModel(
            Identifier.of(ArqueoUtils.ModID, "item/amulet_2"),
            Optional.empty(),
            TextureKey.LAYER0
    );
    public static final Model SCROLL_MODEL = itemModel(
            Identifier.of(ArqueoUtils.ModID, "item/scroll"),
            Optional.empty(),
            TextureKey.LAYER0
    );
}
