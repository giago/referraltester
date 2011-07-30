package com.giago.referraltester;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ReferralTester extends BaseActivity {
	
	private static final String ARTICLE = "http://www.dev-articles.com/article/Android-Analytics-referral-tracking-447001";
	
	private Button manual;
	private String referralUrl;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.referral_tester_activity);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	trackReferralTesterActivity();
        Button scan = (Button)findViewById(R.id.scan_url_btn);
        scan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		        startActivityForResult(intent, 0);
			}
		});
        manual = (Button)findViewById(R.id.manual_url_btn);
        manual.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ReferralTester.this.startActivity(Send.getIntent(ReferralTester.this));
			}
		});
        Button readMore = (Button)findViewById(R.id.read_more_btn);
        readMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(ARTICLE));
				startActivity(i);
			}
		});
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
            	referralUrl = intent.getStringExtra("SCAN_RESULT");            	
            	startActivity(Send.getIntent(ReferralTester.this, referralUrl));                 
            } else {
            	Toast.makeText(ReferralTester.this, "No data from scan, try manual url generation", Toast.LENGTH_LONG).show();
            }
        }
    }
        
}