package kirsan122.nuclearisticcore.client.gui.components;

import kirsan122.nuclearisticcore.NuclearisticCore;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Supplier;

/**
 * Created by kirsan122,
 * Data: 04.11.2026,
 * Time: 12:14
 */
public class LeverToggleButton extends Button {
    private static final ResourceLocation TEXTURE = new ResourceLocation(NuclearisticCore.MOD_ID, "textures/gui/elements/lever.png");

    private final Supplier<Boolean> isSolidMode;

    public LeverToggleButton(int x, int y, Supplier<Boolean> isSolidMode, OnPress onPress) {
        super(x, y, 17, 24, Component.empty(), onPress, DEFAULT_NARRATION);
        this.isSolidMode = isSolidMode;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(TEXTURE, this.getX(), this.getY(), isSolidMode.get() ? 17 : 0, 0, isSolidMode.get() ? 16 : 17, 24, 33, 24);
    }
}
