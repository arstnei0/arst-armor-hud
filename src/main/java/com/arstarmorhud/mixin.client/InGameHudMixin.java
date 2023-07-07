package com.arstarmorhud.mixin.client;

//import net.minecraft.client.gui.DrawContext;
import com.arstarmorhud.ArstArmorHud;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

	@Shadow
	protected abstract PlayerEntity getCameraPlayer();

	@Shadow
	private int scaledWidth;

	@Shadow
	private int scaledHeight;

	@Shadow
	protected abstract void renderHotbarItem(GuiGraphics graphics, int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);

	private static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");

	@Inject(method = "renderHotbar", at = @At("HEAD"))
	public void renderArmorHud(float tickDelta, GuiGraphics graphics, CallbackInfo ci) {
		PlayerEntity player = this.getCameraPlayer();

		Arm arm = player.getMainArm();

		int halfWidth = this.scaledWidth / 2;
		int start;

		int n;
		int calculatedEmptySlots = 0;
		ItemStack[] items = { null, null, null, null };

		for (n = 0; n < 4; n++) {
			ItemStack item = player.getInventory().armor.get(n);
			items[n] = item;
			if (item.isEmpty()) {
				calculatedEmptySlots += 1;
			}
		}

		boolean hideEmptySlots = ArstArmorHud.CONFIG.hideEmptySlots();

		if (arm.equals(Arm.LEFT)) {
			start = halfWidth - 180;
			if (hideEmptySlots) {
				start += calculatedEmptySlots * 20;
			}
		} else {
			start = halfWidth + 100;
		}

		int y = this.scaledHeight - 16 - 3;
		int x;
		int emptySlots = 0;
		boolean empty;
		boolean showSlots = ArstArmorHud.CONFIG.showSlots();
		for (n = 0; n < 4; n++) {
			x = start + n * 20;
			ItemStack item = items[(3 - n)];
			empty = item.isEmpty();
			if (hideEmptySlots) {
				x -= emptySlots * 20;
				if (empty) {
					emptySlots += 1;
				}
			}
			if (showSlots && (!empty || !hideEmptySlots)) {
				graphics.getMatrices().push();
				graphics.getMatrices().translate(0.0F, 0.0F, -90.0F);
				graphics.drawTexture(WIDGETS_TEXTURE, x, this.scaledHeight - 22, 1 + n * 20, 0, 20, 22);
				graphics.getMatrices().pop();
			}
			this.renderHotbarItem(graphics, x + 2, y, tickDelta, player, item, 100);
		}

		if (showSlots && emptySlots < 4) {
			graphics.getMatrices().push();
			graphics.getMatrices().translate(0.0F, 0.0F, -90.0F);
			graphics.drawTexture(WIDGETS_TEXTURE, start - 1, this.scaledHeight - 22, 0, 0, 1, 22);
			int end = start + 80 - emptySlots * 20;
			graphics.drawTexture(WIDGETS_TEXTURE, end, this.scaledHeight - 22, 0, 0, 1, 22);
			graphics.getMatrices().pop();
		}
	}
}
