package kirsan122.nuclearisticcore.client.gui.screens;

import kirsan122.nuclearisticcore.api.ConfigModes;
import kirsan122.nuclearisticcore.api.IUniversalMenu;
import kirsan122.nuclearisticcore.client.gui.GuiData;
import kirsan122.nuclearisticcore.client.gui.components.*;
import kirsan122.nuclearisticcore.common.inventory.UniversalMenu;
import kirsan122.nuclearisticcore.common.util.RenderUtils;
import kirsan122.nuclearisticcore.network.NCPacketHandler;
import kirsan122.nuclearisticcore.network.packet.PacketUpdateConfig;
import kirsan122.nuclearisticcore.network.packet.PacketUpdateRedstone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class UniversalScreen<T extends UniversalMenu<?>> extends AbstractContainerScreen<T> {
    protected ConfigModes.ConfigType currentTab = ConfigModes.ConfigType.ITEM;
    protected UpgradeButtonWidget upgradeButton;
    private RedstoneButton redstoneButton;
    protected ConfigTabButtonWidget configTabButton;
    protected final List<SideModeButtonWidget> sideButtons = new ArrayList<>();

    protected boolean isUpgradeTabOpen = false;
    protected boolean isConfigTabOpen = false;

    protected float configAnimationProgress = 0.0f;
    protected float upgradeAnimationProgress = 0.0f;
    protected final float animationSpeed = 0.1f;

    public UniversalScreen(T menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        sideButtons.clear();
        int tabX = leftPos + imageWidth;
        int tabY = topPos + 115;

        if (menu.getBlockEntity().isRedstoneConfigurable()) {
            RedstoneTabWidget tab = new RedstoneTabWidget(tabX, tabY);
            addRenderableOnly(tab);

            redstoneButton = new RedstoneButton(tabX + 3, tabY + 4,
                    menu.getBlockEntity()::getRedstoneMode,
                    (button) -> NCPacketHandler.sendToServer(new PacketUpdateRedstone(menu.getBlockEntityPos()))
            );
            addRenderableWidget(redstoneButton);
        }

        if (menu.getBlockEntity().isUpgradable()) {
            upgradeButton = new UpgradeButtonWidget(
                    leftPos - GuiData.ALL_CONFIG_BUTTONS_W,
                    topPos + GuiData.UPGRADE_AND_CONFIG_BUTTONS_OFFSET,
                    GuiData.ALL_CONFIG_BUTTONS_W, GuiData.ALL_CONFIG_BUTTONS_H,
                    () -> isUpgradeTabOpen,
                    (btn) -> {
                        isUpgradeTabOpen = !isUpgradeTabOpen;
                        if (minecraft.gameMode != null) {
                            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                        }
                    }
            );
            addRenderableWidget(upgradeButton);
        }

        if (menu.getBlockEntity().isSideConfigurable()) {
            configTabButton = new ConfigTabButtonWidget(
                    leftPos + imageWidth,
                    topPos + GuiData.UPGRADE_AND_CONFIG_BUTTONS_OFFSET,
                    25, 26,
                    () -> isConfigTabOpen,
                    (btn) -> isConfigTabOpen = !isConfigTabOpen
            );
            addRenderableWidget(configTabButton);

            for (int i = 0; i < 6; i++) {
                SideModeButtonWidget sideBtn = getSideModeButtonWidget(i);
                sideButtons.add(sideBtn);
                addRenderableWidget(sideBtn);
            }
        }
    }

    private @NotNull SideModeButtonWidget getSideModeButtonWidget(int i) {
        final int index = i;
        SideModeButtonWidget sideBtn = new SideModeButtonWidget(
                0, 0,
                18, 18,
                index,
                (btn) -> {
                    if (menu.getBlockEntity() != null) {
                        Direction facing = menu.getBlockEntity().getBlockState().getValue(
                                net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
                        );
                        Direction absoluteSide = getAbsoluteSide(facing, index);
                        NCPacketHandler.sendToServer(new PacketUpdateConfig(menu.getBlockEntityPos(), currentTab, absoluteSide));
                    }
                }
        );
        return sideBtn;
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (menu.getBlockEntity().isUpgradable()) {
            if (isUpgradeTabOpen && upgradeAnimationProgress < 1.0f) {
                upgradeAnimationProgress = Math.min(1.0f, upgradeAnimationProgress + animationSpeed);
            } else if (!isUpgradeTabOpen && upgradeAnimationProgress > 0.0f) {
                upgradeAnimationProgress = Math.max(0.0f, upgradeAnimationProgress - animationSpeed);
            }
            menu.setUpgradeTabFullyOpen(upgradeAnimationProgress >= 1.0f);
        }

        if (menu.getBlockEntity().isSideConfigurable()) {
            if (isConfigTabOpen && configAnimationProgress < 1.0f) {
                configAnimationProgress = Math.min(1.0f, configAnimationProgress + animationSpeed);
            } else if (!isConfigTabOpen && configAnimationProgress > 0.0f) {
                configAnimationProgress = Math.max(0.0f, configAnimationProgress - animationSpeed);
            }

            int currentPanelWidth = (int) (GuiData.UPGRADE_AND_CONFIG_PANELS_WH * configAnimationProgress);
            int currentLeftX = leftPos + imageWidth + (currentPanelWidth - GuiData.UPGRADE_AND_CONFIG_PANELS_WH);

            for (int i = 0; i < 6; i++) {
                SideModeButtonWidget btn = sideButtons.get(i);
                btn.setX(currentLeftX + GuiData.BUTTON_OFFSETS[i][0]);
                btn.setY(topPos + GuiData.BUTTON_OFFSETS[i][1] + 4);

                btn.visible = (configAnimationProgress >= 0.95f);
                btn.active = (configAnimationProgress >= 0.95f);
            }
        }
    }

    @Override
    protected boolean isHovering(int slotX, int slotY, int sizeX, int sizeY, double mouseX, double mouseY) {
        if (slotX < 0 && menu.getBlockEntity().isUpgradable()) {
            if (upgradeAnimationProgress < 0.95f) return false;

            int currentOffsetX = (int) (GuiData.UPGRADE_AND_CONFIG_PANELS_WH * upgradeAnimationProgress);
            int animatedX = slotX + (GuiData.UPGRADE_AND_CONFIG_PANELS_WH - currentOffsetX);

            return mouseX >= (leftPos + animatedX) && mouseX < (leftPos + animatedX + sizeX)
                    && mouseY >= (topPos + slotY) && mouseY < (topPos + slotY + sizeY);
        }
        return super.isHovering(slotX, slotY, sizeX, sizeY, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        if (menu.getBlockEntity().isUpgradable())
            RenderUtils.drawUpgradePanel(graphics, font, menu, leftPos, topPos, upgradeAnimationProgress, 4);
        if (menu.getBlockEntity().isSideConfigurable()) {
            RenderUtils.drawConfigPanel(graphics, menu, leftPos, topPos, imageWidth, configAnimationProgress);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);

        if (redstoneButton != null && redstoneButton.isMouseOver(mouseX, mouseY)) {
            graphics.renderComponentTooltip(font, java.util.List.of(redstoneButton.getTooltipText()), mouseX, mouseY);
        }

        GuiData.Holder configHolder = new GuiData.Holder(isConfigTabOpen);
        RenderUtils.drawButtonsTooltips(graphics, configHolder, font, leftPos, topPos, mouseX, mouseY, isUpgradeTabOpen, menu.getBlockEntity().isUpgradable(), menu.getBlockEntity().isSideConfigurable());

        if (menu.getBlockEntity().isSideConfigurable()) {
            drawSideModesTooltips(graphics, this, mouseX, mouseY, configAnimationProgress);
        }
    }

    public float getUpgradeAnimationProgress() { return upgradeAnimationProgress; }
    public float getConfigAnimationProgress() { return configAnimationProgress; }

    @Override
    protected void slotClicked(net.minecraft.world.inventory.Slot slot, int slotId, int mouseButton, net.minecraft.world.inventory.ClickType clickType) {
        if (clickType == net.minecraft.world.inventory.ClickType.QUICK_MOVE) {
            if (isUpgradeTabOpen && upgradeAnimationProgress < 0.95f) {
                return;
            }
        }
        super.slotClicked(slot, slotId, mouseButton, clickType);
    }

    private static void drawSideModesTooltips(GuiGraphics graphics, AbstractContainerScreen<?> screen, int mouseX, int mouseY, float animationProgress) {
        if (animationProgress < 1.0f) return;
        if (!(screen.getMenu() instanceof IUniversalMenu<?> configMenu)) throw new IllegalArgumentException("Screen must be implement interface kirsan122.nuclearisticcore.api.IUniversalMenu");

        int panelX = screen.getGuiLeft() + screen.getXSize();
        int panelY = screen.getGuiTop() + GuiData.PANELS_OFFSET;

        for (int i = 0; i < 6; i++) {
            int slotX = panelX + GuiData.BUTTON_OFFSETS[i][0];
            int slotY = panelY + GuiData.BUTTON_OFFSETS[i][1];
            if (mouseX >= slotX && mouseX < slotX + 18 && mouseY >= slotY && mouseY < slotY + 18) {
                int sideMode = configMenu.getBlockEntity().getDeviceSideMode(i);
                String modeText = "tooltip.nuclearisticcore.sidemode.none";
                if (sideMode == 1) modeText = "tooltip.nuclearisticcore.sidemode.input";
                if (sideMode == 2) modeText = "tooltip.nuclearisticcore.sidemode.output";
                Component text = Component.translatable(GuiData.SIDE_NAMES[i])
                        .append(Component.literal(" : ")
                                .append(Component.translatable(modeText)));
                graphics.renderTooltip(Minecraft.getInstance().font, text, mouseX, mouseY);
                break;
            }
        }
    }
    private static Direction getAbsoluteSide(Direction facing, int uiSideIndex) {
        Direction localSide = Direction.from3DDataValue(uiSideIndex);
        if (localSide.getAxis().isVertical() || facing == null) {
            return localSide;
        }
        return switch (facing) {
            case SOUTH -> localSide.getOpposite();
            case WEST -> localSide.getCounterClockWise();
            case EAST -> localSide.getClockWise();
            default -> localSide;
        };
    }
}