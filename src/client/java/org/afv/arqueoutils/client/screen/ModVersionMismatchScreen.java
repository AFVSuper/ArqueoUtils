package org.afv.arqueoutils.client.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.afv.arqueoutils.util.ModpackCheckConstants;

public class ModVersionMismatchScreen extends Screen {

    public ModVersionMismatchScreen() {
        super(Text.literal("Version Mismatch"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Close Game"),
                button -> this.client.scheduleStop())
                .dimensions(centerX - 105, centerY + 20, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Back to Menu"),
                button -> this.client.setScreen(new TitleScreen()))
                .dimensions(centerX + 5, centerY + 20, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0xFF101010);
        super.render(context, mouseX, mouseY, delta);

        context.drawCenteredTextWithShadow(
                this.textRenderer,
                Text.empty().append(Text.translatable(ModpackCheckConstants.VERSION_MISMATCH_KEY)),

                this.width / 2, this.height / 2 - 20, 0xFFFFFFFF
        );
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}