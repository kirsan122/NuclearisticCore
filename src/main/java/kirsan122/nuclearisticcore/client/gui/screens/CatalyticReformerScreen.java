package kirsan122.nuclearisticcore.client.gui.screens;

import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.common.inventory.CatalyticReformerMenu;
import kirsan122.nuclearisticcore.common.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by kirsan122,
 * Data: 24.10.2026,
 * Time: 19:40
 */
public class CatalyticReformerScreen extends UniversalScreen<CatalyticReformerMenu> implements IProgressGetter {
    private static final ResourceLocation TEXTURE = new ResourceLocation(NuclearisticCore.MOD_ID, "textures/gui/catalytic_reformer_gui.png");

    public CatalyticReformerScreen(CatalyticReformerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageHeight = 174;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        int x = leftPos;
        int y = topPos;

        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        RenderUtils.drawEnergyLine(graphics, this, screen -> menu.getScaledEnergy(), 158, 18);

        RenderUtils.drawFluid(graphics, this, screen -> menu.getBlockEntity().getTankCapability(0), 0, x + 36, y + 17, 16, 52);
        RenderUtils.drawFluid(graphics, this, screen -> menu.getBlockEntity().getTankCapability(1), 1, x + 98, y + 17, 16, 52);
        RenderUtils.drawFluid(graphics, this, screen -> menu.getBlockEntity().getTankCapability(2), 2, x + 125, y + 17, 16, 52);

        RenderUtils.drawTankDivisions(graphics, x + 36, y + 20);
        RenderUtils.drawTankDivisions(graphics, x + 98, y + 20);
        RenderUtils.drawTankDivisions(graphics, x + 125, y + 20);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        int x = this.leftPos;
        int y = this.topPos;

        RenderUtils.drawFluidTooltip(graphics, font, this, screen -> menu.getBlockEntity().getTankCapability(0), mouseX, mouseY, new GuiData.TankParameters(35, 16, 52, 69));
        RenderUtils.drawFluidTooltip(graphics, font, this, screen -> menu.getBlockEntity().getTankCapability(1), mouseX, mouseY, new GuiData.TankParameters(97, 16, 114, 69));
        RenderUtils.drawFluidTooltip(graphics, font, this, screen -> menu.getBlockEntity().getTankCapability(2), mouseX, mouseY, new GuiData.TankParameters(124, 16, 141, 69));

        RenderUtils.drawEnergyTooltip(graphics, 157, 17, 163, 68, font, menu, x, y, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, (imageWidth / 2) - (font.width(title) / 2), titleLabelY, 4210752, false);
    }
}
