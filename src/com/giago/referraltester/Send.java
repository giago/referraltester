package com.giago.referraltester;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Send extends BaseActivity {
	
	private static final String REFERRAL_URL_EXTRA = "referralUrl";
	private Button send;
	private EditText packageName;
	private EditText campaignSource;
	private EditText campaignMedium;
	private EditText campaignTerm;
	private EditText campaignContent;
	private EditText campaignName;
	private Map<String, String> properties;
	
	public static final Intent getIntent(Context context, String  referralUrl) {
		Intent i = getIntent(context);
		i.putExtra(REFERRAL_URL_EXTRA, referralUrl);
		return i;
	}
	
	private static final String getReferralUrl(Intent i) {
		return i.getStringExtra(REFERRAL_URL_EXTRA);
	}
	
	public static final Intent getIntent(Context context) {
		return new Intent(context, Send.class);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_activity);
        trackSendActivity();
        
        packageName = (EditText)findViewById(R.id.package_name);
        campaignSource = (EditText)findViewById(R.id.campaign_source);
        campaignMedium = (EditText)findViewById(R.id.campaign_medium);
        campaignTerm = (EditText)findViewById(R.id.campaign_term);
        campaignContent = (EditText)findViewById(R.id.campaign_content);
        campaignName = (EditText)findViewById(R.id.campaign_name);
        
        String referralUrl = getReferralUrl(getIntent());
    	if(!TextUtils.isEmpty(referralUrl)) {
    		String referrer = getReferrer(referralUrl);
    		properties = getMap(referrer);
    		String id = getId(referralUrl);
    		if(!TextUtils.isEmpty(id)) {
    			properties.put("id", id);
    		}
    		setValue(packageName, "id");
    		setValue(campaignSource, "utm_source");
    		setValue(campaignMedium, "utm_medium");
    		setValue(campaignTerm, "utm_term");
    		setValue(campaignContent, "utm_content");
    		setValue(campaignName, "utm_campaign");
    	}
    	
    	send = (Button)findViewById(R.id.send);
    	send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendBroadcastIntent();
			}
		});
    	
    }
    
    private void sendBroadcastIntent() {
    	Intent i = new Intent("com.android.vending.INSTALL_REFERRER");
    	String id = getId();
    	if(checkId(id)) {
    		return;
    	}
    	i.setPackage(id);
    	String referrer = generateReferrer();
    	if(checkfield(referrer, "utm_source")) {
    		return;
    	}
    	if(checkfield(referrer, "utm_medium")) {
    		return;
    	}
    	if(checkfield(referrer, "utm_campaign")) {
    		return;
    	}
    	i.putExtra("referrer", referrer);
    	sendBroadcast(i);
    }
    
    private boolean checkfield(String referrer, String field) {
    	if(!referrer.contains(field)) {
    		Toast.makeText(this, "The field " + field + " is mandatory", Toast.LENGTH_LONG).show();
    		return true;
    	}
    	return false;
    }
    
    private boolean checkId(String id) {
    	if(TextUtils.isEmpty(id)) {
    		Toast.makeText(this, "The field package name is mandatory", Toast.LENGTH_LONG).show();
    		return true;
    	}
    	return false;
    }
    
    private String getId() {
    	return packageName.getText().toString();
    }
    
    private String generateReferrer() {
    	StringBuilder sb = new StringBuilder();
    	
    	append(sb, campaignSource, "utm_source");
    	sb.append("&");
    	append(sb, campaignMedium, "utm_medium");
    	sb.append("&");
    	if(append(sb, campaignTerm, "utm_term")) {    		
    		sb.append("&");
    	}
    	if(append(sb, campaignContent, "utm_content")) {
    		sb.append("&");
    	}
    	append(sb, campaignName, "utm_campaign");
		return sb.toString();
	}
    
    private boolean append(StringBuilder sb, EditText edit, String parameter) {
    	String text = edit.getText().toString();
    	if(!TextUtils.isEmpty(text)) {
    		sb.append(parameter).append("=").append(text);
    		return true;
    	}
    	return false;
    }

	private void setValue(EditText t, String key) {
    	if(properties.containsKey(key)) {
			t.setText(properties.get(key));
		}	
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    }
    
    private Map<String, String> getMap(String paramString) {
		String[] arrayOfString1 = paramString.split("&");
		HashMap<String, String> localHashMap = new HashMap<String, String>();
		for (String pair : arrayOfString1) {
			String[] entry = pair.split("=");
			if (entry.length != 2)
				break;
			localHashMap.put(entry[0], entry[1]);
		}
		return localHashMap;
	}

    private String getReferrer(String url) {
		try {
			URI uri = new URI(url);
			String referrer = uri.getQuery().split("referrer=")[1];
			return referrer;
		} catch (URISyntaxException e) {
			return null;
		}
	}
    
	private String getId(String url) {
		try {
			URI uri = new URI(url);
			String query = uri.getQuery();
			
			String[] arrayOfString1 = query.split("&");
			String id = arrayOfString1[0].split("=")[1];
			
			return id;
		} catch (Exception e) {
			return null;
		}
	}
    
}