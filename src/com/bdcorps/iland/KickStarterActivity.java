package com.bdcorps.iland;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;

public class KickStarterActivity extends Activity{

	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
	 
				// set title
				alertDialogBuilder.setTitle("KickStarter Campaign");
				 final SpannableString s = 
			               new SpannableString("Enjoying this wallpaper so far? Help it become even better by backing it up on KickStarter here: https://www.kickstarter.com/projects/1694577009/iland-live-wallpaper-android");
			  Linkify.addLinks(s, Linkify.WEB_URLS);
				// set dialog message
				alertDialogBuilder
					.setMessage(s)
					.setCancelable(false)
					.setPositiveButton("Got it!",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							KickStarterActivity.this.finish();
						}
					  });
	 
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();}
}
