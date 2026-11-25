package chronicboy.appleskince;

import api.AddonHandler;
import api.BTWAddon;
import api.config.AddonConfig;

import java.io.File;

public class AppleSkinAddon extends BTWAddon {
	public AppleSkinAddon() {
		super();
	}

	@Override
	public void preInitialize() {
		// Earlier test builds wrote config/appleskin-ce.properties. BTW migrates that file into appleskin-ce.conf
		// and renames it to .properties.old, but the rename crashes the game if a .old file is already there.
		File old = new File("config/appleskin-ce.properties");
		File oldBackup = new File("config/appleskin-ce.properties.old");
		if (old.exists() && oldBackup.exists()) {
			oldBackup.delete();
		}
	}

	@Override
	public void registerConfigProperties(AddonConfig config) {
		AppleSkinConfig.register(config);
	}

	@Override
	public void handleConfigProperties(AddonConfig config) {
		AppleSkinConfig.read(config);
	}

	@Override
	public void initialize() {
		AddonHandler.logMessage(this.getName() + " v" + this.getVersionString() + " initialized");
	}
}
