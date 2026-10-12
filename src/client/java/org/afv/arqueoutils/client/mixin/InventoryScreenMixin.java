package org.afv.arqueoutils.client.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.ArqueoUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends HandledScreen<PlayerScreenHandler> {

    @Unique
    private static final Identifier INVENTORY_TEXTURE = Identifier.of("minecraft", "textures/gui/container/inventory.png");

    public InventoryScreenMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    private void renderExtraSlot(DrawContext context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.client == null || this.client.player == null || this.client.interactionManager == null) {
            return;
        }

        // Only render for SURVIVAL inventory
//        if (!Objects.requireNonNull(this.client.player.getGameMode()).isSurvivalLike()) {
//            return;
//        }
        // These are GUI coordinates relative to the top-left of the inventory window

        // Draw vanilla slot outline (region u=7,v=83,width=18,height=18 in inventory.png)
        renderSlot(context, Identifier.of(ArqueoUtils.ModID, "textures/slots/magnet_empty.png"), this.x + 77, this.y + 44, 41);

        if (client.getNetworkHandler() != null) {
            if (!ArqueoUtils.ENABLED_COLYTRA) return;
            ClientAdvancementManager manager = client.getNetworkHandler().getAdvancementHandler();

            Identifier id = Identifier.of("minecraft", "end/elytra");

            AdvancementEntry entry = manager.get(id);

            if (entry != null) {
                AdvancementProgress progress =
                        ((ClientAdvancementManagerAccessor) manager)
                                .arqueoutils$getProgresses()
                                .get(entry);

                boolean done = progress != null && progress.isDone();
                if (done) renderSlot(context, Identifier.of(ArqueoUtils.ModID, "textures/slots/elytra_empty.png"), this.x + 77, this.y + 26, 42);
            }
        }
    }

    @Unique
    private void renderSlot(DrawContext context, Identifier emptyTexture, int x, int y, int slot) {
        RenderPipeline pipeline = RenderPipelines.GUI_TEXTURED;
        context.drawTexture(
                pipeline,
                INVENTORY_TEXTURE,  // your texture identifier
                x - 1,          // x
                y - 1,          // y
                7,                  // u (texture X)
                83,                 // v (texture Y)
                18,                 // width
                18,                 // height
                256,                // textureWidth (full atlas width)
                256                // textureHeight (full atlas height)// color (white = no tint)
        );
        if (this.client != null) {
            if (this.client.player != null && this.client.player.getInventory().getStack(slot).isEmpty()) {
                context.drawTexture(
                        pipeline,
                        emptyTexture,  // your texture identifier
                        x,          // x
                        y,          // y
                        0,                  // u (texture X)
                        0,                 // v (texture Y)
                        16,                 // width
                        16,                 // height
                        16,                // textureWidth (full atlas width)
                        16                // textureHeight (full atlas height)// color (white = no tint)
                );
            }
        }
    }

}