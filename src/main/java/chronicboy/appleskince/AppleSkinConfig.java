package chronicboy.appleskince;

import api.config.AddonConfig;

/**
 * Settings, stored by BTW's own addon config system in config/appleskin-ce.conf.
 * (BTW treats config/appleskin-ce.properties as an old-format file and migrates it, so don't write one.)
 */
public final class AppleSkinConfig {
	/** Shank and fat icon rows in food tooltips, like AppleSkin. */
	public static boolean showTooltips = true;
	/** Warn about effects eating can give, like raw meat's chance of Hunger. */
	public static boolean showTooltipEffects = true;
	/** Extra text lines: exact numbers and what eating it right now would do. */
	public static boolean showTooltipNumbers = false;
	/** Flash the shanks and fat that held food would add. */
	public static boolean showHudPreview = true;
	/** Hatched exhaustion bar behind the hunger bar, like AppleSkin (singleplayer only). */
	public static boolean showExhaustion = true;

	private AppleSkinConfig() {
	}

	public static void register(AddonConfig config) {
		config.registerBoolean("showTooltips", true, "Shank and fat icon rows in food tooltips");
		config.registerBoolean("showTooltipEffects", true, "Show effects eating can give, like raw meat's chance of Hunger");
		config.registerBoolean("showTooltipNumbers", false, "Extra tooltip lines with exact numbers and what eating it now would do");
		config.registerBoolean("showHudPreview", true, "Flash the shanks and fat that the food you're holding would add");
		config.registerBoolean("showExhaustion", true, "Hatched exhaustion bar behind the hunger bar (singleplayer only)");
	}

	public static void read(AddonConfig config) {
		showTooltips = config.getBoolean("showTooltips");
		showTooltipEffects = config.getBoolean("showTooltipEffects");
		showTooltipNumbers = config.getBoolean("showTooltipNumbers");
		showHudPreview = config.getBoolean("showHudPreview");
		showExhaustion = config.getBoolean("showExhaustion");
	}
}
