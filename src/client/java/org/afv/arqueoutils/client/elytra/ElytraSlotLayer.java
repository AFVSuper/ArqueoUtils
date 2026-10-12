package org.afv.arqueoutils.client.elytra;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.equipment.EquipmentModel;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import org.afv.arqueoutils.ArqueoUtils;
import org.afv.arqueoutils.client.utils.DisguiseClientState;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ElytraSlotLayer<S extends PlayerEntityRenderState, M extends EntityModel<S>> extends FeatureRenderer<S, M> {

    private final ElytraEntityModel elytraModel;
    private final ElytraEntityModel elytraBabyModel;
    private final EquipmentRenderer equipmentRenderer;

    public ElytraSlotLayer(FeatureRendererContext<S, M> context,
                           LoadedEntityModels models,
                           EquipmentRenderer equipmentRenderer) {
        super(context);
        this.elytraModel = new ElytraEntityModel(models.getModelPart(EntityModelLayers.ELYTRA));
        this.elytraBabyModel = new ElytraEntityModel(models.getModelPart(EntityModelLayers.ELYTRA_BABY));
        this.equipmentRenderer = equipmentRenderer;
    }

    @Override
    public void render(MatrixStack matrices,
                       OrderedRenderCommandQueue vertexConsumers,
                       int light,
                       S renderState,
                       float limbAngle,
                       float limbDistance) {

        if (!ArqueoUtils.ENABLED_COLYTRA) {

            return;
        }

        if (DisguiseClientState.isDisguised(renderState)) return;

        // Get the Elytra from the custom slot
        ItemStack elytraStack = ElytraRenderCache.get(renderState);

        // Only render if there is an Elytra in the slot
        if (elytraStack.isEmpty()) {
            return;
        }

        EquippableComponent equippable = elytraStack.get(DataComponentTypes.EQUIPPABLE);
        if (equippable == null) {
            return;
        }

        RegistryKey<EquipmentAsset> assetKey = equippable.assetId().orElseGet(() -> {
            EquippableComponent vanilla =
                    Items.ELYTRA.getDefaultStack().get(DataComponentTypes.EQUIPPABLE);
            return vanilla != null ? vanilla.assetId().orElse(null) : null;
        });

        if (assetKey == null) {
            ArqueoUtils.LOGGER.error("AssetKey is null");
            return; // hard stop, prevents crash
        }

        ElytraEntityModel modelToUse = renderState.baby ? elytraBabyModel : elytraModel;
        matrices.push();
        matrices.translate(0.0F, 0.0F, 0.125F);

        // Render the Elytra using vanilla equipment renderer
        this.equipmentRenderer.render(
                EquipmentModel.LayerType.WINGS,
                assetKey, // RegistryKey can be null for default
                modelToUse,
                renderState,
                elytraStack,
                matrices,
                vertexConsumers,
                light,
                getPlayerElytraTexture(renderState),
                renderState.outlineColor,
                0
        );

        matrices.pop();
    }

    @Nullable
    private static net.minecraft.util.Identifier getPlayerElytraTexture(PlayerEntityRenderState state) {
        if (state.skinTextures.elytra() != null) {
            return state.skinTextures.elytra().texturePath();
        }

        if (state.skinTextures.cape() != null && state.capeVisible) {
            return state.skinTextures.cape().texturePath();
        }

        return null;
    }
}