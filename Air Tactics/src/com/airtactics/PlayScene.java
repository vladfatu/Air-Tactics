package com.airtactics;


import airtactics.com.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.internet.XMPPService;
import com.internet.ui.CreateUser;
import com.internet.ui.GameRoom;
import com.scoreloop.client.android.ui.EntryScreenActivity;
import com.users.User;


public class PlayScene extends Activity implements OnClickListener{
	
	ProgressDialog progressDialog;

		public static final int SINGLE_PLAYER = 1;
		public static final int MULTI_PLAYER = 2;
		public static final int INTERNET_MULTI_PLAYER = 3;
		public static int GAME_TYPE;
		AdView adView;
		AlertDialog alert;
		Boolean firstRun;
		private LinearLayout linearLayout;
		
		//Sprite background, singleButton,slButton, multiButton, internetMultiButton;
		Button singleButton, multiButton, internetMultiButton;
		ImageButton slButton;
		int currentX, currentY, AI;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			firstRunPreferences();
			if (getFirstRun())
			{
				setRunned();
				firstRun = true;
			}
			else firstRun = false;
			requestWindowFeature(Window.FEATURE_NO_TITLE); 
	        updateFullscreenStatus(true);
			/*ScreenDisplay.playPanel = new Panel(this);
			adView = new AdView(this, AdSize.BANNER, "a14de914472599e"); 
			FrameLayout layout = new FrameLayout(this);
		    FrameLayout.LayoutParams gameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
		            FrameLayout.LayoutParams.FILL_PARENT);
		    FrameLayout.LayoutParams adsParams =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, 
		            FrameLayout.LayoutParams.WRAP_CONTENT, android.view.Gravity.BOTTOM); 
		    layout.addView(ScreenDisplay.playPanel, gameParams);
		    layout.addView(adView, adsParams); 
		    setContentView(layout);
		    
		    Display display = getWindowManager().getDefaultDisplay(); 
			ScreenDisplay.screenWidth = display.getWidth();
			ScreenDisplay.screenHeight = display.getHeight();
			ScreenDisplay.setDensity();
		    
		    
		    
			//setContentView(ScreenDisplay.playPanel);
			
			init();*/
	        
	        adView = new AdView(this, AdSize.BANNER, "a14de914472599e"); 
			/*FrameLayout layout = new FrameLayout(this);
		    FrameLayout.LayoutParams gameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
		            FrameLayout.LayoutParams.FILL_PARENT);
		    FrameLayout.LayoutParams adsParams =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, 
		            FrameLayout.LayoutParams.WRAP_CONTENT, android.view.Gravity.BOTTOM); 
		    linearLayout = (LinearLayout) findViewById(R.id.play_scene_layout);
		    layout.addView(linearLayout, gameParams);
		    layout.addView(adView, adsParams); 
		    setContentView(layout);*/
	        setContentView(R.layout.play_scene);
	        linearLayout = (LinearLayout) findViewById(R.id.add_layout);
	        linearLayout.addView(adView);
	        
	        AdRequest request = new AdRequest();
		    //request.addTestDevice("CF95DC53F383F9A836FD749F3EF439CD");
		    //request.setTesting(true);
		    adView.loadAd(request);
	        
	        singleButton = (Button) findViewById(R.id.singleButton);
	        singleButton.setOnClickListener(this);
	        
	        multiButton = (Button) findViewById(R.id.multiButton);
	        multiButton.setOnClickListener(this);
	        
	        internetMultiButton = (Button) findViewById(R.id.internetButton);
	        internetMultiButton.setOnClickListener(this);
	        
