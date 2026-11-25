package chronicboy.appleskince;

import api.AddonHandler;
import api.BTWAddon;

public class AppleSkinAddon extends BTWAddon {
	public AppleSkinAddon() {
		super();
	}

	@Override
	public void initialize() {
		AppleSkinConfig.load();
		AddonHandler.logMessage(this.getName() + " v" + this.getVersionString() + " initialized");
	}
}
