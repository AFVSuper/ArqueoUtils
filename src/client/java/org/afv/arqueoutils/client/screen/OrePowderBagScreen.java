package org.afv.arqueoutils.client.screen;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.init.ModComponents;
import org.afv.arqueoutils.items.OrePowderBagData;
import org.afv.arqueoutils.items.OrePowderTypes;
import org.afv.arqueoutils.screen.OrePowderBagScreenHandler;

public class OrePowderBagScreen extends HandledScreen<OrePowderBagScreenHandler> {

    private static final Identifier GENERIC_54 = Identifier.ofVanilla("textures/gui/container/generic_54.png");
    private static final int TEX_SIZE = 256;
    /** First chest slot in generic_54.png */
    private static final int SLOT_U = 7;
    private static final int SLOT_V = 17;

    private static final int SLOT_INNER = 18;
    private static final int MIN_BG_WIDTH = 176;

    public OrePowderBagScreen(OrePowderBagScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        int countsRight = OrePowderBagScreenHandler.bagCountTextX(OrePowderBagScreenHandler.GRID_COLS - 1)
                + OrePowderBagScreenHandler.BAG_NUMBER_FIELD;
        this.backgroundWidth = MIN_BG_WIDTH;
        this.backgroundHeight = OrePowderBagScreenHandler.PLAYER_INV_START_Y + 58 + 18 + 6;
        this.playerInventoryTitleY = OrePowderBagScreenHandler.PLAYER_INV_START_Y - 10;
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }

    private static void drawSlotSprite(DrawContext context, RenderPipeline pipeline, int guiX, int guiY) {
        context.drawTexture(
                pipeline,
                GENERIC_54,
                guiX - 1,
                guiY - 1,
                SLOT_U,
                SLOT_V,
                SLOT_INNER,
                SLOT_INNER,
                TEX_SIZE,
                TEX_SIZE
        );
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int px = this.x;
        int py = this.y;
        RenderPipeline pipeline = RenderPipelines.GUI_TEXTURED;

        context.drawTexture(pipeline, GENERIC_54, px, py, 0, 0, 176, backgroundHeight - 7, TEX_SIZE, TEX_SIZE);
        context.drawTexture(pipeline, GENERIC_54, px, py + 7, 0, 222 + 7 - backgroundHeight, 176, backgroundHeight - 7, TEX_SIZE, TEX_SIZE);
        context.fill(px + 5, py + 5, px + backgroundWidth - 5, py + backgroundHeight - 5, 0xFFc6c6c6);
//        context.fill(px + 1, py + 1, px + backgroundWidth - 1, py + backgroundHeight - 1, 0xFF8da1d6);

        for (int r = 0; r < OrePowderBagScreenHandler.GRID_ROWS; r++) {
            for (int c = 0; c < OrePowderBagScreenHandler.GRID_COLS; c++) {
                int sx = px + OrePowderBagScreenHandler.bagSlotX(c);
                int sy = py + OrePowderBagScreenHandler.SLOT_START_Y + r * 18;
                drawSlotSprite(context, pipeline, sx, sy);
            }
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                int sx = px + OrePowderBagScreenHandler.SLOT_START_X + c * 18;
                int sy = py + OrePowderBagScreenHandler.PLAYER_INV_START_Y + r * 18;
                drawSlotSprite(context, pipeline, sx, sy);
            }
        }
        for (int c = 0; c < 9; c++) {
            int sx = px + OrePowderBagScreenHandler.SLOT_START_X + c * 18;
            int sy = py + OrePowderBagScreenHandler.PLAYER_INV_START_Y + 58;
            drawSlotSprite(context, pipeline, sx, sy);
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 0x404040, false);

        ItemStack bag = handler.findBagStack();
        OrePowderBagData data = bag.getOrDefault(ModComponents.ORE_POWDER_BAG, OrePowderBagData.EMPTY);
        for (int r = 0; r < OrePowderBagScreenHandler.GRID_ROWS; r++) {
            int rowY = OrePowderBagScreenHandler.SLOT_START_Y + r * 18;
            for (int c = 0; c < OrePowderBagScreenHandler.GRID_COLS; c++) {
                int idx = r * OrePowderBagScreenHandler.GRID_COLS + c;
                String line = Long.toString(data.get(idx));
                int colX = OrePowderBagScreenHandler.bagCountTextX(c);
                context.drawTextWithShadow(this.textRenderer, line, colX, rowY + 5,
                        OrePowderTypes.getColor(OrePowderTypes.ORDER.get(idx))
                );
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
