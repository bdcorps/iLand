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

		if(Build.VERSION.SDK_INT >= 16)
		{
			String p = PlanetMain.class.getPackage().getName();
			String c = PlanetMain.class.getCanonicalName();
		    i.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
		    i.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(p, c));
		}
		else
		{
		    i.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
		    Toast.makeText(getApplicationContext(),
					"Select 'iLand' from the list", Toast.LENGTH_LONG).show();
		}

		this.startActivityForResult(i, 0);

		finish();

		

		super.onCreate(savedInstanceState);
	}
}
