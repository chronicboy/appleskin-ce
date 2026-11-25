package chronicboy.appleskince.mixin;

import chronicboy.appleskince.FoodIcons;
import net.minecraft.src.GuiContainerCreative;
import net.minecraft.src.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The creative search tab draws its own tooltip without calling GuiContainer's, so note the stack here too. */
@Mixin(GuiContainerCreative.class)
public class GuiContainerCreativeMixin {
	@Inject(method = "drawItemStackTooltip", at = @At("HEAD"))
	private void appleskin$rememberStack(ItemStack stack, int x, int y, CallbackInfo ci) {
		FoodIcons.tooltipStack = stack;
	}

	@Inject(method = "drawItemStackTooltip", at = @At("RETURN"))
	private void appleskin$forgetStack(ItemStack stack, int x, int y, CallbackInfo ci) {
		FoodIcons.tooltipStack = null;
	}
}