	        slButton = (ImageButton) findViewById(R.id.slButton);
	        slButton.setOnClickListener(this);
	        
	        
		}
		/*
		public void init()
	    {
			background = new Sprite(getResources(), R.drawable.background, ScreenDisplay.playPanel);
			background.setPosition(background.width/2, background.height/2);
			ScreenDisplay.playPanel.addSprite(background);
			singleButton = new Sprite(getResources(), R.drawable.single_button, ScreenDisplay.playPanel);
			singleButton.setPosition(160,240);
			ScreenDisplay.playPanel.addSprite(singleButton);
			multiButton = new Sprite(getResources(), R.drawable.multi_button, ScreenDisplay.playPanel);
			multiButton.setPosition(160,300);
			ScreenDisplay.playPanel.addSprite(internetMultiButton);
			internetMultiButton = new Sprite(getResources(), R.drawable.multi_button, ScreenDisplay.playPanel);
			internetMultiButton.setPosition(160,360);
			ScreenDisplay.playPanel.addSprite(internetMultiButton);
			slButton = new Sprite(getResources(), R.drawable.sl_button, ScreenDisplay.playPanel);
			slButton.setPosition(160,420);
			ScreenDisplay.playPanel.addSprite(slButton);
	    }
		*/
		
		
		/**
		 * get if this is the first run
		 *
		 * @return returns true, if this is the first run
		 */
		    public boolean getFirstRun() {
		    return mPrefs.getBoolean("firstRun", true);
		 }
		 
		 /**
		 * store the first run
		 */
		 public void setRunned() {
		    SharedPreferences.Editor edit = mPrefs.edit();
		    edit.putBoolean("firstRun", false);
		    edit.commit();
		 }
		 
		 public String getUsernamePref() {
			    return mPrefs.getString("username", "");
			 }
		 
		 public String getPasswordPref() {
			    return mPrefs.getString("password", "");
			 }
		 
		 SharedPreferences mPrefs;
		 
		 /**
		 * setting up preferences storage
		 */
		 public void firstRunPreferences() {
		    Context mContext = this.getApplicationContext();
		    mPrefs = mContext.getSharedPreferences("myAppPrefs", 0); //0 = mode private. only this app can read these preferences
		 }
		
		
		
		
		

		public void alertReceived()
		{
	    	
	    	final CharSequence[] items = {getResources().getString(R.string.good), 
	    						getResources().getString(R.string.very_good)};

	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setTitle(getResources().getString(R.string.what_is_your_level));
	    	builder.setItems(items, new DialogInterface.OnClickListener() {
	    	    public void onClick(DialogInterface dialog, int item) {
	    	        
	    	    	switch (item)
	    	    	{
		    	    	case 0:
		    	    	{
		        			AI = 1;
		        			Intent intent = new Intent(getBaseContext(), Air.class);
		        			intent.putExtra("AI", Integer.toString(AI));
		        			startActivity(intent);
		        			break;
		    	    	}
		    	    	case 1:
		    	    	{
		    	    		AI = 2;	
		    	    		Intent intent = new Intent(getBaseContext(), Air.class);
		    				intent.putExtra("AI", Integer.toString(AI));
		    				startActivity(intent);
		    	    	}
	    	    	}
	    	    	
	    	    }
	    	});
	    	AlertDialog alert = builder.create();
	    	alert.show();
		}
		
		
		private void singleScene() {
			GAME_TYPE = SINGLE_PLAYER;
			AirOpponent.firstTime = true;
			AirOpponent.selectedI = -1;
			AirOpponent.selectedJ = -1;
			alertReceived();
			
			//finish();
			
		}
		
		private void multiScene()
		{
			GAME_TYPE = MULTI_PLAYER;
			AirOpponent.firstTime = true;
			AirOpponent.selectedI = -1;
			AirOpponent.selectedJ = -1;
			AI = 1;	
    		Intent intent = new Intent(getBaseContext(), Air.class);
			intent.putExtra("AI", Integer.toString(AI));
			startActivity(intent);
		}
		
		private void internetScene()
		{
			GAME_TYPE = INTERNET_MULTI_PLAYER;
			AirOpponent.firstTime = true;
			AirOpponent.selectedI = -1;
			AirOpponent.selectedJ = -1;
			new User();
			
			new AsyncTask<Void, Void, Void>() {

				@Override
				protected void onPreExecute() {
					
					progressDialog = ProgressDialog.show(PlayScene.this, 
							getResources().getString(R.string.please_wait),
							getResources().getString(R.string.connecting),
							true);
					super.onPreExecute();
				}

				@Override
				protected void onPostExecute(Void result) {
					
					progressDialog.dismiss();
					if (XMPPService.getInstance() != null && XMPPService.getInstance().isConnected())
					{
						Intent gameIntent;
						
						if (getUsernamePref().length() != 0 && getPasswordPref().length() != 0)
						{
							
							User.getInstance().setUser(getUsernamePref(), getPasswordPref());
							gameIntent = new Intent(getBaseContext(), GameRoom.class);
						}
						else
						{
							gameIntent = new Intent(getBaseContext(), CreateUser.class);
						}
						startActivity(gameIntent);
					}
					else
					{
						Toast.makeText(PlayScene.this, R.string.server_offline, Toast.LENGTH_SHORT).show();
						stopService(new Intent(PlayScene.this, XMPPService.class));
					}
					
					super.onPostExecute(result);
				}

				@Override
				protected Void doInBackground(Void... params) {
					startService(new Intent(PlayScene.this, XMPPService.class));
					return null;
				}
			}.execute();
			
			
		}
		
		private void scoreloopScreen()
		{
			startActivity(new Intent(getBaseContext(), EntryScreenActivity.class));
		}
		
		public void helpDialog()
		{      
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Help");
        	builder.setMessage(R.string.help_text)
        	       .setCancelable(false)
        	       .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
        	           public void onClick(DialogInterface dialog, int id) {
        	                singleScene();
        	                alert.cancel();
        	           }
        	       });
            alert = builder.create();
        	alert.show();
		}
		/*
		public boolean onTouchEvent(MotionEvent event) 
		{
	        int action = event.getAction();
	        currentX = (int)event.getX(0); 
	        currentY = (int)event.getY(0); 
	        //MotionEvent.
	        switch(action & MotionEvent.ACTION_MASK)
	        {
	        case MotionEvent.ACTION_DOWN:
	        {
	        	if (singleButton.touched(currentX, currentY))
	        	{
	        		if (firstRun) 
	        		{
	        			helpDialog();
	        			firstRun = false;
	        		}
	        		else singleScene();
	        	}
	        	else if (multiButton.touched(currentX, currentY))
	        	{
	        		multiScene();
	        	}
	        	else if (internetMultiButton.touched(currentX, currentY))
	        	{
	        		internetScene();
	        	}
	        	else if (slButton.touched(currentX, currentY))
	        	{
	        		scoreloopScreen();
	        	}
	        	break;
	        }
	        case MotionEvent.ACTION_UP:
	        {
	        	
	        }
	        }
			return true;
	      }
		*/
		private void updateFullscreenStatus(Boolean bUseFullscreen)
		{   
		   if(bUseFullscreen)
		   {
		        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		    }
		    else
		    {
		        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		    }

		    //m_contentView.requestLayout();
		}

		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.singleButton:
				if (firstRun) 
        		{
        			helpDialog();
        			firstRun = false;
        		}
        		else singleScene();
				break;
			case R.id.multiButton:
				multiScene();
				break;
			case R.id.internetButton:
				internetScene();
				break;
			case R.id.slButton:
				scoreloopScreen();
			}
			
		}
}
