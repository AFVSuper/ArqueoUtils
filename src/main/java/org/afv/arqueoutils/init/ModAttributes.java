package org.afv.arqueoutils.init;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;

public class ModAttributes {
    private static RegistryEntry.Reference<EntityAttribute> register(
            String name,
            double defaultValue,
            double minValue,
            double maxValue,
            boolean syncedWithClient
    ) {
        Identifier id = Identifier.of(ArqueoUtils.ModID, name);

        EntityAttribute attribute = new ClampedEntityAttribute(
                id.toTranslationKey("attribute"),
                defaultValue,
                minValue,
                maxValue
        ).setTracked(syncedWithClient);

        return Registry.registerReference(Registries.ATTRIBUTE, id, attribute);
    }

    public static final RegistryEntry.Reference<EntityAttribute> MAGNETIC_POWER = register(
            "magnetic_power",
            0,
            0,
            1024,
            true
    );
    public static final RegistryEntry.Reference<EntityAttribute> MAGNETIC_RADIUS = register(
            "magnetic_radius",
            0,
            0,
            1024,
            true
    );
    public static final RegistryEntry.Reference<EntityAttribute> EXPERIENCE_GAIN = register(
            "experience_gain",
            1,
            0,
            1024,
            true
    );

    public static void initialize() {}
}