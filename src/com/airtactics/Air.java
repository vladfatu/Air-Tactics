package com.airtactics;


import java.util.Set;
import java.util.StringTokenizer;

import airtactics.com.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bluetooth.BluetoothChatService;
import com.bluetooth.DeviceListActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.internet.InternetReceiver;
import com.internet.XMPPClient;
import com.internet.XMPPService;
import com.internet.ui.GameRoom;
import com.scoreloop.client.android.ui.OnScoreSubmitObserver;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;
import com.scoreloop.client.android.ui.ShowResultOverlayActivity;

public class Air extends Activity implements OnScoreSubmitObserver{
	/** Called when the activity is first created. */
	//Panel myPanel;
	private static final int SHOW_RESULT = 0;
	
	public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT2 = 3;
    private static final int REQUEST_ENABLE_BT=3;
	
	public static Boolean opponentsTurn;
	public static double _score;
	public static Point pointToSend;
	public static int planesCrashed;
	public static Boolean gameOver, gameStarted, gameWon, opponentAccepted, disconnected;
	int touched,currentX,currentY,nrOfPlanes,planeRotationType[];
	public static Tile tileMatrix[][];
	public static Opponent op;
	float tempPosX,tempPosY,secondTempPosX,secondTempPosY,startingPosY;
	Set<BluetoothDevice> pairedDevices;
	Sprite tile,background,planes[],oppButton,tempButton, chatButton;
	public static Sprite back2, createButton;
	Boolean planeStatus[];
	BluetoothAdapter mBluetoothAdapter;
	public static BluetoothChatService mChatService = null;
	public static StringBuffer mOutStringBuffer;
	private String mConnectedDeviceName = null;
	private static Boolean createButtonAvailable;
	AlertDialog helpAlert;
	AdView adView;
	Label scoreLabel;
	
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
        			StringTokenizer st = new StringTokenizer(text);
                    String temp;
                    int x=-1, y=-1;
                    if (st.hasMoreElements())
                    {
                    	temp = st.nextToken();
                    	if (temp.equals("/pos"))
                    	{
                    		if (st.hasMoreElements())
                    		{
                    			temp = st.nextToken();
                    			x = Integer.parseInt(temp);
                    		}
                    		if (st.hasMoreElements())
                    		{
                    			temp = st.nextToken();
                    			y = Integer.parseInt(temp);
                    		}
                    		if (x != -1 && y != -1) 
                    		{
                    			multiShoot(new Point(x , y));
                    			opponentsTurn = false;
                    		}
                    	}
                    	else if (temp.equals("/resp"))
                    	{
                    		if (st.hasMoreElements()) temp = st.nextToken();
                    		setPoint(pointToSend, Integer.parseInt(temp));
                    	}
                    	else if (temp.equals("/start"))
                    	{
                    		opponentAccepted = true;
                    		Toast.makeText(Air.this, "The opponent has set his plane, you can start playing now!", Toast.LENGTH_LONG).show();
                    	}
                    	else if (temp.equals("/disconnect"))
                    	{
                    		disconnected = true;
                    		gameOver = true;
                    		finish();
                    		Toast.makeText(Air.this, "The opponent has left the game!!!", Toast.LENGTH_LONG).show();
                    	}
                    }
        		}
        	} 
        }
    };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	savedInstanceState = null;
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateFullscreenStatus(true);
        opponentAccepted = true;
        if (PlayScene.GAME_TYPE == PlayScene.SINGLE_PLAYER || opponentsTurn == null)
        {
        	opponentsTurn = false;
        }
        if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER)
        {
	        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	        if (mBluetoothAdapter == null || isFinishing()) {
	            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
	            finish();
	            return;
	        }
	        opponentsTurn = true;
	        
            pointToSend = new Point(0, 0);
        }
        ScreenDisplay.inGamePanel = new Panel(this); 
        adView = new AdView(this, AdSize.BANNER, "a14de914472599e");
		FrameLayout layout = new FrameLayout(this);
	    FrameLayout.LayoutParams gameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
	            FrameLayout.LayoutParams.FILL_PARENT);
	    FrameLayout.LayoutParams adsParams =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, 
	            FrameLayout.LayoutParams.WRAP_CONTENT, android.view.Gravity.BOTTOM); 
	    layout.addView(ScreenDisplay.inGamePanel, gameParams);
	    layout.addView(adView, adsParams); 
	    setContentView(layout);
	    
	    AdRequest request = new AdRequest();
	    //request.addTestDevice("CF95DC53F383F9A836FD749F3EF439CD");
	    //request.setTesting(true);
	    adView.loadAd(request);
        //setContentView(Display.inGamePanel);
        touched=-1;
        String AI = getIntent().getStringExtra("AI");
        op = new Opponent (Integer.parseInt(AI));
        gameWon = false;
        gameOver = false;
        init();
        if (PlayScene.GAME_TYPE == PlayScene.INTERNET_MULTI_PLAYER)
        {
        	disconnected = false;
        	if (!opponentsTurn) opponentAccepted = false;
        	if (getIntent().getExtras() != null)
        		if (getIntent().hasExtra(GameRoom.OPPONENT))
        			Opponent.internetName = getIntent().getStringExtra(GameRoom.OPPONENT);
        	pointToSend = new Point(0, 0);
        	//startActivity(new Intent(getBaseContext(), XMPPClient.class));
        }
        //if (bluetoothSupported) checkBluetooth();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        //if(D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER)
        {
	        if (!mBluetoothAdapter.isEnabled()) {
	            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
	        // Otherwise, setup the chat session
	        } else {
	            if (mChatService == null) setupChat();
	        }
	        //if (mBluetoothAdapter.isEnabled())
	        //{
	        //	ensureDiscoverable();
	        //}
        }
    }
    
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                
                switch (msg.arg1) {
                case BluetoothChatService.STATE_CONNECTED:
                    //mTitle.setText(R.string.title_connected_to);
                    //mTitle.append(mConnectedDeviceName);
                    //mConversationArrayAdapter.clear();
                    break;
                case BluetoothChatService.STATE_CONNECTING:
                    //mTitle.setText(R.string.title_connecting);
                    break;
                case BluetoothChatService.STATE_LISTEN:
                case BluetoothChatService.STATE_NONE:
                    //mTitle.setText(R.string.title_not_connected);
                    break;
                }
                break;
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                StringTokenizer st = new StringTokenizer(readMessage);
                String temp;
                int x=-1, y=-1;
                if (st.hasMoreElements())
                {
                	temp = st.nextToken();
                	if (temp.equals("/pos"))
                	{
                		if (st.hasMoreElements())
                		{
                			temp = st.nextToken();
                			x = Integer.parseInt(temp);
                		}
                		if (st.hasMoreElements())
                		{
                			temp = st.nextToken();
                			y = Integer.parseInt(temp);
                		}
                		if (x != -1 && y != -1) 
                		{
                			multiShoot(new Point(x , y));
                			opponentsTurn = false;
                		}
                	}
                	else if (temp.equals("/resp"))
                	{
                		if (st.hasMoreElements()) temp = st.nextToken();
                		setPoint(pointToSend, Integer.parseInt(temp));
                	}
                }
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                Toast.makeText(getApplicationContext(), "Connected to "
                               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                removeCreateButton();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                               Toast.LENGTH_SHORT).show();
                if (msg.getData().getString(TOAST).equals("Device connection was lost")) 
                {
                	finish();
                }
                break;
            }
        }
    };
    
    public void removeCreateButton()
    {
    	gameStarted = false;
        ScreenDisplay.inGamePanel.removeSprite(back2);
        ScreenDisplay.inGamePanel.removeSprite(createButton);
        createButtonAvailable = false;
        ScreenDisplay.inGamePanel.addLabel(scoreLabel);
    }
    
    public void setPoint(Point p, int val)
    {
    	int tempI, tempJ;
    	tempI = p.x;
    	tempJ = p.y;
    	if (val == -1)
		{
			AirOpponent.tileMatrix[tempI][tempJ].s.setImage(getResources(), R.drawable.no_hit);
			AirOpponent.tileMatrix[tempI][tempJ].hit=false;
			AirOpponent.tileMatrix[tempI][tempJ].visible=true;
			AirOpponent.tileMatrix[tempI][tempJ].value=-1;
			//AirOpponent.opponentsTurn();
		}
		else if (val == 1)
		{
			AirOpponent.tileMatrix[tempI][tempJ].s.setImage(getResources(), R.drawable.hit_body);
			AirOpponent.tileMatrix[tempI][tempJ].hit=true;
			AirOpponent.tileMatrix[tempI][tempJ].visible=true;
			//opponentsTurn();
		}
		else if (val == 2)
		{
			AirOpponent.tileMatrix[tempI][tempJ].s.setImage(getResources(), R.drawable.hit_head);
			AirOpponent.tileMatrix[tempI][tempJ].hit=true;
			AirOpponent.tileMatrix[tempI][tempJ].visible=true;
			AirOpponent.planesCrashed++;
			scoreLabel.setText("You " + planesCrashed + "-" + AirOpponent.planesCrashed + " Opp");
			AirOpponent.scoreLabel.setText("You " + planesCrashed + "-" + AirOpponent.planesCrashed + " Opp");
			if (AirOpponent.planesCrashed >= 3) 
			{
				gameOver = true;
				finishActivity(2);
				//AirOpponent.alertReceived();
				//alertReceivedWon();
			}
			//else opponentsTurn();
		}
    	if (!gameOver)
    	{
	    	AirOpponent.tileMatrix[tempI][tempJ].s.setPosition(25 + 30*tempJ, 55 + 30*tempI);
			ScreenDisplay.inGamePanel2.addSprite(AirOpponent.tileMatrix[tempI][tempJ].s);
    	}
    }
    
    private void setupChat() {
        
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }
    
    private void ensureDiscoverable() {
        
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
    
    public static void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            //Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return; 
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }
    
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BLuetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                connectDevice(data, false);
            }
            break;
        case REQUEST_ENABLE_BT2:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occured
                Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    public void init() 
    { 
    	startingPosY = 100;
    	planesCrashed = 0;
    	AirOpponent.planesCrashed = 0;
    	createButtonAvailable = false;
    	_score = 0;
    	gameOver = false;
    	gameStarted = false;
    	
    	scoreLabel = new Label("You 0-0 Opp", 25, ScreenDisplay.inGamePanel, Color.WHITE);
        scoreLabel.setPosition(160,25);
        if (PlayScene.GAME_TYPE != PlayScene.MULTI_PLAYER)
        {
        	ScreenDisplay.inGamePanel.addLabel(scoreLabel);
        }
    	
    	tile = new Sprite(getResources(), R.drawable.grid, ScreenDisplay.inGamePanel);
        background = new Sprite(getResources(), R.drawable.background, ScreenDisplay.inGamePanel);
        background.setPosition(background.width/2, background.height/2);
    	tile.setPosition(160, 190);
    	ScreenDisplay.inGamePanel.addSprite(background);
        ScreenDisplay.inGamePanel.addSprite(tile);
        nrOfPlanes = 3;
    	planes = new Sprite[nrOfPlanes];
    	planeStatus = new Boolean[nrOfPlanes];
    	planeRotationType = new int[nrOfPlanes];
    	tileMatrix = new Tile[10][];
    	for (int i=0;i<10;i++)
    		tileMatrix[i]=new Tile[10];
    	
    	for (int i=0;i<10;i++)
    	{
    		for (int j=0;j<10;j++)
    			tileMatrix[i][j] = new Tile(i,j);
    	}
    	
    	oppButton = new Sprite(getResources(), R.drawable.opp_button, ScreenDisplay.inGamePanel);
        oppButton.setPosition(290, 20);
        ScreenDisplay.inGamePanel.addSprite(oppButton);
        
        /*if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER)
        {
	        chatButton = new Sprite(getResources(), R.drawable.chat_button, ScreenDisplay.inGamePanel);
	        chatButton.setPosition(160, 20);
	        ScreenDisplay.inGamePanel.addSprite(chatButton);
        }*/
	        
        //tileMatrix[1][1].s = new Sprite(getResources(), R.drawable.selected, Display.inGamePanel);
        //tempButton = new Sprite(getResources(), R.drawable.no_hit, Display.inGamePanel);
        //tileMatrix[1][1].s.setPosition(25 + 30*1, 55 + 30*1);
        //Display.inGamePanel.addSprite(tileMatrix[1][1].s);
        
    	
        for (int i=0;i<nrOfPlanes;i++)
        {
    		planes[i] = new Sprite(getResources(), R.drawable.plane, ScreenDisplay.inGamePanel);
    		planes[i].setPosition(55 + i*90, startingPosY + i*30);
    		ScreenDisplay.inGamePanel.addSprite(planes[i]);
    		planeStatus[i]=true;
    		planeRotationType[i]=0;
        }
		
        //here I initialize the matrix values for the planes
        
        tileMatrix[1][0].value = 2;
        tileMatrix[0][1].value = 1;
        tileMatrix[1][1].value = 1;
        tileMatrix[2][1].value = 1;
        tileMatrix[1][2].value = 1;
        tileMatrix[0][3].value = 1;
        tileMatrix[1][3].value = 1;
        tileMatrix[2][3].value = 1;
        
        tileMatrix[4][1].value = 2;
        tileMatrix[3][2].value = 1;
        tileMatrix[4][2].value = 1;
        tileMatrix[5][2].value = 1;
        tileMatrix[4][3].value = 1;
        tileMatrix[3][4].value = 1;
        tileMatrix[4][4].value = 1;
        tileMatrix[5][4].value = 1;
        
        tileMatrix[7][2].value = 2;
        tileMatrix[6][3].value = 1;
        tileMatrix[7][3].value = 1;
        tileMatrix[8][3].value = 1;
        tileMatrix[7][4].value = 1;
        tileMatrix[6][5].value = 1;
        tileMatrix[7][5].value = 1;
        tileMatrix[8][5].value = 1;
        //planes[0].rotate(90);
        if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER)
        {
        	back2 = new Sprite(getResources(), R.drawable.background, ScreenDisplay.inGamePanel);
	        back2.setPosition(back2.width/2, back2.height/2);
	        ScreenDisplay.inGamePanel.addSprite(back2);
	        
	        createButton = new Sprite(getResources(), R.drawable.create_button, ScreenDisplay.inGamePanel);
	        createButton.setPosition(160, 200);
	        ScreenDisplay.inGamePanel.addSprite(createButton);
	        gameStarted = true;
	        createButtonAvailable = true;
        }
        
    }
    
    public void alertReceived()
	{
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("You have lost! Better luck next time!")
    	       .setCancelable(false)
    	       .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                finish();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	if (!this.isFinishing())
    	{
    		alert.show();
    	}
    	else finish();
	}
    
    public void alertReceivedWon()
	{
    	
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage("Congratulations !!! You crushed your opponent!")
    	       .setCancelable(false)
    	       .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                finish();
    	           }
    	       });
    	AlertDialog alert = builder.create();
    	if (!isFinishing()) 
    	{
    		alert.show();
    	}
    	else finish();
	}
    
    public void startGame()
    {
    	
    }
    
    public Boolean checkPlane(int i)
    {
    	Boolean check;
    	check = false;
    	int x,y,temp1=0,temp2=0;
    	if (i%2==0) temp1 = 1;
    	else temp2 = 1;
		y = (int)(planes[touched].getTop() - (tile.getTop()-tile.height/2))/30 - temp1;
		x = (int)(planes[touched].getLeft() - (tile.getLeft()-tile.width/2))/30 - temp2;
    	switch(i)
		{
			case 0:
			{
				if (tileMatrix[x][y].value==0 && tileMatrix[x-1][y].value==0 && tileMatrix[x+1][y].value==0 
						&& tileMatrix[x][y+1].value==0 && tileMatrix[x][y+2].value==0
						&& tileMatrix[x-1][y+2].value==0 && tileMatrix[x+1][y+2].value==0
						&& tileMatrix[x][y-1].value==0) 
				{	
					check =true;
				}
				break;
			}
			case 1:
			{
				if (tileMatrix[x-1][y-1].value==0 && tileMatrix[x+1][y-1].value==0 && tileMatrix[x][y].value==0 
						&& tileMatrix[x-1][y].value==0 && tileMatrix[x+1][y].value==0 && tileMatrix[x-1][y+1].value==0 
						&& tileMatrix[x+1][y+1].value==0 && tileMatrix[x+2][y].value==0) 
				{
					check = true;
				}
				break;
			}
			case 2:
			{
				if (tileMatrix[x][y-1].value==0 && tileMatrix[x-1][y-1].value==0 && tileMatrix[x+1][y-1].value==0  
						&& tileMatrix[x][y].value==0 && tileMatrix[x][y+1].value==0 && tileMatrix[x-1][y+1].value==0 
						&& tileMatrix[x+1][y+1].value==0 && tileMatrix[x][y+2].value==0 ) 
				{
					check =true;
				}
				break;
			}
			case 3:
			{
				if (tileMatrix[x][y-1].value==0 && tileMatrix[x+2][y-1].value==0 && tileMatrix[x][y].value==0  
						&& tileMatrix[x+1][y].value==0 && tileMatrix[x+2][y].value==0 && tileMatrix[x][y+1].value==0 
						&& tileMatrix[x+2][y+1].value==0 && tileMatrix[x-1][y].value==0) 
				{
					check = true;
				}
			}
			
		}
    	return check;
    }
    
    public void setPlane(int i)
    {
    	int y,x,temp1=0,temp2=0;
    	if (i%2==0) temp1 = 1;
    	else temp2 = 1;
		y = (int)(planes[touched].getTop() - (tile.getTop()-tile.height/2))/30 - temp1;
		x = (int)(planes[touched].getLeft() - (tile.getLeft()-tile.width/2))/30 - temp2;
    	switch(i)
		{
			case 0:
			{
				if (tileMatrix[x][y].value==0 && tileMatrix[x-1][y].value==0 && tileMatrix[x+1][y].value==0 
						&& tileMatrix[x][y+1].value==0 && tileMatrix[x][y+2].value==0
						&& tileMatrix[x-1][y+2].value==0 && tileMatrix[x+1][y+2].value==0
						&& tileMatrix[x][y-1].value==0) 
				{	
					tileMatrix[x][y].value=1; 
					tileMatrix[x-1][y].value=1;
					tileMatrix[x+1][y].value=1;
					tileMatrix[x][y+1].value=1;
					tileMatrix[x][y+2].value=1;
					tileMatrix[x-1][y+2].value=1;
					tileMatrix[x+1][y+2].value=1;
					tileMatrix[x][y-1].value=2;
				}
				else
				{
					planeStatus[touched] = false;
					
				}
				break;
			}
			case 1:
			{
				if (tileMatrix[x-1][y-1].value==0 && tileMatrix[x+1][y-1].value==0 && tileMatrix[x][y].value==0 
						&& tileMatrix[x-1][y].value==0 && tileMatrix[x+1][y].value==0 && tileMatrix[x-1][y+1].value==0 
						&& tileMatrix[x+1][y+1].value==0 && tileMatrix[x+2][y].value==0) 
				{
					tileMatrix[x-1][y-1].value=1;
					tileMatrix[x+1][y-1].value=1;
					tileMatrix[x][y].value=1;
					tileMatrix[x-1][y].value=1;
					tileMatrix[x+1][y].value=1;
					tileMatrix[x-1][y+1].value=1;
					tileMatrix[x+1][y+1].value=1;
					tileMatrix[x+2][y].value=2;
				}
				 
				else
				{
					planeStatus[touched] = false;
					
				}
				break;
			}
			case 2:
			{
				if (tileMatrix[x][y-1].value==0 && tileMatrix[x-1][y-1].value==0 && tileMatrix[x+1][y-1].value==0  
						&& tileMatrix[x][y].value==0 && tileMatrix[x][y+1].value==0 && tileMatrix[x-1][y+1].value==0 
						&& tileMatrix[x+1][y+1].value==0 && tileMatrix[x][y+2].value==0 ) 
				{
					tileMatrix[x][y-1].value=1;
					tileMatrix[x-1][y-1].value=1;
					tileMatrix[x+1][y-1].value=1;
					tileMatrix[x][y].value=1;
					tileMatrix[x][y+1].value=1;
					tileMatrix[x-1][y+1].value=1;
					tileMatrix[x+1][y+1].value=1;
					tileMatrix[x][y+2].value=2;
					
				} 
				else
				{
					planeStatus[touched] = false;
					
				}
				break;
			}
			case 3:
			{
				if (tileMatrix[x][y-1].value==0 && tileMatrix[x+2][y-1].value==0 && tileMatrix[x][y].value==0  
						&& tileMatrix[x+1][y].value==0 && tileMatrix[x+2][y].value==0 && tileMatrix[x][y+1].value==0 
						&& tileMatrix[x+2][y+1].value==0 && tileMatrix[x-1][y].value==0) 
				{
					tileMatrix[x][y-1].value=1;
					tileMatrix[x+2][y-1].value=1;
					tileMatrix[x][y].value=1;
					tileMatrix[x+1][y].value=1;
					tileMatrix[x+2][y].value=1;
					tileMatrix[x][y+1].value=1;
					tileMatrix[x+2][y+1].value=1;
					tileMatrix[x-1][y].value=2;
					
				} 
				else
				{
					planeStatus[touched] = false;
					
				}
			}
			
		}
    }
    private void opponentScreen()
	{
		//startActivity(new Intent(getBaseContext(), AirOpponent.class));
		if (PlayScene.GAME_TYPE == PlayScene.INTERNET_MULTI_PLAYER ) 
			startActivity(new Intent(this, AirOpponent.class));
		else startActivityForResult(new Intent(getBaseContext(), AirOpponent.class), 2);
	}
    public void resetPlane(int i)
    {
    	int y,x,temp1=0,temp2=0;
    	if (i%2==0) temp1 = 1;
    	else temp2 = 1;
		y = (int)(planes[touched].getTop() - (tile.getTop()-tile.height/2))/30 - temp1;
		x = (int)(planes[touched].getLeft() - (tile.getLeft()-tile.width/2))/30 - temp2;
    	switch(i)
		{
    	
			case 0:
			{
				tileMatrix[x][y].value=0;             				
				tileMatrix[x-1][y].value=0;           				
				tileMatrix[x+1][y].value=0;           				
				tileMatrix[x][y+1].value=0;           				
				tileMatrix[x][y+2].value=0;            				
				tileMatrix[x-1][y+2].value=0;           				
				tileMatrix[x+1][y+2].value=0;           				
				tileMatrix[x][y-1].value=0;          				
				break;
			}
			case 1:
			{
				tileMatrix[x-1][y-1].value=0;          				
				tileMatrix[x+1][y-1].value=0;           				
				tileMatrix[x][y].value=0;
				tileMatrix[x-1][y].value=0;           				
				tileMatrix[x+1][y].value=0;           				
				tileMatrix[x-1][y+1].value=0;           				
				tileMatrix[x+1][y+1].value=0;           				
				tileMatrix[x+2][y].value=0;          				
				break;
			}
			case 2:
			{
				tileMatrix[x][y-1].value=0;          				
				tileMatrix[x-1][y-1].value=0;            				
				tileMatrix[x+1][y-1].value=0;            				
				tileMatrix[x][y].value=0;            				
				tileMatrix[x][y+1].value=0;            				
				tileMatrix[x-1][y+1].value=0;            				
				tileMatrix[x+1][y+1].value=0;            				
				tileMatrix[x][y+2].value=0;           				
				break;
			}
			case 3:
			{
				tileMatrix[x][y-1].value=0;            				
				tileMatrix[x+2][y-1].value=0;           				
				tileMatrix[x][y].value=0;           				
				tileMatrix[x+1][y].value=0;            				
				tileMatrix[x+2][y].value=0;
				tileMatrix[x][y+1].value=0;
				tileMatrix[x+2][y+1].value=0;
				tileMatrix[x-1][y].value=0;
			}	
		}
    }
    
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float tempX=160,tempY=240;
        currentX = (int)event.getX(0); 
        currentY = (int)event.getY(0) ; 
        //MotionEvent.
        switch(action & MotionEvent.ACTION_MASK)
        {
        case MotionEvent.ACTION_DOWN: 
        { 
        	if (gameOver) finish();
        	else if (createButtonAvailable && createButton.touched(currentX, currentY))
        	{
        		Intent serverIntent = null;
        		opponentsTurn = false;
    	        serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
        		//removeCreateButton();
        	}
        	else if (!createButtonAvailable && oppButton.touched(currentX, currentY))
        	{
        		if (!gameStarted) 
        		{
        			if (opponentsTurn && PlayScene.GAME_TYPE == PlayScene.INTERNET_MULTI_PLAYER) 
        				XMPPService.getInstance().sendMessage(Opponent.internetName, "/start");
        			Toast.makeText(getBaseContext(), "The game has started! You will no longer be able to move you planes!", Toast.LENGTH_SHORT).show();
        			gameStarted = true;
        		}
        		opponentScreen();
        	}
        	/*else if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER && chatButton.touched(currentX, currentY))
        	{
        		startActivity(new Intent(getBaseContext(), BluetoothChat.class));
        	}*/
        	else if (!gameStarted)
        	{
        	for (int i=0;i<nrOfPlanes;i++)
        	{
        		if (currentX>planes[i].getLeft()-planes[i].width/2 && 
        				currentX<planes[i].getLeft()+planes[i].width/2 && 
        				currentY>planes[i].getTop()-planes[i].height/2 && 
        				currentY<planes[i].getTop()+planes[i].height/2)
        		{
        			touched = i;
        			tempPosX = planes[touched].getLeft();
        			tempPosY = planes[touched].getTop();
        			//int y,x;
        			if (planeStatus[touched])
        			{
            		//y = (int)(planes[touched].getTop() - (tile.getTop()-tile.height/2))/30 - 1;
            		//x = (int)(planes[touched].getLeft() - (tile.getLeft()-tile.width/2))/30 ;
            		resetPlane(planeRotationType[touched]);
        			}
        		}
        	}
        	}
        break;
        }
        case MotionEvent.ACTION_POINTER_DOWN:
        {
        	if (touched!=-1)
        	{
        		secondTempPosX = event.getX(1); 
        		secondTempPosY = event.getY(1);
        	}
        	break;
        }
        case MotionEvent.ACTION_POINTER_UP:
        {
        	if (touched!=-1)
        	{
        		if (event.getY(1)>secondTempPosY+5)
        		{
        			planes[touched].rotate(90);
        			if (planeRotationType[touched]!=3) planeRotationType[touched]++;
        			else planeRotationType[touched] = 0;
        		}
        	}
        	break;
        }
        case MotionEvent.ACTION_MOVE:
        { 
        	if (touched!=-1) 
        		{
        		
        			if (currentY-55<=tile.getTop()+tile.height/2)
        			{
        				int tempW=0,tempH=0;
        				if (planeRotationType[touched]%2 == 0)
        					tempW=15;
        				else tempH=15;
        				planeStatus[touched] = true;
        				if (currentY-100-planes[touched].height/2 > tile.getTop()-tile.height/2)
        					tempY = ((int)(((currentY-100)-(tile.getTop()-tile.height/2))/30))*30 + tempH + (tile.getTop()-tile.height/2);
        				else
        					tempY = tile.getTop()-tile.height/2 + planes[touched].height/2;
        				
        				if (currentX-planes[touched].width/2 > tile.getLeft()-tile.width/2 && currentX+planes[touched].width/2 < tile.getLeft()+tile.width/2)
        					tempX = ((int)((currentX-(tile.getLeft()-tile.width/2))/30)+1)*30 - tempW + (tile.getLeft()-tile.width/2);
        				else if (currentX-planes[touched].width/2 <= tile.getLeft()-tile.width/2)
        					tempX = tile.getLeft()-tile.width/2 + planes[touched].width/2;
        				else 
        					tempX = tile.getLeft()+tile.width/2 - planes[touched].width/2;
        			
        			}
        			else
        			{
        				planeStatus[touched] = false;
        				tempX = currentX;
        				tempY = currentY-100;
        			}
        			planes[touched].setPosition(tempX, tempY);
        			if (planeStatus[touched])
        			{
        				planeStatus[touched] = checkPlane(planeRotationType[touched]);
        			}
        				if (planeStatus[touched])
        				{
        					planes[touched].setImage(getResources(), R.drawable.good_plane);
        					//planes[touched].rotate(planeRotationType[touched]*90);
        				}
        				else
        				{
        					planes[touched].setImage(getResources(), R.drawable.redplane);
        				}
        				
        		}
        	break;
        }
        case MotionEvent.ACTION_UP:
        { 
        	if (touched!=-1) 
        	{
        		
        		
        		if (planeStatus[touched]) 
        			setPlane(planeRotationType[touched]);
        		else
        			{
        				//resetPlane(planeRotationType[touched]);
        				planes[touched].setPosition(tempPosX, tempPosY);
        				if (tempPosY != startingPosY) 
        				{
        					setPlane(planeRotationType[touched]);
        					planeStatus[touched] = true;
        				} 
        				
        			}
        		planes[touched].setImage(getResources(), R.drawable.plane);
        	}
        	touched = -1;
        }
        }
        return true;
    }
    public void checkBluetooth()
    {
    	if (!mBluetoothAdapter.isEnabled()) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    	else
    	{
    		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    		// If there are paired devices
    		if (pairedDevices.size() > 0) 
    		{
    			// Loop through paired devices
    			for (BluetoothDevice device : pairedDevices) 
    			{
    				Log.d("BluetoothTest", "bluetooth enabled");
    			}
    		}
    	}
    }
    
    public void multiShoot(Point p)
    {
    	int x = 0,y = 0;
    	_score++;
    	x = p.x;
    	y = p.y;
    	if (tileMatrix[x][y].value == 0) 
		{
			tileMatrix[x][y].s = new Sprite(getResources(), R.drawable.no_hit, ScreenDisplay.inGamePanel);
			tileMatrix[x][y].hit=false;
			tileMatrix[x][y].visible=true;
			tileMatrix[x][y].value=-1;  
		} 
		else if (tileMatrix[x][y].value == 1)
		{
			tileMatrix[x][y].s = new Sprite(getResources(), R.drawable.hit_body, ScreenDisplay.inGamePanel);
			tileMatrix[x][y].hit=true;
			tileMatrix[x][y].visible=true;
		}
		else if (tileMatrix[x][y].value == 2)
		{
			tileMatrix[x][y].s = new Sprite(getResources(), R.drawable.hit_head, ScreenDisplay.inGamePanel);
			tileMatrix[x][y].hit=true;
			tileMatrix[x][y].visible=true;
			planesCrashed++;
			Toast.makeText(this, "One of your planes has crashed!!!", Toast.LENGTH_LONG).show();
			scoreLabel.setText("You " + planesCrashed + "-" + AirOpponent.planesCrashed + " Opp");
			AirOpponent.scoreLabel.setText("You " + planesCrashed + "-" + AirOpponent.planesCrashed + " Opp");
			if (planesCrashed >= 3) 
			{
				planesCrashed = 0;
				gameOver = true; 
				alertReceived();
			}
		}
    	tileMatrix[x][y].s.setPosition(25 + 30*x, 55 + 30*y);
		ScreenDisplay.inGamePanel.addSprite(tileMatrix[x][y].s);
		if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER)
		{
			sendMessage("/resp " + tileMatrix[x][y].value);
		}
		else if (PlayScene.GAME_TYPE == PlayScene.INTERNET_MULTI_PLAYER)
		{
			XMPPService.getInstance().sendMessage(Opponent.internetName, "/resp " + tileMatrix[x][y].value);
		}
    }
    
    public void opponentsShoot()
    {
    	Point p = new Point();
    	Boolean ok;
    	int x = 0,y = 0;
    	ok = true;
    	//for (int i=0; i<10;i++ )
    	//	for (int j=0;j<10;j++)
    	//{
    	//		ok = true;
    	
    	p = op.shoot();
    	x = p.x;
    	y = p.y;
    	//Toast.makeText(getBaseContext(), p.x + " ," + p.y, Toast.LENGTH_SHORT).show();
    	
    	_score++;
    		
    		if (tileMatrix[x][y].value == 0) 
			{
				tileMatrix[x][y].s = new Sprite(getResources(), R.drawable.no_hit, ScreenDisplay.inGamePanel);
				tileMatrix[x][y].hit=false;
				tileMatrix[x][y].visible=true;
				tileMatrix[x][y].value=-1;  
			} 
			else if (tileMatrix[x][y].value == 1)
			{
				tileMatrix[x][y].s = new Sprite(getResources(), R.drawable.hit_body, ScreenDisplay.inGamePanel);
				tileMatrix[x][y].hit=true;
				tileMatrix[x][y].visible=true;
			}
			else if (tileMatrix[x][y].value == 2)
			{
				tileMatrix[x][y].s = new Sprite(getResources(), R.drawable.hit_head, ScreenDisplay.inGamePanel);
				tileMatrix[x][y].hit=true;
				tileMatrix[x][y].visible=true;
				planesCrashed++;
				Toast.makeText(this, "One of your planes has crashed!!!", Toast.LENGTH_LONG).show();
				scoreLabel.setText("You " + planesCrashed + "-" + AirOpponent.planesCrashed + " Opp");
				if (planesCrashed == 3) alertReceived();
			}
    		Opponent.yourMatrix[x][y] = tileMatrix[x][y].value;
    		tileMatrix[x][y].s.setPosition(25 + 30*x, 55 + 30*y);
			ScreenDisplay.inGamePanel.addSprite(tileMatrix[x][y].s);
			if (tileMatrix[x][y].value == -1 || tileMatrix[x][y].value == 2) 
			{
				if (tileMatrix[x][y].value == 2) op.resetPosiblePlanes(p);
				else op.removePlanesWithPoint(p);
			}
			else if (tileMatrix[x][y].value == 1) op.setPossiblePlanes(p);
    	//}
    }
	public void onScoreSubmit(int status, Exception error) {
		// TODO Auto-generated method stub
		startActivityForResult(new Intent(this, ShowResultOverlayActivity.class), SHOW_RESULT);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		ScoreloopManagerSingleton.get().setOnScoreSubmitObserver(null);
		if (PlayScene.GAME_TYPE == PlayScene.INTERNET_MULTI_PLAYER)
		{
			if (mInternetReceiver != null) unregisterReceiver(mInternetReceiver);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (PlayScene.GAME_TYPE == PlayScene.INTERNET_MULTI_PLAYER)
		{
			if (gameOver)
			{
				alertReceivedWon();
			}
			if (mInternetReceiver == null) mInternetReceiver = new InternetReceiver();
		    IntentFilter intentFilter = new IntentFilter("new");
		    registerReceiver(mInternetReceiver, intentFilter);
		    if (disconnected) finish();
		}
		scoreLabel.setText("You " + planesCrashed + "-" + AirOpponent.planesCrashed + " Opp");
		
		if (gameOver) 
		{
			if (gameWon) ScoreloopManagerSingleton.get().onGamePlayEnded(_score, null);
			if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER) alertReceivedWon();
			else if (PlayScene.GAME_TYPE == PlayScene.INTERNET_MULTI_PLAYER) 
				if (gameWon)alertReceivedWon();
				else alertReceived();
			else finish();
		}
		else if (opponentsTurn) 
		{
			if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER)
			{
				//sendMessage("/pos " + pointToSend.x + pointToSend.y);
			}
			else if (PlayScene.GAME_TYPE == PlayScene.INTERNET_MULTI_PLAYER)
			{
				//sendMessage("/pos " + pointToSend.x + pointToSend.y);
			}
			else 
			{
				opponentsShoot(); 
				opponentsTurn = false;
			}
			
		}
		ScoreloopManagerSingleton.get().setOnScoreSubmitObserver(this);
		if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER)
		{
			if (mChatService != null) {
	            // Only if the state is STATE_NONE, do we know that we haven't started already
	            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
	              // Start the Bluetooth chat services
	              mChatService.stop();
	              mChatService.start();
	            }
	        }
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ScreenDisplay.inGamePanel.removeAll();
		back2 = null;
		createButton = null;
		ScreenDisplay.inGamePanel = null;
		if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER)
		{
			if (mChatService != null) mChatService.stop();
		}
		
		System.gc();
	}
	
	public void helpDialog()
	{      
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help");
    	builder.setMessage(R.string.help_text)
    	       .setCancelable(false)
    	       .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                //singleScene();
    	                helpAlert.cancel();
    	           }
    	       });
    	helpAlert = builder.create();
    	helpAlert.show();
	}
    
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

	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.soundmenu , menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.quit:
        	this.finish();
            //quit();
            return true;
        case R.id.help:
        	helpDialog();
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }



}