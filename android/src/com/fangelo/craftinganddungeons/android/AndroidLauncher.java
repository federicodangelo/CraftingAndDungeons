package com.fangelo.craftinganddungeons.android;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.fangelo.craftinganddungeons.MyGdxGame;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public class AndroidLauncher extends AndroidApplication implements 
	GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener {
	
	private static final int REQUEST_CODE_RESOLVE_ERR = 31415;
	private static int RC_SIGN_IN = 9001;
	private GoogleApiClient mGoogleApiClient;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useImmersiveMode = true;
		
		initialize(new MyGdxGame(new AndroidPlatformAdapter(this)), config);
		
		// Create the Google Api Client with access to the Play Games services
	    mGoogleApiClient = new GoogleApiClient.Builder(this)
	            .addConnectionCallbacks(this)
	            .addOnConnectionFailedListener(this)
	            .addApi(Games.API).addScope(Games.SCOPE_GAMES)
	            // add other APIs and scopes here as needed
	            .build();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//mGoogleApiClient.connect();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		//if (mGoogleApiClient.isConnected())
		//	mGoogleApiClient.disconnect();
	}
	
	public void loginSocialNetwork() {
		mGoogleApiClient.connect();
	}
	
	public void logoutSocialNetwork() {
		Games.signOut(mGoogleApiClient);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		 if (result.hasResolution()) {
		 	try {
		 		result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
		 	} catch (SendIntentException e) {
		 		//mGoogleApiClient.connect();
	       }
	   }
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// The player is signed in. Hide the sign-in button and allow the
	    // player to proceed.		
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// Attempt to reconnect
	    mGoogleApiClient.connect();		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == RC_SIGN_IN) {
	        if (resultCode == RESULT_OK) {
	            mGoogleApiClient.connect();
	        } else {
	            // Bring up an error dialog to alert the user that sign-in
	            // failed. The R.string.signin_failure should reference an error
	            // string in your strings.xml file that tells the user they
	            // could not be signed in, such as "Unable to sign in."
	        	
	            //BaseGameUtils.showActivityResultError(this,
	            //    requestCode, resultCode, R.string.signin_failure);
	        }
	    }		
	}
}
