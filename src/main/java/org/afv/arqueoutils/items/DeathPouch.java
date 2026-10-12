package org.afv.arqueoutils.items;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

public class DeathPouch extends Item {
    public DeathPouch(Settings settings) {
        super(settings);
    }

    private List<ItemStack> getContents(ItemStack pouch) {
        ContainerComponent component = pouch.get(DataComponentTypes.CONTAINER);
        if (component == null) return List.of();
        return component.stream().toList();
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack pouch = user.getStackInHand(hand);

        world.playSound(user, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ITEM_BUNDLE_DROP_CONTENTS,
                SoundCategory.MASTER,
                1, 1f
        );
        for (ItemStack item : getContents(pouch)) {
            user.dropItem(item, true);
        }

        pouch.decrement(1);

        return ActionResult.SUCCESS;
    }
}
