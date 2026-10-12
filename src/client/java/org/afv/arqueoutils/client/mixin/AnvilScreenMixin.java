package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin {

//    @WrapOperation(
//            method = "drawForeground",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"
//            )
//    )
//    private void moveCost(
//            DrawContext context,
//            TextRenderer renderer,
//            Text text,
//            int x,
//            int y,
//            int color,
//            Operation<Integer> original
//    ) {
//        original.call(context, renderer, text, x - 10, y, color);
//    }
//
//    @WrapOperation(
//            method = "drawForeground",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"
//            )
//    )
//    private void moveCost(
//            DrawContext instance, int x1, int y1, int x2, int y2, int color, Operation<Void> original
//    ) {
//        original.call(instance, x1 - 10, y1, x2 - 10, y2, color);
//    }
}