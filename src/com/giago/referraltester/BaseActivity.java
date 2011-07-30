package com.giago.referraltester;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.app.Activity;

public class BaseActivity extends Activity {
	
	private static final String ANALYTICS_UID = "UA-5190493-19";
	
	private GoogleAnalyticsTracker tracker;

	protected GoogleAnalyticsTracker getTracker() {
		if(tracker == null) {
			tracker = GoogleAnalyticsTracker.getInstance();
			tracker.start(ANALYTICS_UID, 20, this);
		}
		return tracker;
	}
	
	@Override
	public void onDestroy() {
		if(tracker != null) {
			tracker.stop();
		}
		super.onDestroy();
	}

	public void trackReferralTesterActivity() {
		getTracker().trackPageView("ReferralTester");
	}
	
	public void trackSendActivity() {
		getTracker().trackPageView("Send");
	}
}
