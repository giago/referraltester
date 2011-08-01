package com.giago.referraltester;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ReferralTester extends BaseActivity {
	
	private static final String ARTICLE = "http://www.dev-articles.com/article/Android-Analytics-referral-tracking-447001";
	
	private static final int MISSING_SCANNER_DIALOG_ID = 10000;
	
	public static final String DEFAULT_TITLE = "Install Barcode Scanner?";
	public static final String DEFAULT_MESSAGE = "This application requires Barcode Scanner. Would you like to install it?";
	public static final String DEFAULT_YES = "Yes";
	public static final String DEFAULT_NO = "No";
	private static final String PACKAGE = "com.google.zxing.client.android";
	
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
		        try {
		        	startActivityForResult(intent, 0);
		        } catch (ActivityNotFoundException e) {
					showDialog(MISSING_SCANNER_DIALOG_ID);
				}
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
    
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case MISSING_SCANNER_DIALOG_ID:
                dialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
				          .setTitle(DEFAULT_TITLE)
				          .setMessage(DEFAULT_MESSAGE)
				          .setNegativeButton(DEFAULT_NO, new DialogInterface.OnClickListener() {
				        	  @Override
				        	  public void onClick(DialogInterface dialog, int which) {
				        		  dialog.dismiss();
				        	  }
				          })
				          .setPositiveButton(DEFAULT_YES, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Uri uri = Uri.parse("market://search?q=pname:" + PACKAGE);
							        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
							        ReferralTester.this.startActivity(intent);
								}
				          }).create();
                break;
        }
        if (dialog == null) {
            dialog = super.onCreateDialog(id);
        }
        return dialog;
    }
        
}