package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.afv.arqueoutils.potion.ModEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    private static final Identifier VIGNETTE_TEX =
            Identifier.of("minecraft", "textures/misc/vignette.png");

    @ModifyArg(
            method = "drawHeart(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIZZZ)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud$HeartType;getTexture(ZZZ)Lnet/minecraft/util/Identifier;"
            ),
            index = 0
    )
    private boolean modifyHardcoreArg(boolean hardcore) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && player.hasStatusEffect(ModEffects.HEALTH_DISRUPTION)) {
                hardcore = true;
        }
        return hardcore; // otherwise vanilla
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderUhcVignette(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;

        if (!(player.hasStatusEffect(ModEffects.HEALTH_DISRUPTION))) return;

        int ticks = Objects.requireNonNull(player.getStatusEffect(ModEffects.HEALTH_DISRUPTION)).getDuration();
        if (ticks <= 0) return; // no vignette unless UHC is active

        renderUHCVignette(context, ticks);
    }

    @Unique
    public void renderUHCVignette(DrawContext context, int ticks) {
        int i;
        float f = MathHelper.clamp((float) ticks / 20, 0.0F, 1.0F);
        i = ColorHelper.fromFloats(1.0F, 0.0F, f, f);

        context.drawTexture(RenderPipelines.VIGNETTE, VIGNETTE_TEX, 0, 0, 0.0F, 0.0F, context.getScaledWindowWidth(), context.getScaledWindowHeight(), context.getScaledWindowWidth(), context.getScaledWindowHeight(), i);
    }
}