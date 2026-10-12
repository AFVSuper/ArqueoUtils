package org.afv.arqueoutils.items;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SwingAnimationComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.SwingAnimationType;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.afv.arqueoutils.init.ModItems;
import org.afv.arqueoutils.network.ModelCardOpenScreenPayload;

import java.util.*;

public class ModelCard extends Item {
    //<editor-fold desc="Model Map">
    private static final Map<String, Map<String, Set<Item>>> CARD_MODELS = Map.of(
            "ALL", Map.of(
                    "golden_key", Set.of(Items.FLINT, Items.TRIAL_KEY, Items.OMINOUS_TRIAL_KEY),
                    "tome_of_undying", Set.of(Items.TOTEM_OF_UNDYING)
            ),
            "AFVSuper", Map.of(
                    "hornet_mask", Set.of(Items.CARVED_PUMPKIN),
                    "needle", Set.of(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD),
                    "vampire/sword", Set.of(Items.DIAMOND_SWORD, Items.NETHERITE_SWORD),
                    "vampire/dark_spear", Set.of(Items.NETHERITE_SPEAR),
                    "vampire/bow", Set.of(Items.BOW),
                    "vampire/axe", Set.of(Items.DIAMOND_AXE, Items.NETHERITE_AXE),
                    "gems/perfect_diamond", Set.of(Items.FLINT),
                    "gems/peridot", Set.of(Items.FLINT),
                    "gems/ruby", Set.of(Items.FLINT)
            ),
            "nikstort1208", Map.of(
                    "", Set.of()
            ),
            "TheSubZeGG", Map.of(
                    "ice_sword", Set.of(Items.NETHERITE_SWORD)
            )
    );
    //</editor-fold>

    //<editor-fold desc="Model Map">
    private static final Map<String, Text> MODEL_NAMES = Map.of(
            "all/golden_key", Text.literal("Golden Key")
                    .setStyle(Style.EMPTY.withItalic(false)),
            "all/tome_of_undying", Text.literal("Tome of Undying")
                    .setStyle(Style.EMPTY.withItalic(false)),
            "afvsuper/hornet_mask", Text.literal("Hornet's Mask")
                    .setStyle(Style.EMPTY.withItalic(false)),
            "afvsuper/needle", Text.literal("Hornet's Needle")
                    .setStyle(Style.EMPTY.withItalic(false)),
            "afvsuper/gems/perfect_diamond", Text.literal("Pristine Diamond")
                    .setStyle(Style.EMPTY.withItalic(false)),
            "afvsuper/gems/ruby", Text.literal("Pristine Ruby")
                    .setStyle(Style.EMPTY.withItalic(false)),
            "afvsuper/gems/peridot", Text.literal("Pristine Peridot")
                    .setStyle(Style.EMPTY.withItalic(false)),
            "afvsuper/vampire/dark_spear", Text.literal("Dark Spear")
                    .setStyle(Style.EMPTY.withItalic(false)),
            "thesubzegg/ice_sword", Text.literal("Ice Sword")
                    .setStyle(Style.EMPTY.withItalic(false))
    );
    //</editor-fold>

    public static Text getItemCustomName(String model) {
        return MODEL_NAMES.get(model.replace("arqueoutils:modelcard/", ""));
    }

    public static List<String> getValidModels(PlayerEntity player, ItemStack stack) {
        var mapAll = CARD_MODELS.get("ALL");
        List<String> list = new ArrayList<>(mapAll.keySet()
                .stream()
                .filter(k -> mapAll.get(k).contains(stack.getItem()))
                .map(s -> "arqueoutils:modelcard/all/" + s)
                .toList());

        String name = player.getStringifiedName();
        if (!CARD_MODELS.containsKey(name)) return list;

        var mapName = CARD_MODELS.get(name);
        list.addAll(mapName.keySet()
                .stream()
                .filter(k -> mapName.get(k).contains(stack.getItem()))
                .map(s -> "arqueoutils:modelcard/" + name.toLowerCase() + "/" + s)
                .toList());

        if (name.equals("AFVSuper") && player.getGameMode() == GameMode.CREATIVE) {
            for (String key : CARD_MODELS.keySet()) {
                if (key.equals("ALL") || key.equals("AFVSuper")) continue;
                var map = CARD_MODELS.get(key);
                list.addAll(map.keySet()
                        .stream()
                        .filter(k -> map.get(k).contains(stack.getItem()))
                        .map(s -> "arqueoutils:modelcard/" + key.toLowerCase() + "/" + s)
                        .toList());
            }
        }

        return list;
    }

    public static ItemStack createPreview(ItemStack original, String model) {
        ItemStack preview = original.copy();

        preview.set(
                DataComponentTypes.ITEM_MODEL,
                Identifier.of(model)
        );

        Text name = getItemCustomName(model);
        if (name != null) preview.set(DataComponentTypes.CUSTOM_NAME, name);
        else if (preview.contains(DataComponentTypes.ITEM_MODEL)
                && !Objects.requireNonNull(original.get(DataComponentTypes.ITEM_MODEL)).getNamespace().equalsIgnoreCase("minecraft")) preview.remove(DataComponentTypes.CUSTOM_NAME);

        return preview;
    }

    public ModelCard(Settings settings) {
        super(settings.component(DataComponentTypes.SWING_ANIMATION, new SwingAnimationComponent(SwingAnimationType.NONE, 0)));
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) {
            return ActionResult.SUCCESS;
        }

        Hand targetHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack target = player.getStackInHand(targetHand);
        if (target.isEmpty()) {
            player.sendMessage(Text.translatable("message.arqueoutils.model_card.no_item"), true);
            return ActionResult.FAIL;
        }

        List<String> models = getValidModels(player, target);
        if (models.isEmpty()) {
            player.sendMessage(Text.translatable("message.arqueoutils.model_card.no_models"), true);
            return ActionResult.FAIL;
        }

        if (player instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new ModelCardOpenScreenPayload(hand, models));
        }

        return ActionResult.SUCCESS;
    }

    public static boolean applyModel(PlayerEntity player, Hand cardHand, String model) {
        if (!player.getStackInHand(cardHand).isOf(ModItems.MODEL_CARD)) {
            return false;
        }

        Hand targetHand = cardHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack target = player.getStackInHand(targetHand);
        if (target.isEmpty()) {
            return false;
        }

        if (model.isEmpty()) {
            if (target.contains(DataComponentTypes.ITEM_MODEL)
                    && !Objects.requireNonNull(target.get(DataComponentTypes.ITEM_MODEL)).getNamespace().equalsIgnoreCase("minecraft")) target.remove(DataComponentTypes.CUSTOM_NAME);
            target.remove(DataComponentTypes.ITEM_MODEL);
        } else {
            Text name = getItemCustomName(model);
            if (name != null) {
                target.set(DataComponentTypes.CUSTOM_NAME, name);
            } else if (target.contains(DataComponentTypes.ITEM_MODEL)
                    && !Objects.requireNonNull(target.get(DataComponentTypes.ITEM_MODEL)).getNamespace().equalsIgnoreCase("minecraft")) target.remove(DataComponentTypes.CUSTOM_NAME);
            List<String> valid = getValidModels(player, target);
            if (!valid.contains(model) && !model.startsWith("minecraft:")) {
                return false;
            }
            target.set(DataComponentTypes.ITEM_MODEL, Identifier.of(model));
        }

        player.setStackInHand(targetHand, target);
        return true;
    }
}
