package chronicboy.appleskince;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/** Plain on/off switches in config/appleskin-ce.properties. Missing keys are written back with defaults. */
public final class AppleSkinConfig {
	private static final File FILE = new File("config/appleskin-ce.properties");

	/** Hunger and fat values in food tooltips. */
	public static boolean showTooltips = true;
	/** The "Now:" tooltip line: what eating it right now would do. */
	public static boolean showTooltipNowLine = true;
	/** Flash the shanks and fat that held food would add. */
	public static boolean showHudPreview = true;
	/** Thin exhaustion line under the hunger bar (singleplayer only). */
	public static boolean showExhaustion = true;

	private AppleSkinConfig() {
	}

	public static void load() {
		Properties p = new Properties();
		if (FILE.exists()) {
			try (InputStream in = new FileInputStream(FILE)) {
				p.load(in);
			} catch (IOException e) {
				AppleSkinMod.LOGGER.warn("Could not read " + FILE + ", using defaults", e);
			}
		}
		showTooltips = read(p, "showTooltips", showTooltips);
		showTooltipNowLine = read(p, "showTooltipNowLine", showTooltipNowLine);
		showHudPreview = read(p, "showHudPreview", showHudPreview);
		showExhaustion = read(p, "showExhaustion", showExhaustion);
		save();
	}

	private static boolean read(Properties p, String key, boolean def) {
		String v = p.getProperty(key);
		return v == null ? def : Boolean.parseBoolean(v.trim());
	}

	public static void save() {
		Properties p = new Properties();
		p.setProperty("showTooltips", Boolean.toString(showTooltips));
		p.setProperty("showTooltipNowLine", Boolean.toString(showTooltipNowLine));
		p.setProperty("showHudPreview", Boolean.toString(showHudPreview));
		p.setProperty("showExhaustion", Boolean.toString(showExhaustion));
		File dir = FILE.getParentFile();
		if (dir != null && !dir.exists()) dir.mkdirs();
		try (OutputStream out = new FileOutputStream(FILE)) {
			p.store(out, "AppleSkin CE - set any of these to false to turn that feature off");
		} catch (IOException e) {
			AppleSkinMod.LOGGER.warn("Could not write " + FILE, e);
		}
	}
}
