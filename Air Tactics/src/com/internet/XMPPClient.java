package com.internet;

import java.util.ArrayList;

import airtactics.com.R;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.airtactics.Air;

public class XMPPClient extends Activity {

	public static String SERVER = "tacticsgames.dyndns.org";
	public static int PORT = 5222;
	public static String INTERNET_OPPONENT = "internet opponent";
    private ArrayList<String> messages = new ArrayList<String>();
    private Handler mHandler = new Handler();
    private EditText mRecipient;
    private EditText mSendText;
    private ListView mList;
    SharedPreferences mPrefs;
    
    private BroadcastReceiver mInternetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	String from, text;
        	from = "";
        	text = "";
        	if (intent.getExtras() != null)
        	{
        		if (intent.hasExtra(XMPPService.SENDER))
        		{
        			from = intent.getStringExtra(XMPPService.SENDER);
        		}
        		if (intent.hasExtra(XMPPService.NEW_MESSAGE))
        		{
        			text = intent.getStringExtra(XMPPService.NEW_MESSAGE);
        		}
        	}
        	addToList(from + " : " + text);    
        	XMPPService.getInstance().sendMessage(from, text);
        }
    };

    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.i("XMPPClient", "onCreate called");
        setContentView(R.layout.xmpp_main);
        firstRunPreferences();
        XMPPService.getInstance().login(getUsernamePref(), getPasswordPref());
        XMPPService.getInstance().joinChat("test");

        mRecipient = (EditText) this.findViewById(R.id.recipient);
        Log.i("XMPPClient", "mRecipient = " + mRecipient);
        mSendText = (EditText) this.findViewById(R.id.sendText);
        Log.i("XMPPClient", "mSendText = " + mSendText);
        mList = (ListView) this.findViewById(R.id.listMessages);
        Log.i("XMPPClient", "mList = " + mList);
        setListAdapter();

        // Set a listener to send a chat text message
        Button send = (Button) this.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startGame(mRecipient.getText().toString());
            }
        });
       
        //startService(new Intent(this, XMPPService.class));
    }
    
    /**
	 * setting up preferences storage
	 */
	 public void firstRunPreferences() {
	    Context mContext = this.getApplicationContext();
	    mPrefs = mContext.getSharedPreferences("myAppPrefs", 0); //0 = mode private. only this app can read these preferences
	 }
	 public String getUsernamePref() {
		    return mPrefs.getString("username", "");
		 }
	 
	 public String getPasswordPref() {
		    return mPrefs.getString("password", "");
		 }
    
    private void startGame(String opponent)
    {
    	Intent intent = new Intent(getBaseContext(), Air.class);
		intent.putExtra("AI", "1");
		intent.putExtra(INTERNET_OPPONENT, opponent + XMPPService.EMAIL_ADDRESS);
		startActivity(intent);
    }
    
    public void addToList(String msg)
    {
    	//messages.add(fromName + ":");
        messages.add(msg);
        // Add the incoming message to the list view
        mHandler.post(new Runnable() {
            public void run() {
                setListAdapter();
            }
        });
    }

    private void setListAdapter
            () {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.multi_line_list_item,
                messages);
        mList.setAdapter(adapter);
    }
    
    @Override
    public void onResume() 
    {
    	super.onResume();
    	if (mInternetReceiver == null) mInternetReceiver = new InternetReceiver();
    	IntentFilter intentFilter = new IntentFilter("new");
    	registerReceiver(mInternetReceiver, intentFilter);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	if (mInternetReceiver != null) unregisterReceiver(mInternetReceiver);
    }
    
    @Override
	protected void onDestroy()
	{
    	super.onDestroy();
    	stopService(new Intent(this, XMPPService.class));
    	//connection.disconnect();
	}
}
