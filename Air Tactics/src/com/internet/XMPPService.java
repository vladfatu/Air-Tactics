package com.internet;

import java.io.IOException;
import java.nio.channels.InterruptibleChannel;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.PacketInterceptor;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.internet.ui.adapters.GameRow;
import com.users.User;

public class XMPPService extends Service{
	
	public static String SERVER = "tacticsgames.dyndns.org";
	public static String NEW_MESSAGE = "new message";
	
	public static String OPP_ACTION = "opp_action";
	public static String OPP_JOIN = "opp_join";
	
	public static String PRESENCE = "presence";
	public static String NEW_GAME = "new game";
	public static String NO_GAME = "no game";
	
	public static String CONNECTION_SUCCESSFUL = "com.airtactics.CONNECTION_SUCCESSFUL";
	public static String CONNECTION_FAILED = "com.airtactics.CONNECTION_FAILED";
	
	public static String MESSAGE_TYPE = "message_type";
	public static String SENDER = "sender";
	public static String EMAIL_ADDRESS = "@tacticsgames.tk";
	public static int AVAILABLE = 1;
	public static int AWAY = 2;
	public static int PORT = 5222;
	private int currentMode;
    private XMPPConnection connection; 
    private MultiUserChat chat;
    Roster roster;
    RosterListener rosterListener;
    
    public MultiUserChat getChat() {
		return chat;
	}

	public void setChat(MultiUserChat chat) {
		this.chat = chat;
	}

	private static XMPPService instance;
    
    public XMPPService()
    {
    	if (instance == null)
    		instance = this;
    }
    
    public static XMPPService getInstance()
    {
    	return instance;
    }
    
    @Override
    public void onCreate()
    {
    	super.onCreate();
    	connect();
    }
    
    public boolean isConnected()
    {
    	if (connection != null)
    	{
    		return connection.isConnected();
    	}
    	return false;
    }
    
    public boolean isAuthenticated()
    {
    	if (isConnected())
    	{
    		return connection.isAuthenticated();
    	}
    	return false;
    }
    
    public void sendMessage(String to, String text) {
    	
        Message msg = new Message(to, Message.Type.chat);
        msg.setBody(text);
        connection.sendPacket(msg);
    }
    
