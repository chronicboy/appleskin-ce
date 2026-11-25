package chronicboy.appleskince;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppleSkinMod implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("AppleSkin CE");

	@Override
	public void onInitialize() {
		// Config is loaded in AppleSkinAddon.initialize(), after BTW CE has set up its config folder.
	}
}
