package org.afv.arqueoutils.init;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.recipe.TrimRemovalRecipe;

public class ModRecipes {

    public static final RecipeSerializer<TrimRemovalRecipe> TRIM_REMOVAL_SERIALIZER =
            Registry.register(
                    Registries.RECIPE_SERIALIZER,
                    Identifier.of(ArqueoUtils.ModID, "trim_removal"),
                    new TrimRemovalRecipe.Serializer()
            );

    public static void initialize() {
    }
}