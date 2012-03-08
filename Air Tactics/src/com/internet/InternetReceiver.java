package com.internet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class InternetReceiver extends BroadcastReceiver {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals("new")) {
	        	intent.getData();
	        	//XMPPClient.handler.postDelayed(XMPPClient.r, 1000);
	        	//context
	        //Do stuff - maybe update my view based on the changed DB contents
	    }
	}
}
