package org.afv.arqueoutils.client.comps;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.afv.arqueoutils.items.MagnetItem;

public class MagnetTooltipComponent implements TooltipComponent {

    private final MagnetItem.MagnetTooltipData data;

    public MagnetTooltipComponent(MagnetItem.MagnetTooltipData data) {
        this.data = data;
    }

    @Override
    public int getHeight(TextRenderer textRenderer) {
        return 60;
    }

    @Override
    public int getWidth(TextRenderer textRenderer) {
        return Math.max(
                textRenderer.getWidth("Magnet"),
                textRenderer.getWidth("Power: " + data.power())
        );
    }

    @Override
    public void drawText(
            DrawContext context,
            TextRenderer textRenderer,
            int x,
            int y
    ) {
        context.drawText(
                textRenderer,
                Text.literal(data.enabled() ? "Magnet: Enabled" : "Magnet: Disabled")
                        .formatted(data.enabled() ? Formatting.GREEN : Formatting.RED),
                x,
                y,
                0xFFFFFF,
                false
        );

        context.drawText(
                textRenderer,
                Text.literal("Power: " + data.power()).formatted(Formatting.DARK_GREEN),
                x,
                y + 10,
                0xFFFFFF,
                false
        );

        context.drawText(
                textRenderer,
                Text.literal("Radius: " + data.radius()).formatted(Formatting.DARK_GREEN),
                x,
                y + 20,
                0xFFFFFF,
                false
        );
    }
}