package chronicboy.appleskince;

import btw.item.items.BucketItemDrinkable;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.FoodStats;
import net.minecraft.src.Item;
import net.minecraft.src.ItemFood;
import net.minecraft.src.ItemStack;

/**
 * Food values for one item, in BTW's units, plus the maths BTW uses when you eat it.
 *
 * BTW's hunger bar holds 60 units: 6 units per shank, 10 shanks. Fat holds 0..20, 2 per icon.
 * Eating only fills hunger. Whatever doesn't fit becomes fat: excess units * fatMultiplier / 3
 * (see FoodStats.addStats(int, float)). So one shank of overflow gives fatMultiplier fat icons.
 */
public final class FoodInfo {
	public static final int MAX_HUNGER = 60;
	public static final float MAX_FAT = 20F;
	public static final int UNITS_PER_SHANK = 6;
	public static final float FAT_PER_ICON = 2F;

	/** Hunger restored, in BTW units (6 = one shank). */
	public final int hunger;
	/** Fat icons gained per shank of overflow. */
	public final float fatMultiplier;
	/** Drinkable buckets (milk) can always be drunk, even at full hunger. */
	public final boolean isDrink;
	private final boolean alwaysEdible;

	private FoodInfo(int hunger, float fatMultiplier, boolean isDrink, boolean alwaysEdible) {
		this.hunger = hunger;
		this.fatMultiplier = fatMultiplier;
		this.isDrink = isDrink;
		this.alwaysEdible = alwaysEdible;
	}

	/** Returns null when the stack isn't something you can eat or drink for hunger. */
	public static FoodInfo of(ItemStack stack) {
		if (stack == null) return null;
		Item item = stack.getItem();
		if (item instanceof ItemFood food) {
			int units = food.getHungerRestored();
			if (units <= 0) return null;
			return new FoodInfo(units, food.getSaturationModifier(), false, food.alwaysEdible);
		}
		if (item instanceof BucketItemDrinkable drink) {
			if (drink.hungerHealed <= 0) return null;
			return new FoodInfo(drink.hungerHealed, drink.saturationModifier, true, true);
		}
		return null;
	}

	/** Same rules as the item's own right-click check. */
	public boolean canConsume(EntityPlayer player) {
		return isDrink ? player.canDrink() : player.canEat(alwaysEdible);
	}

	/** What hunger and fat would be after eating this, mirroring FoodStats.addStats(int, float). */
	public Result predict(FoodStats stats) {
		int food = stats.getFoodLevel();
		float fat = stats.getSaturationLevel();
		int newFood = Math.min(food + hunger, MAX_HUNGER);
		int excess = hunger - (newFood - food);
		float newFat = fat;
		if (excess > 0) {
			newFat = Math.min(fat + excess * fatMultiplier / 3F, MAX_FAT);
		}
		return new Result(food, fat, newFood, newFat);
	}

	public static final class Result {
		public final int oldFood;
		public final float oldFat;
		public final int newFood;
		public final float newFat;

		Result(int oldFood, float oldFat, int newFood, float newFat) {
			this.oldFood = oldFood;
			this.oldFat = oldFat;
			this.newFood = newFood;
			this.newFat = newFat;
		}

		public boolean changesAnything() {
			return newFood > oldFood || newFat > oldFat + 0.001F;
		}
	}

	/** Shanks or icons with at most one decimal: 2, 1.5, 0.3. */
	public static String format(float value) {
		float rounded = Math.round(value * 10F) / 10F;
		if (rounded == (int) rounded) return Integer.toString((int) rounded);
		return String.format(java.util.Locale.ROOT, "%.1f", rounded);
	}
}
