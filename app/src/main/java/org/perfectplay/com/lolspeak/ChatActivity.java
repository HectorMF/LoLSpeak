package org.perfectplay.com.lolspeak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChatActivity extends Activity {

    private XMPPTCPConnection connection;
    private static HashMap<String, ArrayList<Spanned>> messages = new HashMap<String, ArrayList<Spanned>>();
    private Handler mHandler = new Handler();

    public String recipient = "sum47559389@pvp.net";
    private EditText textMessage;
    private ListView listview;
    private Roster roster;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        textMessage = (EditText) this.findViewById(R.id.chatET);
        listview = (ListView) this.findViewById(R.id.listMessages);
        setListAdapter();

        // Set a listener to send a chat text message
        Button send = (Button) this.findViewById(R.id.sendBtn);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String to = recipient;
                String text = textMessage.getText().toString();
                if(text.isEmpty()) return;
                textMessage.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(textMessage.getWindowToken(), 0);
                Log.i("XMPPChatDemoActivity ", "Sending text " + text + " to " + to);
                Message msg = new Message(to, Message.Type.chat);
                msg.setBody(text);
                if (connection != null) {
                    try {
                        connection.sendPacket(msg);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    //String fromName = connection.getUser()..getAccountAttribues("name");
                    if(!messages.containsKey(recipient))
                        messages.put(recipient, new ArrayList<Spanned>());
                    messages.get(recipient).add(Html.fromHtml("<font color='red'><b>" + "You" + "</b></font>: " + text));
                    setListAdapter();
                }
            }
        });
        connect();
    }

    /**
     * Called by Settings dialog when a connection is establised with
     * the XMPP server
     */
    public void setConnection(XMPPTCPConnection connection) {
        this.connection = connection;
        this.roster = connection.getRoster();
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter =  MessageTypeFilter.CHAT;
            connection.addPacketListener(new PacketListener() {
                @Override
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        Log.d("FROM", message.getFrom());
                        String fromSimplified =  message.getFrom().substring(0,message.getFrom().indexOf("/"));
                        String fromName = roster.getEntry(fromSimplified).getName();//roster.getEntry(message.getFrom()).getName();
                        Log.i("XMPPChatDemoActivity ", " Text Recieved " + message.getBody() + " from " +  fromName);

                        if(!messages.containsKey(fromSimplified))
                            messages.put(fromSimplified, new ArrayList<Spanned>());
                        messages.get(fromSimplified).add(Html.fromHtml("<font color='blue'><b>" + fromName + "</b></font>: " + message.getBody()));

                        //messages.add(message.getBody());
                        // Add the incoming message to the list view
                        mHandler.post(new Runnable() {
                            public void run() {
                                setListAdapter();
                            }
                        });
                    }
                }
            }, filter);
        }
    }

    private void setListAdapter() {
        if(!messages.containsKey(recipient))
            messages.put(recipient, new ArrayList<Spanned>());
        ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(this, R.layout.listitem, messages.get(recipient));
        listview.setAdapter(adapter);
        listview.setSelection(adapter.getCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void connect() {

        final ProgressDialog dialog = ProgressDialog.show(this, "Connecting...", "Please wait...", false);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Create a connection
                connection = LoginActivity.xmppConnection;

                // Set the status to available
                Presence presence = new Presence(Presence.Type.available);
                try {
                    connection.sendPacket(presence);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                setConnection(connection);

                Collection<RosterEntry> entries = roster.getEntries();
                for (RosterEntry entry : entries) {

                    Log.d("XMPPChatDemoActivity",  "--------------------------------------");
                    Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
                    Log.d("XMPPChatDemoActivity", "User: " + entry.getUser());
                    Log.d("XMPPChatDemoActivity", "Name: " + entry.getName());
                    Log.d("XMPPChatDemoActivity", "Status: " + entry.getStatus());
                    Log.d("XMPPChatDemoActivity", "Type: " + entry.getType());
                    Presence entryPresence = roster.getPresence(entry.getUser());

                    Log.d("XMPPChatDemoActivity", "Presence Status: "+ entryPresence.getStatus());
                    Log.d("XMPPChatDemoActivity", "Presence Type: " + entryPresence.getType());

                    Presence.Type type = entryPresence.getType();
                    if (type == Presence.Type.available)
                        Log.d("XMPPChatDemoActivity", "Presence AVIALABLE");
                    Log.d("XMPPChatDemoActivity", "Presence : " + entryPresence);
                }
                dialog.dismiss();
            }
        });
        t.start();
        dialog.show();
    }
}