
package com.airtactics;


import java.util.Set;
import airtactics.com.R;
import android.view.Menu;
import com.bluetooth.BluetoothChat;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.scoreloop.client.android.ui.OnScoreSubmitObserver;
import com.scoreloop.client.android.ui.ScoreloopManagerSingleton;
import com.scoreloop.client.android.ui.ShowResultOverlayActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

public class AirOpponent extends Activity implements OnScoreSubmitObserver{
	/** Called when the activity is first created. */
	//Panel myPanel;
	private static final int SHOW_RESULT = 0;
	public static int planesCrashed;
	public static int selectedI, selectedJ;
	public static boolean firstTime, shootMode;
	public static double _score;
	public static int touched,currentX,currentY,REQUEST_ENABLE_BT=1,nrOfPlanes,planeRotationType[];
	public static Tile tileMatrix[][];
	public static float tempPosX,tempPosY,secondTempPosX,secondTempPosY,startingPosY;
	Set<BluetoothDevice> pairedDevices;
	public static Sprite tile,background,planes[],youButton, chatButton;
	public static Boolean planeStatus[];
	AdView adView;
	AlertDialog helpAlert;
	public static Label scoreLabel;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        updateFullscreenStatus(true);
        
        ScreenDisplay.inGamePanel2 = new Panel(this); 
        adView = new AdView(this, AdSize.BANNER, "a14de914472599e");
		FrameLayout layout = new FrameLayout(this);
	    FrameLayout.LayoutParams gameParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
	            FrameLayout.LayoutParams.FILL_PARENT);
	    FrameLayout.LayoutParams adsParams =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, 
	            FrameLayout.LayoutParams.WRAP_CONTENT, android.view.Gravity.BOTTOM); 
	    layout.addView(ScreenDisplay.inGamePanel2, gameParams);
	    layout.addView(adView, adsParams); 
	    setContentView(layout);
	    
	    AdRequest request = new AdRequest();
	    //request.addTestDevice("CF95DC53F383F9A836FD749F3EF439CD");
	    //request.setTesting(true);
	    adView.loadAd(request);
        //setContentView(Display.inGamePanel2);
        touched=-1;
        shootMode = true;
        
        if (selectedI != -1)
        {
        	tileMatrix[selectedI][selectedJ].selected = false;
        }
        
        selectedI = -1;
        selectedJ = -1;
        
        init();
        //if (bluetoothSupported) checkBluetooth();
        //int usedMegs = (int)(Debug.getNativeHeapAllocatedSize() / 1048576L);
        //String usedMegsString = String.format(" - Memory Used: %d MB", usedMegs);
        //Toast.makeText(getBaseContext(), usedMegsString, Toast.LENGTH_SHORT).show();
        //getWindow().setTitle(usedMegsString);
    }
    public void init() 
    { 
    	startingPosY = 421;
    	tile = new Sprite(getResources(), R.drawable.grid, ScreenDisplay.inGamePanel2);
        background = new Sprite(getResources(), R.drawable.background, ScreenDisplay.inGamePanel2);
        background.setPosition(background.width/2, background.height/2);
    	tile.setPosition(160, 190);
    	ScreenDisplay.inGamePanel2.addSprite(background);
        ScreenDisplay.inGamePanel2.addSprite(tile);
        
        youButton = new Sprite(getResources(), R.drawable.you_button, ScreenDisplay.inGamePanel2);
        youButton.setPosition(30, 20);
        ScreenDisplay.inGamePanel2.addSprite(youButton);
        
        scoreLabel = new Label("You " + Air.planesCrashed + "-" + planesCrashed + " Opp", 25, ScreenDisplay.inGamePanel2, Color.WHITE);
        scoreLabel.setPosition(160,25);
		ScreenDisplay.inGamePanel2.addLabel(scoreLabel);
         
        /*if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER)
        {
	        chatButton = new Sprite(getResources(), R.drawable.chat_button, ScreenDisplay.inGamePanel2);
	        chatButton.setPosition(160, 20);
	        ScreenDisplay.inGamePanel2.addSprite(chatButton);
        }*/
        
        
    	
    	if (firstTime)
    	{
    		planesCrashed = 0;
    		tileMatrix = new Tile[10][];
    		for (int i=0;i<10;i++)
    			tileMatrix[i]=new Tile[10];
    	
    		for (int i=0;i<10;i++)
    		{
    			for (int j=0;j<10;j++)
    				tileMatrix[i][j] = new Tile(i,j);
    		}
    	}
    	else
    	{
    		for (int i=0;i<10;i++)
    		{
    			for (int j=0;j<10;j++)
    				if (tileMatrix[i][j].visible)
    				{
            	        ScreenDisplay.inGamePanel2.addSprite(tileMatrix[i][j].s);
    				}
    		}
    	}
    	
    	if (firstTime) 
    	{
    		nrOfPlanes = 3;
    		planes = new Sprite[nrOfPlanes];
    		planeStatus = new Boolean[nrOfPlanes];
        	planeRotationType = new int[nrOfPlanes];
    	}
        for (int i=0;i<nrOfPlanes;i++)
        {
        	if (firstTime)
        	{
        		planes[i] = new Sprite(getResources(), R.drawable.plane, ScreenDisplay.inGamePanel2);
        		planes[i].setPosition(60 + i*100, startingPosY);
        		planeStatus[i]=false;
        		planeRotationType[i]=0;
        	}
        	else
        	{
        		planes[i].setPanel(ScreenDisplay.inGamePanel2);
        	}
        	ScreenDisplay.inGamePanel2.addSprite(planes[i]);
        }
        //planes[0].rotate(90);
    }
    
    public void alertReceived()
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
    	alert.show();
	}
    
    public int checkPosition(int i, int j)
    {
    	int k =Air.op.getPosition(i, j);
    	if (k!=0) return k;
    	else return -1;
    }
    
    public Boolean checkPlane(int i)
    {
    	Boolean check;
    	check = false;
    	int y,x,temp1=0,temp2=0;
    	if (i%2==0) temp1 = 1;
    	else temp2 = 1;
		y = (int)(planes[touched].getTop() - (tile.getTop()-tile.height/2))/30 - temp1;
		x = (int)(planes[touched].getLeft() - (tile.getLeft()-tile.width/2))/30 - temp2;
    	switch(i)
		{
			case 0:
			{
				if (tileMatrix[y][x].value==0 && tileMatrix[y][x-1].value==0 && tileMatrix[y][x+1].value==0 
						&& tileMatrix[y+1][x].value==0 && tileMatrix[y+2][x].value==0
						&& tileMatrix[y+2][x-1].value==0 && tileMatrix[y+2][x+1].value==0
						&& tileMatrix[y-1][x].value==0) 
				{	
					check =true;
				}
				break;
			}
			case 1:
			{
				if (tileMatrix[y-1][x-1].value==0 && tileMatrix[y-1][x+1].value==0 && tileMatrix[y][x].value==0 
						&& tileMatrix[y][x-1].value==0 && tileMatrix[y][x+1].value==0 && tileMatrix[y+1][x-1].value==0 
						&& tileMatrix[y+1][x+1].value==0 && tileMatrix[y][x+2].value==0) 
				{
					check = true;
				}
				break;
			}
			case 2:
			{
				if (tileMatrix[y-1][x].value==0 && tileMatrix[y-1][x-1].value==0 && tileMatrix[y-1][x+1].value==0  
						&& tileMatrix[y][x].value==0 && tileMatrix[y+1][x].value==0 && tileMatrix[y+1][x-1].value==0 
						&& tileMatrix[y+1][x+1].value==0 && tileMatrix[y+2][x].value==0 ) 
				{
					check =true;
				}
				break;
			}
			case 3:
			{
				if (tileMatrix[y-1][x].value==0 && tileMatrix[y-1][x+2].value==0 && tileMatrix[y][x].value==0  
						&& tileMatrix[y][x+1].value==0 && tileMatrix[y][x+2].value==0 && tileMatrix[y+1][x].value==0 
						&& tileMatrix[y+1][x+2].value==0 && tileMatrix[y][x-1].value==0) 
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
				if (tileMatrix[y][x].value==0 && tileMatrix[y][x-1].value==0 && tileMatrix[y][x+1].value==0 
						&& tileMatrix[y+1][x].value==0 && tileMatrix[y+2][x].value==0
						&& tileMatrix[y+2][x-1].value==0 && tileMatrix[y+2][x+1].value==0
						&& tileMatrix[y-1][x].value==0) 
				{	
					tileMatrix[y][x].value=1; 
					tileMatrix[y][x-1].value=1;
					tileMatrix[y][x+1].value=1;
					tileMatrix[y+1][x].value=1;
					tileMatrix[y+2][x].value=1;
					tileMatrix[y+2][x-1].value=1;
					tileMatrix[y+2][x+1].value=1;
					tileMatrix[y-1][x].value=2;
				}
				else
				{
					planeStatus[touched] = false;
					
				}
				break;
			}
			case 1:
			{
				if (tileMatrix[y-1][x-1].value==0 && tileMatrix[y-1][x+1].value==0 && tileMatrix[y][x].value==0 
						&& tileMatrix[y][x-1].value==0 && tileMatrix[y][x+1].value==0 && tileMatrix[y+1][x-1].value==0 
						&& tileMatrix[y+1][x+1].value==0 && tileMatrix[y][x+2].value==0) 
				{
					tileMatrix[y-1][x-1].value=1;
					tileMatrix[y-1][x+1].value=1;
					tileMatrix[y][x].value=1;
					tileMatrix[y][x-1].value=1;
					tileMatrix[y][x+1].value=1;
					tileMatrix[y+1][x-1].value=1;
					tileMatrix[y+1][x+1].value=1;
					tileMatrix[y][x+2].value=2;
				}
				 
				else
				{
					planeStatus[touched] = false;
					
				}
				break;
			}
			case 2:
			{
				if (tileMatrix[y-1][x].value==0 && tileMatrix[y-1][x-1].value==0 && tileMatrix[y-1][x+1].value==0  
						&& tileMatrix[y][x].value==0 && tileMatrix[y+1][x].value==0 && tileMatrix[y+1][x-1].value==0 
						&& tileMatrix[y+1][x+1].value==0 && tileMatrix[y+2][x].value==0 ) 
				{
					tileMatrix[y-1][x].value=1;
					tileMatrix[y-1][x-1].value=1;
					tileMatrix[y-1][x+1].value=1;
					tileMatrix[y][x].value=1;
					tileMatrix[y+1][x].value=1;
					tileMatrix[y+1][x-1].value=1;
					tileMatrix[y+1][x+1].value=1;
					tileMatrix[y+2][x].value=2;
					
				} 
				else
				{
					planeStatus[touched] = false;
					
				}
				break;
			}
			case 3:
			{
				if (tileMatrix[y-1][x].value==0 && tileMatrix[y-1][x+2].value==0 && tileMatrix[y][x].value==0  
						&& tileMatrix[y][x+1].value==0 && tileMatrix[y][x+2].value==0 && tileMatrix[y+1][x].value==0 
						&& tileMatrix[y+1][x+2].value==0 && tileMatrix[y][x-1].value==0) 
				{
					tileMatrix[y-1][x].value=1;
					tileMatrix[y-1][x+2].value=1;
					tileMatrix[y][x].value=1;
					tileMatrix[y][x+1].value=1;
					tileMatrix[y][x+2].value=1;
					tileMatrix[y+1][x].value=1;
					tileMatrix[y+1][x+2].value=1;
					tileMatrix[y][x-1].value=2;
					
				} 
				else
				{
					planeStatus[touched] = false;
					
				}
			}
			
		}
    }
    public void toYourScreen()
    {
    	finish();
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
				tileMatrix[y][x].value=0;             				
				tileMatrix[y][x-1].value=0;           				
				tileMatrix[y][x+1].value=0;           				
				tileMatrix[y+1][x].value=0;           				
				tileMatrix[y+2][x].value=0;            				
				tileMatrix[y+2][x-1].value=0;           				
				tileMatrix[y+2][x+1].value=0;           				
				tileMatrix[y-1][x].value=0;          				
				break;
			}
			case 1:
			{
				tileMatrix[y-1][x-1].value=0;          				
				tileMatrix[y-1][x+1].value=0;           				
				tileMatrix[y][x].value=0;
				tileMatrix[y][x-1].value=0;           				
				tileMatrix[y][x+1].value=0;           				
				tileMatrix[y+1][x-1].value=0;           				
				tileMatrix[y+1][x+1].value=0;           				
				tileMatrix[y][x+2].value=0;          				
				break;
			}
			case 2:
			{
				tileMatrix[y-1][x].value=0;          				
				tileMatrix[y-1][x-1].value=0;            				
				tileMatrix[y-1][x+1].value=0;            				
				tileMatrix[y][x].value=0;            				
				tileMatrix[y+1][x].value=0;            				
				tileMatrix[y+1][x-1].value=0;            				
				tileMatrix[y+1][x+1].value=0;            				
				tileMatrix[y+2][x].value=0;           				
				break;
			}
			case 3:
			{
				tileMatrix[y-1][x].value=0;            				
				tileMatrix[y-1][x+2].value=0;           				
				tileMatrix[y][x].value=0;           				
				tileMatrix[y][x+1].value=0;            				
				tileMatrix[y][x+2].value=0;
				tileMatrix[y+1][x].value=0;
				tileMatrix[y+1][x+2].value=0;
				tileMatrix[y][x-1].value=0;
			}	
		}
    }
    
    public void opponentsTurn()
    {
    	Air.opponentsTurn = true;
    	Handler handler = new Handler(); 
        handler.postDelayed(new Runnable() { 
             public void run() { 
            	 finish(); 
             } 
        }, 1000); 
    	
    }
    
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float tempX=160,tempY=240;
        currentX = (int)event.getX(0); 
        currentY = (int)event.getY(0); 
        //MotionEvent.
        switch(action & MotionEvent.ACTION_MASK)
        {
        case MotionEvent.ACTION_DOWN:
        { 
        	if (Air.gameOver) finish();
        	else if (youButton.touched(currentX, currentY))
        	{
        		toYourScreen();
        	}
        	/*else if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER && chatButton.touched(currentX, currentY))
        	{
        		startActivity(new Intent(getBaseContext(), BluetoothChat.class));
        	}*/
        	else
        	{
        		if ((PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER) && Air.opponentsTurn)
        		{
        			Toast.makeText(getApplicationContext(), "It's not your turn!", Toast.LENGTH_SHORT).show();
        		}
        		else if (!shootMode)
        		{
        				for (int i=0;i<nrOfPlanes;i++)
        				{
        					if (planes[i].touched(currentX, currentY))
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
        		else if (Air.opponentsTurn) Toast.makeText(getBaseContext(), "It's not your turn!", Toast.LENGTH_SHORT).show();
        		else if (tile.touched(currentX, currentY))
        		{		
        			int tempI, tempJ;
        			tempI = (int)((currentY - tile.top) / 30);
        			tempJ = (int)((currentX - tile.left) / 30);
        			if (tileMatrix[tempI][tempJ].selected && tempI == selectedI && tempJ == selectedJ)
        			{
        				if (PlayScene.GAME_TYPE == PlayScene.MULTI_PLAYER)
        				{
        					Air.pointToSend.x = tempI;
        					Air.pointToSend.y = tempJ;
        					Air.sendMessage("/pos " + tempJ + " " + tempI);
        					Air.opponentsTurn = true;
        					
        				}
        				else
        				{
	        				if (checkPosition(tempI,tempJ)==-1)
	        				{
	        					tileMatrix[tempI][tempJ].s.setImage(getResources(), R.drawable.no_hit);
	        					tileMatrix[tempI][tempJ].hit=false;
	        					tileMatrix[tempI][tempJ].visible=true;
	        					tileMatrix[tempI][tempJ].value=-1;
	        					opponentsTurn();
	        				}
	        				else if (checkPosition(tempI,tempJ)==1)
	        				{
	        					tileMatrix[tempI][tempJ].s.setImage(getResources(), R.drawable.hit_body);
	        					tileMatrix[tempI][tempJ].hit=true;
	        					tileMatrix[tempI][tempJ].visible=true;
	        					opponentsTurn();
	        				}
	        				else if (checkPosition(tempI,tempJ)==2)
	        				{
	        					tileMatrix[tempI][tempJ].s.setImage(getResources(), R.drawable.hit_head);
	        					tileMatrix[tempI][tempJ].hit=true;
	        					tileMatrix[tempI][tempJ].visible=true;
	        					planesCrashed++;
	        					scoreLabel.setText("You " + Air.planesCrashed + "-" + planesCrashed + " Opp");
	        					if (planesCrashed == 3) 
	        					{
	        						Air.gameOver = true;
	        						Air.gameWon = true;
	        						alertReceived();
	        					}
	        					else opponentsTurn();
	        				}
	        				tileMatrix[tempI][tempJ].s.setPosition(25 + 30*tempJ, 55 + 30*tempI);
	        				ScreenDisplay.inGamePanel2.addSprite(tileMatrix[tempI][tempJ].s);
        				}
        			}
        			else if (!tileMatrix[tempI][tempJ].visible)
        			{
        				if (selectedI != -1)
        				{
        					ScreenDisplay.inGamePanel2.removeSprite(tileMatrix[selectedI][selectedJ].s);
        					//tileMatrix[selectedI][selectedJ].s = null;
        					//tileMatrix[selectedI][selectedJ].selected = false;
        				}
        				selectedI = tempI;
        				selectedJ = tempJ;
        				tileMatrix[tempI][tempJ].s = new Sprite(getResources(), R.drawable.selected, ScreenDisplay.inGamePanel2);
    					tileMatrix[tempI][tempJ].selected = true;
        				tileMatrix[tempI][tempJ].s.setPosition(25 + 30*tempJ, 55 + 30*tempI);
        				ScreenDisplay.inGamePanel2.addSprite(tileMatrix[tempI][tempJ].s);
        			}
        		}
        		else //checking if the player has touched an outside plane when in shootMode
        		{
        			for (int i=0; i<nrOfPlanes;i++)
        				if (planes[i].touched(currentX, currentY))
        					Toast.makeText(getBaseContext(), "If you want to arrange planes you have to switch to arrange planes mode", Toast.LENGTH_SHORT).show();
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
    
    
    
	public void onScoreSubmit(int status, Exception error) {
		// TODO Auto-generated method stub
		startActivityForResult(new Intent(this, ShowResultOverlayActivity.class), SHOW_RESULT);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		ScoreloopManagerSingleton.get().setOnScoreSubmitObserver(null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		ScoreloopManagerSingleton.get().setOnScoreSubmitObserver(this);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		ScreenDisplay.inGamePanel2.removeAll();
		ScreenDisplay.inGamePanel2 = null;
		System.gc();
		if (firstTime) firstTime = false;
	}
    
	
	public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.opponentmenu , menu);
        return true;
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

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.sw:
        	if (shootMode)  
        	{
        		shootMode = false;
        		Toast.makeText(getBaseContext(), "Arrange planes mode activated!", Toast.LENGTH_SHORT).show();
        	}
        	else 
        	{
        		shootMode = true;
        		Toast.makeText(getBaseContext(), "Shoot mode activated!", Toast.LENGTH_SHORT).show();
        	}
            return true;
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
    
    //This is for updating the fullscreen status (maybe from the menu)
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

}