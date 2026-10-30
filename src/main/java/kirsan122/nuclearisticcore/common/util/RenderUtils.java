package kirsan122.nuclearisticcore.common.util;

import kirsan122.nuclearisticcore.NuclearisticCore;
import kirsan122.nuclearisticcore.api.IUniversalMenu;
import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.common.blockEntity.machines.UniversalBlockEntity;
import kirsan122.nuclearisticcore.common.inventory.IFluidScreenRender;
import kirsan122.nuclearisticcore.common.inventory.UniversalMenu;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Created by kirsan122,
 * Data: 27.10.2026,
 * Time: 18:29
 */
public final class RenderUtils {

    /**
     * This excellent method renders the slide-out upgrades panel, avoiding code duplication.
     */
    public static void drawUpgradePanel(@NotNull GuiGraphics graphics, Font font, AbstractContainerMenu menu, int x, int y, float tabAnimationProgress, int upgradeSlotStart) {
        int currentOffsetX = (int) (GuiData.UPGRADE_AND_CONFIG_PANELS_WH * tabAnimationProgress);

        if (tabAnimationProgress > 0.0f) {
            int panelX = x - currentOffsetX;
            graphics.blit(GuiData.UPGRADE_PANEL_TEXTURE,
                    panelX, y + GuiData.PANELS_OFFSET, 0, 0, GuiData.UPGRADE_AND_CONFIG_PANELS_WH, GuiData.UPGRADE_AND_CONFIG_PANELS_WH, GuiData.UPGRADE_AND_CONFIG_PANELS_WH, GuiData.UPGRADE_AND_CONFIG_PANELS_WH);
        }

        if (tabAnimationProgress > 0.0f && tabAnimationProgress < 1.0f) {
            int itemRenderOffset = GuiData.UPGRADE_AND_CONFIG_PANELS_WH - currentOffsetX;
            int scissorX = x - currentOffsetX;
            int scissorY = y + GuiData.PANELS_OFFSET;

            graphics.enableScissor(scissorX, scissorY, scissorX + currentOffsetX, scissorY + GuiData.UPGRADE_AND_CONFIG_PANELS_WH);
            graphics.pose().pushPose();
            graphics.pose().translate(itemRenderOffset, 0, 0);

            for (int i = upgradeSlotStart; i < upgradeSlotStart + 4; i++) {
                net.minecraft.world.inventory.Slot slot = menu.getSlot(i);
                if (slot.hasItem()) {
                    graphics.renderItem(slot.getItem(), x + slot.x, y + slot.y);
                    graphics.renderItemDecorations(font, slot.getItem(), x + slot.x, y + slot.y);
                }
            }

            graphics.pose().popPose();
            graphics.disableScissor();
        }
    }

    /**
     * And this excellent method renders the slide-out config panel, avoiding code duplication.
     */
    public static void drawConfigPanel(@NotNull GuiGraphics graphics, IUniversalMenu<?> menu, int x, int y, int imageW, float configAnimationProgress) {

        int currentConfigX = (int) (82 * configAnimationProgress);

        int panelX = x + imageW;
        int panelY = y + GuiData.PANELS_OFFSET;

        if (configAnimationProgress > 0.0f) {
            graphics.enableScissor(panelX, panelY, panelX + currentConfigX, panelY + 82);

            int animatedPanelX = panelX - (82 - currentConfigX);
            graphics.blit(GuiData.CONFIG_PANEL_TEXTURE, animatedPanelX, panelY, 0, 0, 82, 82, 82, 82);

            ResourceLocation configModesTexture = new ResourceLocation(NuclearisticCore.MOD_ID, "textures/gui/elements/buttons_and_panels/gui_config_mode.png");

            for (int i = 0; i < 6; i++) {
                int btnRelX = GuiData.BUTTON_OFFSETS[i][0];
                int btnRelY = GuiData.BUTTON_OFFSETS[i][1];

                int sideMode = menu.getSideMode(i);

                if (sideMode > 0) {
                    int arrowX = animatedPanelX + btnRelX;
                    int arrowY = panelY + btnRelY;
                    int v = (sideMode == 1) ? 0 : 18;

                    graphics.blit(configModesTexture, arrowX, arrowY, 0, v, 18, 18, 18, 36);
                }
            }
            graphics.disableScissor();
        }
    }

    /**
     * This method renders energy tooltip in all devices of this mod, energy tooltip is located in
     * the same place, except for the generator and some machines.
     */
    public static void drawEnergyTooltip(@NotNull GuiGraphics graphics, Font font, UniversalMenu<?> menu, int x, int y, int mouseX, int mouseY) {
        drawEnergyTooltip(graphics, 161, 167, 17, 68, font, menu, x, y, mouseX, mouseY);
    }

