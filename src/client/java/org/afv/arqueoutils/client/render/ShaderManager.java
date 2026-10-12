package org.afv.arqueoutils.client.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.client.mixin.GameRendererAccessor;

public class ShaderManager {

    private static boolean bwEnabled = false;
    private static final Identifier BW_ID =
            Identifier.of("arqueoutils", "blackwhite");
            //Identifier.ofVanilla("creeper");

    public static void setBWEnabled(boolean enabled) {
        bwEnabled = enabled;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        GameRenderer renderer = client.gameRenderer;
        GameRendererAccessor accessor = (GameRendererAccessor) renderer;

        if (enabled) {
            accessor.setPostProcessorId(BW_ID);
            accessor.setPostProcessorEnabled(true);
        } else {
            accessor.setPostProcessorEnabled(false);
            accessor.setPostProcessorId(null);
        }
    }

    public static boolean isBWEnabled() {
        return bwEnabled;
    }
}