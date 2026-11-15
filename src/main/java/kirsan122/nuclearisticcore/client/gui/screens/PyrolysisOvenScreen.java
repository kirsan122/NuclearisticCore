package kirsan122.nuclearisticcore.client.gui.screens;

import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.client.gui.components.LeverToggleButton;
import kirsan122.nuclearisticcore.common.inventory.PyrolysisOvenMenu;
import kirsan122.nuclearisticcore.common.util.RenderUtils;
import kirsan122.nuclearisticcore.network.NCPacketHandler;
import kirsan122.nuclearisticcore.network.packet.PacketTogglePyrolysisMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by kirsan122,
 * Data: 04.11.2026,
 * Time: 11:37
 */
public class PyrolysisOvenScreen extends UniversalScreen<PyrolysisOvenMenu> {

    public static final ResourceLocation GUI_FLUID_MODE = new ResourceLocation(NuclearisticCore.MOD_ID, "textures/gui/pyrolysis_oven_fluid_mode.png");
    public static final ResourceLocation GUI_SOLID_MODE = new ResourceLocation(NuclearisticCore.MOD_ID, "textures/gui/pyrolysis_oven_solid_mode.png");

    public PyrolysisOvenScreen(PyrolysisOvenMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(new LeverToggleButton(
                leftPos + 13, topPos + 31,
                menu::isSolidModeActive,
                (btn) -> NCPacketHandler.sendToServer(new PacketTogglePyrolysisMode(menu.getBlockEntityPos()))
        ));
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(graphics, partialTick, mouseX, mouseY);

        ResourceLocation texture = menu.isSolidModeActive() ? GUI_SOLID_MODE : GUI_FLUID_MODE;

        int x = leftPos;
        int y = topPos;

        graphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight);

        if (!menu.isSolidModeActive()) {
            RenderUtils.drawFluid(graphics, this, s -> menu.getBlockEntity().getTankCapability(0), 0, x + 53, y + 15, 16, 44);
            RenderUtils.drawFluid(graphics, this, s -> menu.getBlockEntity().getTankCapability(1), 1, x + 107, y + 15, 16, 44);

            RenderUtils.drawTankDivisions(graphics, x + 53, y + 15);
            RenderUtils.drawTankDivisions(graphics, x + 107, y + 15);
        } else {
            RenderUtils.drawFluid(graphics, this, s -> menu.getBlockEntity().getTankCapability(2), 2, x + 59, y + 21, 16, 44);
            RenderUtils.drawTankDivisions(graphics, x + 59, y + 21);

            if (menu.isCrafting()) {
                graphics.blit(GUI_SOLID_MODE, x + 98, y + 38, 176, 0, menu.getScaledProgress(), 16);
            }
        }

        RenderUtils.drawEnergyLine(graphics, this, s -> menu.getScaledEnergy());
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        RenderUtils.drawEnergyTooltip(graphics, font, menu, leftPos, topPos, mouseX, mouseY);

        if (!menu.isSolidModeActive()) {
            RenderUtils.drawFluidTooltip(graphics, font, this, s -> menu.getBlockEntity().getTankCapability(0), mouseX, mouseY, new GuiData.TankParameters(52, 14, 69, 59));
            RenderUtils.drawFluidTooltip(graphics, font, this, s -> menu.getBlockEntity().getTankCapability(1), mouseX, mouseY, new GuiData.TankParameters(106, 14, 123, 59));
        } else {
            RenderUtils.drawFluidTooltip(graphics, font, this, s -> menu.getBlockEntity().getTankCapability(2), mouseX, mouseY, new GuiData.TankParameters(58, 20, 75, 65));
        }
        RenderUtils.drawEnergyTooltip(graphics, font, menu, leftPos, topPos, mouseX, mouseY);

    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, (imageWidth / 2) - (font.width(title) / 2), titleLabelY, 4210752, false);
    }
}
