package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogColorMixin {

    // Soul Sand Valley vanilla fog color: 0x1B4745
    private static final float SSV_R = 0x1B / 255f;
    private static final float SSV_G = 0x47 / 255f;
    private static final float SSV_B = 0x45 / 255f;

    private static final double RADIUS = 75.0D;
    private static final double MIN_Y = 110.0D;

    // How many blocks the transition takes, tune to taste
    private static final double RADIUS_FADE = 16.0D;
    private static final double Y_FADE = 16.0D;

    @Inject(
            method = "getFogColor",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void foglayer$overrideFogColor(
            Camera camera, float tickProgress, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfoReturnable<Vector4f> cir) {

        if (world == null || world.getRegistryKey() != World.NETHER) return;

        Vec3d pos = camera.getCameraPos();
        double dist = Math.sqrt(pos.x * pos.x + pos.z * pos.z);

        // 1 well inside the cylinder, 0 well outside/below, smooth in between
        double horizontal = 1.0 - smoothstep(RADIUS - RADIUS_FADE, RADIUS, dist);
        double vertical = smoothstep(MIN_Y - Y_FADE, MIN_Y, pos.y);

        double blend = horizontal * vertical;
        if (blend <= 0.0) return; // fully outside, leave vanilla color untouched

        Vector4f original = cir.getReturnValue();
        float t = (float) MathHelper.clamp(blend, 0.0, 1.0);

        float r = MathHelper.lerp(t, original.x(), SSV_R);
        float g = MathHelper.lerp(t, original.y(), SSV_G);
        float b = MathHelper.lerp(t, original.z(), SSV_B);

        cir.setReturnValue(new Vector4f(r, g, b, original.w()));
    }

    // Standard GLSL-style smoothstep: 0 below edge0, 1 above edge1, cubic ease between
    private static double smoothstep(double edge0, double edge1, double x) {
        double t = MathHelper.clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0);
        return t * t * (3.0 - 2.0 * t);
    }
}