package org.linaro.wallpaper;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class LogoSettings extends PreferenceActivity {

	public final static String PREFS_NAME = "logo1_settings";
	public final static String KEY_NAME = "location";

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		getPreferenceManager().setSharedPreferencesName(PREFS_NAME);
		addPreferencesFromResource(R.xml.logo1_settings);
	}
}
