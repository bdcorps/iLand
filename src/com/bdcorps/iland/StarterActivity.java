package com.bdcorps.iland;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

public class StarterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent i = new Intent();

		if (Build.VERSION.SDK_INT > 15) {
			i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);

			String p = PlanetMain.class.getPackage().getName();
			String c = PlanetMain.class.getCanonicalName();
			i.putExtra(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER,
					new ComponentName(p, c));
		} else {
			i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
		}
		this.startActivityForResult(i, 0);

		finish();

		Toast.makeText(getApplicationContext(),
				"Select 'iLand' from the list", Toast.LENGTH_LONG).show();

		super.onCreate(savedInstanceState);
	}
}
