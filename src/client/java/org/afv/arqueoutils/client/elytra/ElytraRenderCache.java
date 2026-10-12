package org.afv.arqueoutils.client.elytra;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.WeakHashMap;

@Environment(EnvType.CLIENT)
public final class ElytraRenderCache {

    private static final Map<BipedEntityRenderState, ItemStack> ELYTRA = new WeakHashMap<>();

    private ElytraRenderCache() {}

    public static void put(BipedEntityRenderState state, ItemStack stack) {
        if (!stack.isEmpty()) {
            ELYTRA.put(state, stack);
        }
    }

    public static ItemStack get(BipedEntityRenderState state) {
        return ELYTRA.getOrDefault(state, ItemStack.EMPTY);
    }
}