package chronicboy.appleskince.mixin;

import chronicboy.appleskince.FoodIcons;
import chronicboy.appleskince.FoodInfo;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Minecraft;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * Paints the shank rows into inventory tooltips. func_102021_a is the tooltip box drawer; it doesn't return
 * where it drew, so the position is worked out again with the same maths.
 */
@Mixin(GuiContainer.class)
public class GuiContainerMixin {
	@Inject(method = "drawItemStackTooltip", at = @At("HEAD"))
	private void appleskin$rememberStack(ItemStack stack, int x, int y, CallbackInfo ci) {
		FoodIcons.tooltipStack = stack;
	}

	@Inject(method = "drawItemStackTooltip", at = @At("RETURN"))
	private void appleskin$forgetStack(ItemStack stack, int x, int y, CallbackInfo ci) {
		FoodIcons.tooltipStack = null;
	}

	@SuppressWarnings("rawtypes")
	@Inject(method = "func_102021_a", at = @At("RETURN"))
	private void appleskin$drawIcons(List lines, int mouseX, int mouseY, CallbackInfo ci) {
		if (FoodIcons.tooltipStack == null || lines == null || lines.isEmpty()) return;
		try {
			boolean any = false;
			for (Object o : lines) {
				if (o instanceof String s && FoodIcons.isMarked(s)) {
					any = true;
					break;
				}
			}
			if (!any) return;
			FoodInfo info = FoodInfo.of(FoodIcons.tooltipStack);
			if (info == null) return;

			GuiScreen screen = (GuiScreen) (Object) this;
			FontRenderer font = Minecraft.getMinecraft().fontRenderer;
			int maxWidth = 0;
			for (Object o : lines) maxWidth = Math.max(maxWidth, font.getStringWidth((String) o));
			int x = mouseX + 12;
			int y = mouseY - 12;
			int height = 8;
			if (lines.size() > 1) height += 2 + (lines.size() - 1) * 10;
			if (x + maxWidth > screen.width) x -= 28 + maxWidth;
			if (y + height + 6 > screen.height) y = screen.height - height - 6;

			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			FoodIcons.bindIcons();
			FoodIcons.setZ(300F);
			for (int i = 0; i < lines.size(); i++) {
				FoodIcons.drawMarkedLine((String) lines.get(i), info, x, y);
				if (i == 0) y += 2;
				y += 10;
			}
			FoodIcons.setZ(0F);
		} catch (Throwable ignored) {
		} finally {
			// func_102021_a leaves lighting and depth test on; put them back the way it left them.
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}
}
