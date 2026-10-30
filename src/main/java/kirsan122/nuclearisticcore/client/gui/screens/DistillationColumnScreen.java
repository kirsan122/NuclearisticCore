package kirsan122.nuclearisticcore.client.gui.screens;

import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.common.inventory.DistillationColumnMenu;
import kirsan122.nuclearisticcore.common.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class DistillationColumnScreen extends AbstractContainerScreen<DistillationColumnMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(NuclearisticCore.MOD_ID, "textures/gui/distillation_column_gui.png");

    public DistillationColumnScreen(DistillationColumnMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = leftPos;
        int y = topPos;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if (menu.isCrafting()) {
            graphics.blit(TEXTURE, x + 46, y + 38, 176, 0, menu.getScaledProgress(), 16);
        }

        RenderUtils.drawEnergyLine(graphics, this, screen -> menu.getScaledEnergy(), 164, 18);

        RenderUtils.drawFluid(graphics, this, screen -> menu.getBlockEntity().getTankCapability(0), 0, x + 26, y + 25, 16, 37);
        RenderUtils.drawFluid(graphics, this, screen -> menu.getBlockEntity().getTankCapability(1), 1, x + 76, y + 25, 16, 37);
        RenderUtils.drawFluid(graphics, this, screen -> menu.getBlockEntity().getTankCapability(2), 2, x + 98, y + 25, 16, 37);
        RenderUtils.drawFluid(graphics, this, screen -> menu.getBlockEntity().getTankCapability(3), 3, x + 120, y + 25, 16, 37);
        RenderUtils.drawFluid(graphics, this, screen -> menu.getBlockEntity().getTankCapability(4), 4, x + 142, y + 25, 16, 37);

        graphics.blit(GuiData.DISTILLATION_DIVISIONS, x + 26, y + 25, 0, 0, 16, 37, 16, 37);
        graphics.blit(GuiData.DISTILLATION_DIVISIONS, x + 76, y + 25, 0, 0, 16, 37, 16, 37);
        graphics.blit(GuiData.DISTILLATION_DIVISIONS, x + 98, y + 25, 0, 0, 16, 37, 16, 37);
        graphics.blit(GuiData.DISTILLATION_DIVISIONS, x + 120, y + 25, 0, 0, 16, 37, 16, 37);
        graphics.blit(GuiData.DISTILLATION_DIVISIONS, x + 142, y + 25, 0, 0, 16, 37, 16, 37);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        int x = leftPos;
        int y = topPos;

        RenderUtils.drawFluidTooltip(graphics, font, this, screen -> menu.getBlockEntity().getTankCapability(0), mouseX, mouseY, new GuiData.TankParameters(25, 24, 42, 62));
        RenderUtils.drawFluidTooltip(graphics, font, this, screen -> menu.getBlockEntity().getTankCapability(1), mouseX, mouseY, new GuiData.TankParameters(75, 24, 92, 62));
        RenderUtils.drawFluidTooltip(graphics, font, this, screen -> menu.getBlockEntity().getTankCapability(2), mouseX, mouseY, new GuiData.TankParameters(97, 24, 114, 62));
        RenderUtils.drawFluidTooltip(graphics, font, this, screen -> menu.getBlockEntity().getTankCapability(3), mouseX, mouseY, new GuiData.TankParameters(119, 24, 136, 62));
        RenderUtils.drawFluidTooltip(graphics, font, this, screen -> menu.getBlockEntity().getTankCapability(4), mouseX, mouseY, new GuiData.TankParameters(141, 24, 158, 62));

        if (mouseX >= x + 163 && mouseX <= x + 169 && mouseY >= y + 17 && mouseY <= y + 68) {
            String energyText = menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE";
            graphics.renderComponentTooltip(font, java.util.List.of(Component.literal(energyText)), mouseX, mouseY);
        }
    }


    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, (imageWidth / 2) - (font.width(title) / 2), titleLabelY, 4210752, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 4210752, false);
    }
}
