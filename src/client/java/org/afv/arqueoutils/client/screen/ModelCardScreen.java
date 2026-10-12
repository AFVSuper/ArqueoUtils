package org.afv.arqueoutils.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.afv.arqueoutils.items.ModelCard;
import org.afv.arqueoutils.network.ModelCardSelectPayload;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModelCardScreen extends Screen {
    private static final int SLOT_SIZE = 16;
    private static final int SPACING = 10;

    private final Hand cardHand;
    private final List<String> models;
    private final List<ItemStack> previews = new ArrayList<>();

    private int originX;
    private int originY;

    private String vanillaModelPath;

    public ModelCardScreen(Hand cardHand, List<String> models) {
        super(Text.translatable("gui.arqueoutils.model_card.title"));
        this.cardHand = cardHand;
        this.models = models;
    }

    @Override
    protected void init() {
        if (this.client == null || this.client.player == null) {
            close();
            return;
        }

        Hand targetHand = cardHand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ItemStack target = this.client.player.getStackInHand(targetHand);
        if (target.isEmpty()) {
            close();
            return;
        }

        previews.clear();
        ItemStack vanilla = target.copy();
        String path = Registries.ITEM.getId(target.getItem()).getPath();
        if (vanilla.contains(DataComponentTypes.ITEM_MODEL) &&
                !Objects.requireNonNull(vanilla.get(DataComponentTypes.ITEM_MODEL)).getNamespace().equalsIgnoreCase("minecraft")) {
            vanilla.remove(DataComponentTypes.CUSTOM_NAME);
        }
        vanilla.set(DataComponentTypes.ITEM_MODEL, Identifier.ofVanilla(path));
        previews.add(vanilla);
        for (String model : models) {
            previews.add(ModelCard.createPreview(target, model));
        }
        vanillaModelPath = "minecraft:" + path;

        int columns = Math.min(9, previews.size());
        int rows = (previews.size() + columns - 1) / columns;
        originX = (this.width) / 2 - (columns-1)*SPACING/2 - (columns)*SLOT_SIZE/2;
        originY = (this.height) / 2 - (rows-1)*SPACING/2 - (rows)*SLOT_SIZE/2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

//        context.drawTexture(RenderPipelines.GUI_TEXTURED, GENERIC_54, originX, originY, 0, 0, panelWidth, panelHeight, TEX_SIZE, TEX_SIZE);
//        context.drawText(this.textRenderer, this.title, originX + 8, originY + 6, 0x404040, false);

        int columns = Math.min(9, previews.size());
        for (int i = 0; i < previews.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            int slotX, slotY;
            slotX = originX + col * (SLOT_SIZE + SPACING);
            slotY = originY + row * (SLOT_SIZE + SPACING);

            context.fill(slotX - 1, slotY - 1, slotX + SLOT_SIZE + 1, slotY + SLOT_SIZE + 1, 0x77000000);
//            context.fill(slotX - 1, slotY - 1, slotX + 1, slotY + 1, 0xFFFFFFFF);
            context.fill(slotX - 2, slotY - 2, slotX - 1, slotY + SLOT_SIZE + 2, 0xFFFFFFFF);
            context.fill(slotX - 2, slotY - 2, slotX  + SLOT_SIZE + 2, slotY - 1, 0xFFFFFFFF);
            context.fill(slotX + SLOT_SIZE + 2, slotY - 2, slotX + SLOT_SIZE + 1, slotY + SLOT_SIZE + 2, 0xFFFFFFFF);
            context.fill(slotX - 2, slotY + SLOT_SIZE + 2, slotX + SLOT_SIZE + 1, slotY + SLOT_SIZE + 1, 0xFFFFFFFF);
//            context.fill(originX - 1, originY - 1, originX, originY, 0xFFFF0000);
            context.drawItem(previews.get(i), slotX, slotY);
        }

        int hovered = getHoveredIndex(mouseX, mouseY);
        if (hovered >= 0) {
            context.drawItemTooltip(this.textRenderer, previews.get(hovered), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click.button() == 0) {
            int index = getHoveredIndex((int) click.x(), (int) click.y());
            if (index >= 0) {
                String model = index == 0 ? vanillaModelPath : models.get(index - 1);
                ClientPlayNetworking.send(new ModelCardSelectPayload(cardHand, model));
                if (this.client != null) {
                    this.client.player.playSound(net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value(), 1.0F, 1.0F);
                }
                close();
                return true;
            }
        }
        return super.mouseClicked(click, doubled);
    }

    private int getHoveredIndex(int mouseX, int mouseY) {
        if (previews.isEmpty()) return -1;

        int columns = Math.min(9, previews.size());
        for (int i = 0; i < previews.size(); i++) {
            int row = i / columns;
            int col = i % columns;
            int slotX = originX + col * (SLOT_SIZE + SPACING);
            int slotY = originY + row * (SLOT_SIZE + SPACING);
            if (mouseX >= slotX - 2 && mouseX < slotX + SLOT_SIZE + 2 && mouseY >= slotY - 2 && mouseY < slotY + SLOT_SIZE + 2) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.client != null && this.client.options.inventoryKey.matchesKey(input)) {
            this.close();
            return true;
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
