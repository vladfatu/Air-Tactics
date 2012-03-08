package com.internet.ui;

import java.util.ArrayList;

import airtactics.com.R;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.airtactics.Air;
import com.airtactics.AirOpponent;
import com.airtactics.Opponent;
import com.airtactics.PlayScene;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.internet.InternetReceiver;
import com.internet.XMPPService;
import com.internet.ui.adapters.GameRow;
import com.users.User;

public class GameRoom extends ListActivity implements OnClickListener{

	private GamesAdapter adapter;
	public static String  OPPONENT = "opponent";
	private ArrayList<GameRow> games = null;
	private Runnable viewGames;
	private ProgressDialog m_ProgressDialog = null; 
	public static Boolean waiting;
	Button createButton;
	AdView adView;
	private LinearLayout linearLayout;
	
	private BroadcastReceiver mInternetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	String from;
        	from = "";
        	if (intent.getExtras() != null)
        	{
        		if (intent.hasExtra(XMPPService.MESSAGE_TYPE))
        		{
        			if (intent.getStringExtra(XMPPService.MESSAGE_TYPE).equals(XMPPService.PRESENCE))
        			{
        				if (intent.hasExtra(XMPPService.SENDER))
                		{
                			from = intent.getStringExtra(XMPPService.SENDER);
                			if (intent.hasExtra(XMPPService.PRESENCE))
                			{
                				if (intent.getStringExtra(XMPPService.PRESENCE).equals(XMPPService.NEW_GAME))
                				{
                					games.add(new GameRow(from));
                					adapter.add(new GameRow(from));
                					adapter.notifyDataSetChanged();
                				}
                				else if (intent.getStringExtra(XMPPService.PRESENCE).equals(XMPPService.NO_GAME))
                				{
                					//games.remove(tempGame);
                					adapter.removeGame(from);
                					adapter.notifyDataSetChanged();
                					
                				}
                			}
                		}
        			}
        			else if (intent.getStringExtra(XMPPService.MESSAGE_TYPE).equals(XMPPService.NEW_MESSAGE))
        			{
        				if (intent.hasExtra(XMPPService.SENDER))
                		{
                			from = intent.getStringExtra(XMPPService.SENDER);
                			if (intent.hasExtra(XMPPService.NEW_MESSAGE))
                			{
                				if (intent.getStringExtra(XMPPService.NEW_MESSAGE).equals("/join"))
                				{
                					XMPPService.getInstance().sendMessage(from, "/resp");
                					startGame(from, false);
                				}
                				else if (intent.getStringExtra(XMPPService.NEW_MESSAGE).equals("/resp"))
                				{
                					startGame(from, true);
                				}
                			}
                		}
        			}
        		}
        	}
        }
    };
    
    private void startGame(String from, Boolean opponentFirst)
    {
    	Intent intent = new Intent(getBaseContext(), Air.class);
		intent.putExtra(OPPONENT, from);
		intent.putExtra("AI", "1");
		AirOpponent.selectedI = -1;
		AirOpponent.selectedJ = -1;
		Air.opponentsTurn = opponentFirst;
		AirOpponent.firstTime = true;
		createButton.setText(getString(R.string.createGame));
		XMPPService.getInstance().setMode(XMPPService.AVAILABLE);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
    	//finish();
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
        setContentView(R.layout.game_room);
        linearLayout = (LinearLayout) findViewById(R.id.add_layout_game_room);
        linearLayout.addView(adView);
        
        AdRequest request = new AdRequest();
	    //request.addTestDevice("CF95DC53F383F9A836FD749F3EF439CD");
	    //request.setTesting(true);
	    adView.loadAd(request);
		
		XMPPService.getInstance().setMode(XMPPService.AVAILABLE);
		waiting = false;
		
		createButton = (Button) findViewById(R.id.createButton);
		createButton.setOnClickListener(this);
		
        //XMPPService.getInstance().joinChat("test");
		
		
        
        
	}
	
	private Runnable returnRes = new Runnable() {

        public void run() {
            if(games != null && games.size() > 0){
                adapter.notifyDataSetChanged();
                for(int i=0;i<games.size();i++)
                adapter.add(games.get(i));
            }
            m_ProgressDialog.dismiss();
            adapter.notifyDataSetChanged();
        }
    };
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	
    	XMPPService.getInstance().sendMessage(games.get(position).getOpponent(), "/join");
    	//startGame(games.get(position).getOpponent(), true);
        /*Order o = new Order();
        o = m_orders.get(position);
        itemId = o.getId();
        m_orders = new ArrayList<Order>();
        this.m_adapter = new OrderAdapter(this, R.layout.row, m_orders);
        setListAdapter(this.m_adapter);
        Thread thread =  new Thread(null, viewOrders, "MagentoBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(Grocery.this,    
              "Please wait...", "Retrieving data ...", true);
        //Toast.makeText(getBaseContext(), " ID #" + m_orders.indexOf(o), 1).show();*/
    }
	
	private void getGames() 
	{
		if (!User.getInstance().getLoggedIn())
		{
			XMPPService.getInstance().login(User.getInstance().getUsername(), User.getInstance().getPassword());
			User.getInstance().setLoggedIn(true);
		}
		games = XMPPService.getInstance().getGames();
        runOnUiThread(returnRes);
		
	}
	
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.createButton:
			if (XMPPService.getInstance().getCurrentMode() == XMPPService.AVAILABLE) 
			{
				createButton.setText(getString(R.string.stopWaiting));
				XMPPService.getInstance().setMode(XMPPService.AWAY);
			}
			else if (XMPPService.getInstance().getCurrentMode() == XMPPService.AWAY)
			{
				createButton.setText(getString(R.string.createGame));
				XMPPService.getInstance().setMode(XMPPService.AVAILABLE);
			}
		}
		
	}
	
	private class GamesAdapter extends ArrayAdapter<GameRow> {

	    private ArrayList<GameRow> items;

	    public GamesAdapter(Context context, int textViewResourceId, ArrayList<GameRow> items) 
	    {
	            super(context, textViewResourceId, items);
	            this.items = items;
	    }
	    
	    public void removeGame(String user)
	    {
	    	for (GameRow game : items)
	    	{
	    		if (game.getOpponent().equals(user))
	    			items.remove(game);
	    	}
	    }
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) 
	    {
	            View v = convertView;
	            if (v == null) 
	            {
	                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                v = vi.inflate(R.layout.game_row, null);
	            }
	            GameRow o = items.get(position);
	            if (o != null) 
	            {
	                    TextView tt = (TextView) v.findViewById(R.id.opponentName);
	                    TextView bt = (TextView) v.findViewById(R.id.DaysText);
	                    if (tt != null) 
	                    {
	                          tt.setText(o.getOpponent().split("@")[0]); 
	                    }
	                    if(bt != null)
	                    {
	                        //  bt.setText(o.getOrderStatus());
	                    }
	            }
	            return v;
	    }
	}
	
	@Override
    public void onResume() 
    {
    	super.onResume();
    	
    	if (PlayScene.GAME_TYPE == PlayScene.INTERNET_MULTI_PLAYER)
		{
			if (XMPPService.getInstance() != null)
			{
				if (Air.disconnected != null && Air.gameOver != null)
				if (!Air.disconnected && !Air.gameOver) XMPPService.getInstance().sendMessage(Opponent.internetName, "/disconnect");
			}
		}
    	
    	games = new ArrayList<GameRow>();
        adapter = new GamesAdapter(this, R.layout.game_row, games);
        setListAdapter(adapter);
        
        viewGames = new Runnable(){
            public void run() {
                getGames();
            }
        };
    	Thread thread =  new Thread(null, viewGames, "MagentoBackground");
        thread.start();
        m_ProgressDialog = ProgressDialog.show(GameRoom.this,    
        		"Please wait...", "Retrieving data ...", true);
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
    	
    	//might need this if the service is not stopped here
    	XMPPService.getInstance().setMode(XMPPService.AVAILABLE);
    	
    	stopService(new Intent(this, XMPPService.class));
    	
	}

}