    public static void drawEnergyTooltip(@NotNull GuiGraphics graphics, int startX, int endX, int startY, int endY, Font font, UniversalMenu<?> menu, int x, int y, int mouseX, int mouseY) {
        if (mouseX >= x + startX && mouseX <= x + endX && mouseY >= y + startY && mouseY <= y + endY) {
            String energyText = menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE";
            graphics.renderComponentTooltip(font, java.util.List.of(Component.literal(energyText)), mouseX, mouseY);
        }
    }

    public static void drawEnergyLine(@NotNull GuiGraphics graphics, AbstractContainerScreen<?> screen, Function<AbstractContainerScreen<?>, Integer> getScaledEnergy, int xPos, int yPos) {
        int scaledEnergy = getScaledEnergy.apply(screen);
        graphics.blit(GuiData.ENERGY_LINE, screen.getGuiLeft() + xPos, screen.getGuiTop() + yPos + (50 - scaledEnergy), 0, (50 - scaledEnergy), 5, scaledEnergy, 5, 50);
    }

    public static void drawEnergyLine(@NotNull GuiGraphics graphics, AbstractContainerScreen<?> screen, Function<AbstractContainerScreen<?>, Integer> getScaledEnergy) {
        drawEnergyLine(graphics, screen, getScaledEnergy, 162, 18);
    }

    public static void drawTankDivisions(@NotNull GuiGraphics graphics, int x, int y) {
        graphics.blit(GuiData.ELECTROLYZER_DIVISIONS, x, y, 0, 0, 16, 45, 16, 45);
    }

    public static void drawButtonsTooltips(@NotNull GuiGraphics graphics, GuiData.Holder configOpenHolder, Font font, int leftPos, int topPos, int mouseX, int mouseY, boolean isUpgradeTabOpen, boolean isUpgradable, boolean isSideConfigurable) {
        int configBtnMinX = leftPos + 176;
        int configBtnMaxX = configBtnMinX + 25;
        int btnMinY = topPos + GuiData.UPGRADE_AND_CONFIG_BUTTONS_OFFSET;
        int configBtnMaxY = btnMinY + 26;

        int btnMinX = leftPos - 25;

        int btnMaxY = topPos + GuiData.UPGRADE_AND_CONFIG_BUTTONS_OFFSET + 26;
        if (isSideConfigurable) {
            if (mouseX >= configBtnMinX && mouseX <= configBtnMaxX && mouseY >= btnMinY && mouseY <= configBtnMaxY) {
                Component tooltipText = configOpenHolder.value
                        ? Component.translatable("tooltip.nuclearisticcore.close_config_tab")
                        : Component.translatable("tooltip.nuclearisticcore.open_config_tab");
                graphics.renderComponentTooltip(font, java.util.List.of(tooltipText), mouseX, mouseY);
            }
        }
        if (isUpgradable) {
            if (mouseX >= btnMinX && mouseX <= leftPos && mouseY >= btnMinY && mouseY <= btnMaxY) {
                Component tooltipText = isUpgradeTabOpen
                        ? Component.translatable("tooltip.nuclearisticcore.close_upgrade_tab")
                        : Component.translatable("tooltip.nuclearisticcore.open_upgrade_tab");

                graphics.renderComponentTooltip(font, java.util.List.of(tooltipText), mouseX, mouseY);
            }
        }
    }

    public static void drawFluidTooltip(@NotNull GuiGraphics graphics, Font font, AbstractContainerScreen<?> screen, Function<AbstractContainerScreen<?>, LazyOptional<IFluidHandler>> getter, int mouseX, int mouseY, GuiData.TankParameters... tankParameters) {
        int x = screen.getGuiLeft();
        int y = screen.getGuiTop();

        for (GuiData.TankParameters parameters : tankParameters) {
            int startX = parameters.startX();
            int startY = parameters.startY();
            int endX = parameters.endX();
            int endY = parameters.endY();

            if (mouseX >= x + startX && mouseX < x + endX && mouseY >= y + startY && mouseY < y + endY) {
                var handler = getter.apply(screen);
                handler.ifPresent(tank -> {
                    net.minecraftforge.fluids.FluidStack fluidStack = tank.getFluidInTank(0);
                    java.util.List<Component> tooltip = new java.util.ArrayList<>();

                    if (fluidStack.isEmpty()) {
                        tooltip.add(Component.translatable("tooltip.nuclearisticcore.empty"));
                    } else {

                        tooltip.add(Component.translatable(fluidStack.getFluid().getFluidType().getDescriptionId()));

                        tooltip.add(Component.literal(fluidStack.getAmount() + " / " + tank.getTankCapacity(0) + " mB")
                                .withStyle(net.minecraft.ChatFormatting.GRAY));
                    }
                    graphics.renderComponentTooltip(font, tooltip, mouseX, mouseY);
                });
            }
        }
    }

