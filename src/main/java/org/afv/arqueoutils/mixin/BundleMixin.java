package org.afv.arqueoutils.mixin;

import net.minecraft.component.type.BundleContentsComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleContentsComponent.class)
public class BundleMixin {
    @Inject(method = "getNumberOfStacksShown", at = @At("HEAD"), cancellable = true)
    public void numgetNumberOfItemsToShowVis(CallbackInfoReturnable<Integer> cir) {
        BundleContentsComponent component = (BundleContentsComponent) (Object) this;
        cir.setReturnValue(component.size());
        cir.cancel();
    }
}
