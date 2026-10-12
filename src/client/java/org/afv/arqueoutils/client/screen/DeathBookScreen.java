package org.afv.arqueoutils.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.afv.arqueoutils.network.DeathBookSubmitPayload;
import org.afv.arqueoutils.network.DeathBookValidRequest;

public class DeathBookScreen extends Screen {

    private TextFieldWidget nameField;
    private ButtonWidget confirmButton;
    private final String selfName;
    private boolean validName;

    public DeathBookScreen(String selfName) {
        super(Text.literal("Death Book"));
        this.selfName = selfName;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        nameField = new TextFieldWidget(
            textRenderer,
            centerX - 100,
            centerY - 10,
            200,
            20,
            Text.translatable("gui.arqueoutils.death_book.box")
        );

        nameField.setMaxLength(16);
        nameField.setEditable(true);
        nameField.setVisible(true);
        nameField.setChangedListener(this::onTextChanged);
        addSelectableChild(nameField);
        setInitialFocus(nameField);

        addDrawableChild(nameField);
        setInitialFocus(nameField);

        confirmButton = ButtonWidget.builder(Text.translatable("gui.arqueoutils.death_book.confirm"), b -> submit())
                .dimensions(centerX - 40, centerY + 20, 80, 20)
                .build();
        confirmButton.active = false;

        addDrawableChild(confirmButton);
    }

    private void onTextChanged(String text) {
        ClientPlayNetworking.send(new DeathBookValidRequest(this.selfName, text));
    }

    @Override
    public void tick() {
        super.tick();
        confirmButton.active = validName;
        nameField.setEditableColor(validName ? 0xFF55FF55 : 0xFFFF5555);
    }

    public void setValid(boolean valid) {
        this.validName = valid;
    }

    private void submit() {
        String target = nameField.getText().trim();

        if (target.isEmpty() || target.equalsIgnoreCase(selfName)) {
            error();
            return;
        }

        if (this.client.player == null) return;
        ClientPlayNetworking.send(new DeathBookSubmitPayload(this.client.player.getName().getString(), target));
        close();
    }

    private void error() {
        if (this.client.player != null) {
            this.client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS.value(), 1, 0.5f);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Title (top, centered)
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("gui.arqueoutils.death_book.title"),
                this.width / 2,
                this.height / 2 - 50,
                0xFFFFFF
        );

        // Prompt above text field
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.translatable("gui.arqueoutils.death_book.prompt"),
                this.width / 2,
                this.height / 2 - 25,
                0xA0A0A0
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}