    public void createAccount(String username, String password)
    {
    	Map<String, String> map = new HashMap<String, String>();
    	map.put("email", username + "@tacticsgames.tk");
    	AccountManager manager = connection.getAccountManager();
    	try {
			manager.createAccount(username, password, map);
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void addRosterListener()
    {
		
		rosterListener = new RosterListener() {
		    // Ignored events public void entriesAdded(Collection<String> addresses) {}
		    public void entriesDeleted(Collection<String> addresses) {}
		    public void entriesUpdated(Collection<String> addresses) {}
		    public void presenceChanged(Presence presence) {
		        System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
		        if (presence.isAway())
		        {
		        	Intent i = new Intent("new");
                    i.putExtra(MESSAGE_TYPE, PRESENCE);
                    i.putExtra(PRESENCE, NEW_GAME);
                    i.putExtra(SENDER, presence.getFrom());
                    sendBroadcast(i);
		        }
		        else if (presence.isAvailable())
		        {
		        	Intent i = new Intent("new");
                    i.putExtra(MESSAGE_TYPE, PRESENCE);
                    i.putExtra(PRESENCE, NO_GAME);
                    i.putExtra(SENDER, presence.getFrom());
                    sendBroadcast(i);
		        }
		    }
			public void entriesAdded(Collection<String> arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		roster.addRosterListener(rosterListener);
    }
    
    public ArrayList<GameRow> getGames()
    {
    	ArrayList<GameRow> games = new ArrayList<GameRow>();
    	Collection<RosterEntry> entries = roster.getEntries();
    	for (RosterEntry entry : entries) {
    		if (roster.getPresence(entry.getUser()).isAway())
    		{
    			games.add(new GameRow(entry.getUser()));
    		}
    	}
    	return games;
    }
    
    public void removerosterListener()
    {
    	roster.removeRosterListener(rosterListener);
    }
    
    public void joinChat(String chatName)
    {
    	chat = new MultiUserChat(connection, chatName + "@conference.tacticsgames.tk");
    	try {
			chat.join(User.getInstance().getUsername(), User.getInstance().getPassword());
            chat.addPresenceInterceptor(new PacketInterceptor() {

                public void interceptPacket(Packet arg0) {
                    System.out.println("status_incerceptor " + arg0);
                }
            });
			//in case I will need to get messages in the rooms, 
			//this will be the way to do it
			
			/*chat.addMessageListener(new PacketListener() 
            {
                public void processPacket(Packet packet) 
                {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.i("XMPPClient", "Got text [" + message.getBody() + "] from [" + fromName + "]");
                        // Send the incoming message to any activity that is listening
                        Intent i = new Intent("new");
                        i.putExtra(NEW_MESSAGE, message.getBody());
                        i.putExtra(SENDER, fromName);
                        sendBroadcast(i);
                    }
                }
            });*/
			
    	} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void connect() {
    	
        try {
			KeyStore.getInstance("bks");
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Create a connection
        ConnectionConfiguration connConfig =
                new ConnectionConfiguration(SERVER, PORT);
        connConfig.setExpiredCertificatesCheckEnabled(true);
        connConfig.setNotMatchingDomainCheckEnabled(true);
        connConfig.setSecurityMode(SecurityMode.required);
        connConfig.setSelfSignedCertificateEnabled(true);
        connConfig.setSASLAuthenticationEnabled(true);
        connConfig.setKeystoreType("bks");
        connection = new XMPPConnection(connConfig);
        

        try {
            connection.connect();
            sendBroadcast(new Intent(CONNECTION_SUCCESSFUL));
            Log.i("XMPPClient", "[SettingsDialog] Connected to " + connection.getHost());
        } catch (XMPPException ex) {
        	sendBroadcast(new Intent(CONNECTION_FAILED));
            Log.e("XMPPClient", "[SettingsDialog] Failed to connect to " + connection.getHost());
            Log.e("XMPPClient", ex.toString());
            setConnection(null);
        }
      
    }
    
    public void login(String username, String password)
    {
    	try {
            connection.login(username, password);
            Log.i("XMPPClient", "Logged in as " + connection.getUser());

            // Set the status to available
            Presence presence = new Presence(Presence.Type.available);
            connection.sendPacket(presence);
            currentMode = AVAILABLE;
            setConnection(connection);
            roster = connection.getRoster();
            addRosterListener();
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as " + username);
            Log.e("XMPPClient", ex.toString());
                setConnection(null);
        }
    }
    
    public void setMode(int mode)
    {
    	Presence presence = new Presence(Presence.Type.available);
    	if (mode == AWAY && currentMode == AVAILABLE)
    	{
    		presence.setMode(Presence.Mode.away);
            connection.sendPacket(presence);
            currentMode = AWAY;
    	}
    	else if (mode == AVAILABLE && currentMode == AWAY)
    	{
    		presence.setMode(Presence.Mode.available);
            connection.sendPacket(presence);
            currentMode = AVAILABLE;
    	}
    }
    
    

    public int getCurrentMode() {
		return currentMode;
	}

	public void setCurrentMode(int currentMode) {
		this.currentMode = currentMode;
	}

	/**
     * Called by Settings dialog when a connection is establised with the XMPP server
     *
     * @param connection
     */
    public void setConnection(XMPPConnection connection) 
    {
        this.connection = connection;
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.i("XMPPClient", "Got text [" + message.getBody() + "] from [" + fromName + "]");
                        // Send the incoming message to any activity that is listening
                        Intent i = new Intent("new");
                        i.putExtra(MESSAGE_TYPE, NEW_MESSAGE);
                        i.putExtra(NEW_MESSAGE, message.getBody());
                        i.putExtra(SENDER, fromName);
                        sendBroadcast(i);
                    }
                }
            }, filter);
        }
    }

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onDestroy() {
		if (connection != null)
		{
			Presence presence = new Presence(Presence.Type.available);
        	connection.disconnect(presence);
        	instance = null;
		}
		super.onDestroy();
	}
}
