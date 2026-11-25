package chronicboy.appleskince.mixin;

import chronicboy.appleskince.FoodIcons;
import chronicboy.appleskince.FoodInfo;
import emi.shims.java.com.unascribed.retroemi.EmiAgnosBTWFabric;
import emi.shims.java.net.minecraft.client.gui.tooltip.TextTooltipComponent;
import emi.shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.src.FontRenderer;
import net.minecraft.src.ItemStack;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

/**
 * BTW's built-in EMI builds item tooltips as components and draws them itself. Swap our placeholder lines
 * for a component that draws the shank row. Optional: if EMI changes, the mod still works without this.
 */
@Mixin(value = EmiAgnosBTWFabric.class, remap = false)
public class EmiTooltipMixin {
	@Inject(method = "getItemTooltipAgnos", at = @At("RETURN"), cancellable = true, require = 0)
	private void appleskin$iconRows(ItemStack stack, CallbackInfoReturnable<List<TooltipComponent>> cir) {
		try {
			List<TooltipComponent> in = cir.getReturnValue();
			if (in == null) return;
			FoodInfo info = null;
			List<TooltipComponent> out = null;
			for (int i = 0; i < in.size(); i++) {
				TooltipComponent c = in.get(i);
				if (c instanceof TextTooltipComponent text && FoodIcons.isMarked(text.getText())) {
					if (info == null) info = FoodInfo.of(stack);
					if (info == null) return;
					if (out == null) out = new ArrayList<>(in);
					out.set(i, new IconRow(info, text.getText()));
				}
			}
			if (out != null) cir.setReturnValue(out);
		} catch (Throwable ignored) {
		}
	}

	private static final class IconRow implements TooltipComponent {
		private final FoodInfo info;
		private final String line;
		private final int width;

		IconRow(FoodInfo info, String line) {
			this.info = info;
			this.line = line;
			this.width = FoodIcons.rowWidth(line.contains(FoodIcons.MARK_HUNGER)
					? FoodIcons.hungerIcons(info) : FoodIcons.fatIcons(info));
		}

		@Override
		public int getHeight() {
			return 10;
		}

		@Override
		public int getWidth(FontRenderer font) {
			return width;
		}

		@Override
		public void drawItems(FontRenderer font, int x, int y) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1F, 1F, 1F, 1F);
			FoodIcons.bindIcons();
			FoodIcons.drawMarkedLine(line, info, x, y);
		}
	}
}
