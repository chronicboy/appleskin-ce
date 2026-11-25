package chronicboy.appleskince.mixin;

import chronicboy.appleskince.AppleSkinConfig;
import chronicboy.appleskince.FoodInfo;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackMixin {
	@SuppressWarnings({"unchecked", "rawtypes"})
	@Inject(method = "getTooltip", at = @At("RETURN"))
	private void appleskin$addFoodLines(EntityPlayer player, boolean advanced, CallbackInfoReturnable<List> cir) {
		if (!AppleSkinConfig.showTooltips) return;
		List lines = cir.getReturnValue();
		if (lines == null) return;
		try {
			FoodInfo info = FoodInfo.of((ItemStack) (Object) this);
			if (info == null) return;

			lines.add("§7Hunger: §6" + FoodInfo.format(info.hunger / (float) FoodInfo.UNITS_PER_SHANK));
			if (info.fatMultiplier > 0F) {
				lines.add("§7Overflow to fat: §e" + Math.round(info.fatMultiplier * 100F) + "%");
			} else {
				lines.add("§7Overflow to fat: §8none");
			}

			if (AppleSkinConfig.showTooltipNowLine && player != null && !player.capabilities.isCreativeMode) {
				lines.add(nowLine(info, player));
			}
		} catch (Throwable t) {
			// Never break a tooltip over our extra lines.
		}
	}

	private static String nowLine(FoodInfo info, EntityPlayer player) {
		if (player.isPotionActive(Potion.hunger)) {
			return "§cCan't " + (info.isDrink ? "drink" : "eat") + " with the Hunger effect";
		}
		if (!info.canConsume(player)) {
			return "§8Now: too full to eat";
		}
		FoodInfo.Result r = info.predict(player.getFoodStats());
		if (!r.changesAnything()) {
			return "§8Now: no effect, hunger and fat are full";
		}
		StringBuilder sb = new StringBuilder("§7Now: ");
		boolean any = false;
		if (r.newFood > r.oldFood) {
			sb.append("§6+").append(FoodInfo.format((r.newFood - r.oldFood) / (float) FoodInfo.UNITS_PER_SHANK)).append(" hunger");
			any = true;
		}
		float fatIcons = (r.newFat - r.oldFat) / FoodInfo.FAT_PER_ICON;
		if (fatIcons > 0.001F) {
			if (any) sb.append("§7, ");
			String fat = FoodInfo.format(fatIcons);
			// Tiny gains round to 0; still say something was gained.
			sb.append("§e+").append("0".equals(fat) ? "<0.1" : fat).append(" fat");
		}
		return sb.toString();
	}
}
