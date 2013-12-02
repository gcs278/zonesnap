package com.zonesnap.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

import com.zonesnap.networking.NetworkPostLogin;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;
import com.zonesnap.classes.ZoneSnap_App;
import com.zonesnap.zonesnap_app.R;

// This activity is the first activity
// It logs in a user and takes them to the HomeActivity
public class LoginActivity extends Activity {
	private final String PENDING_ACTION_BUNDLE_KEY = "com.facebook.samples.hellofacebook:PendingAction";

	// GUI Variables
	private LoginButton loginButton;
	private TextView greeting;
	ProgressBar progressBar;
	// UI LifeCycle helper for transistions
	private UiLifecycleHelper uiHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			String name = savedInstanceState
					.getString(PENDING_ACTION_BUNDLE_KEY);
		}

		setContentView(R.layout.activity_login);

		loginButton = (LoginButton) findViewById(R.id.authButton);
		loginButton
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {
						// pass user to global_App
						ZoneSnap_App.user = user;
						updateUI();
					}

				});

		greeting = (TextView) findViewById(R.id.greeting);

		// Set the font of the ZoneSnap logo
		Typeface zsLogo = Typeface.createFromAsset(getAssets(),
				"fonts/capella.ttf");
		TextView title = (TextView) findViewById(R.id.login_title);
		title.setTypeface(zsLogo);

		// Set the font of the login message
		Typeface zsFont = Typeface.createFromAsset(getAssets(),
				"fonts/Orbitron-Regular.ttf");
		greeting.setTypeface(zsFont);

		progressBar = (ProgressBar) findViewById(R.id.login_progress);
	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d("HelloFacebook", "Success!");
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();

		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising reporting. Do so in
		// the onResume methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.activateApp(this);

		updateUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	// Settings menu
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent settings = new Intent(this, SettingsActivity.class);
		startActivity(settings);
		return super.onOptionsItemSelected(item);
	}

	// When the session State Changes
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {

		if ((exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(LoginActivity.this)
					.setTitle(R.string.cancelled)
					.setMessage(
							R.string.permission_not_granted
									+ exception.getMessage())
					.setPositiveButton(R.string.ok, null).show();
		}
		updateUI();
	}

	private void updateUI() {
		Session session = Session.getActiveSession();
		boolean activeSession = (session != null && session.isOpened());

		if (activeSession && ZoneSnap_App.user != null) {

			progressBar.setVisibility(View.VISIBLE);
			// Login in the user
			NetworkPostLogin task = new NetworkPostLogin(this, progressBar);
			task.execute(ZoneSnap_App.user.getUsername());

			greeting.setText(getString(R.string.hello_user,
					ZoneSnap_App.user.getFirstName()));
		} else {
			greeting.setText("Please log in");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

}