    public static void drawFluid(GuiGraphics graphics, AbstractContainerScreen<?> screen, Function<AbstractContainerScreen<?>, LazyOptional<IFluidHandler>> getter, int tankIndex, int tankX, int tankY, int tankWidth, int tankMaxHeight) {

        LazyOptional<IFluidHandler> cap = getter.apply(screen);
        if (cap == null) return;

        cap.ifPresent(handler -> {
            int internalIndex = handler.getTanks() == 1 ? 0 : tankIndex;
            if (internalIndex >= handler.getTanks()) return;

            net.minecraftforge.fluids.FluidStack fluidStack = handler.getFluidInTank(internalIndex);
            int amount = fluidStack.getAmount();
            int capacity = handler.getTankCapacity(internalIndex);

            if (!fluidStack.isEmpty() && amount > 0) {
                int fluidHeight = (int) ((float) amount / capacity * tankMaxHeight);
                if (fluidHeight > tankMaxHeight) fluidHeight = tankMaxHeight;
                if (fluidHeight <= 0) return;

                net.minecraft.world.level.material.Fluid fluid = fluidStack.getFluid();
                net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions extensions = net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions.of(fluid);
                ResourceLocation fluidTexture = extensions.getStillTexture(fluidStack);

                net.minecraft.client.renderer.texture.TextureAtlasSprite sprite = net.minecraft.client.Minecraft.getInstance()
                        .getTextureAtlas(net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS)
                        .apply(fluidTexture != null ? fluidTexture : new ResourceLocation("block/water_still"));

                int color = extensions.getTintColor(fluidStack);
                float r = ((color >> 16) & 0xFF) / 255.0F;
                float g = ((color >> 8) & 0xFF) / 255.0F;
                float b = (color & 0xFF) / 255.0F;
                float a = ((color >> 24) & 0xFF) / 255.0F;

                graphics.flush();

                com.mojang.blaze3d.systems.RenderSystem.setShader(net.minecraft.client.renderer.GameRenderer::getPositionTexShader);
                com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, net.minecraft.world.inventory.InventoryMenu.BLOCK_ATLAS);
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(r, g, b, a);

                com.mojang.blaze3d.systems.RenderSystem.enableBlend();
                com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();

                com.mojang.blaze3d.vertex.Tesselator tesselator = com.mojang.blaze3d.vertex.Tesselator.getInstance();
                com.mojang.blaze3d.vertex.BufferBuilder bufferbuilder = tesselator.getBuilder();
                bufferbuilder.begin(com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_TEX);

                int renderStartY = tankY + tankMaxHeight;
                int renderTargetY = tankY + (tankMaxHeight - fluidHeight);

                for (int currentX = tankX; currentX < tankX + tankWidth; currentX += 16) {
                    int widthToDraw = Math.min(16, (tankX + tankWidth) - currentX);

                    for (int currentY = renderStartY; currentY > renderTargetY; currentY -= 16) {
                        int heightToDraw = Math.min(16, currentY - renderTargetY);
                        int drawAtY = currentY - heightToDraw;

                        float uMin = sprite.getU0();
                        float uMax = sprite.getU1();
                        float vMin = sprite.getV0();
                        float vMax = sprite.getV1();

                        if (widthToDraw < 16) {
                            uMax = uMin + (uMax - uMin) * ((float) widthToDraw / 16.0F);
                        }
                        if (heightToDraw < 16) {
                            int pixelsCutFromTop = 16 - heightToDraw;
                            vMin = vMin + (vMax - vMin) * ((float) pixelsCutFromTop / 16.0F);
                        }

                        org.joml.Matrix4f matrix = graphics.pose().last().pose();
                        bufferbuilder.vertex(matrix, currentX, drawAtY + heightToDraw, 0).uv(uMin, vMax).endVertex();
                        bufferbuilder.vertex(matrix, currentX + widthToDraw, drawAtY + heightToDraw, 0).uv(uMax, vMax).endVertex();
                        bufferbuilder.vertex(matrix, currentX + widthToDraw, drawAtY, 0).uv(uMax, vMin).endVertex();
                        bufferbuilder.vertex(matrix, currentX, drawAtY, 0).uv(uMin, vMin).endVertex();
                    }
                }

                tesselator.end();

                com.mojang.blaze3d.systems.RenderSystem.disableBlend();
                com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                graphics.flush();
            }
        });
    }
}
