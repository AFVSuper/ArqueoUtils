package org.afv.arqueoutils.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.object.PlayerTextObjectContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;


@Mixin(MessageHandler.class)
public abstract class MessageHandlerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    // either called from handleDisguisedChatMessage directly, or after some potential chat delay
    @ModifyArg(
            method = "method_45745", // lambda inside handleDisguisedChatMessage
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;)V",
                    ordinal = 0
            )
    )
    public Text chatheads$handleAddedDisguisedMessage(Text message) {
        // ArqueoUtils.LOGGER.info("CHAT MSG");
        String original = message.getString();

        broadcastTest(message);
        broadcastTest(Text.literal(original));

        Text prefix = Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic("AFVSuper"), true));

        MutableText newMessage = Text.empty()
                .append(prefix)
                .append(" <")
                .append("AFVSuper")
                .append("> " + original);
        return message;
    }

    @Unique
    private void broadcastTest(Text msg) {
        Text send = Text.empty()
                .append("TEST: ")
                .append(msg);
        client.player.sendMessage(send, false);
    }
}