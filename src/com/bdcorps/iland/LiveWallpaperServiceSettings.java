package com.bdcorps.iland;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.bdcorps.iland.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class LiveWallpaperServiceSettings extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	
	private InterstitialAd interstitial;
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		getPreferenceManager().setSharedPreferencesName(
				PlanetMain.SHARED_PREFS_NAME);
		addPreferencesFromResource(R.xml.wallpaper_settings);
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

		// Admob Interstitial
		interstitial = new InterstitialAd(this);
		  interstitial.setAdUnitId("ca-app-pub-2914505663533005/7056850773");

			AdRequest adRequest = new AdRequest.Builder()
			.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
			.addTestDevice("410095009ef12100").build();
			
		  interstitial.loadAd(adRequest);
		  interstitial.setAdListener(new AdListener() {
		      public void onAdLoaded() {
		          displayInterstitial();
		      }
		  });
		  }

		  public void displayInterstitial() {
		  if (interstitial.isLoaded()) {
		      interstitial.show();
		  }
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
	}

}
