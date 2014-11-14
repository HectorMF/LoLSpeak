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
    private Handler mHandler = new Handler();

    public String recipient;
    private EditText textMessage;
    private ListView listview;
    private Roster roster;
    private Handler handler;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        handler = new Handler();
        textMessage = (EditText) this.findViewById(R.id.chatET);
        listview = (ListView) this.findViewById(R.id.listMessages);
        recipient = (String)getIntent().getExtras().get("User");
        LoginActivity.xmppConnection.removePacketListener(LoginActivity.listener);
        LoginActivity.xmppConnection.addPacketListener(LoginActivity.listener, MessageTypeFilter.CHAT);

        if(recipient.indexOf("/") > 0)
            recipient = recipient.substring(0, recipient.indexOf("/"));

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

                Message msg = new Message(to, Message.Type.chat);
                msg.setBody(text);
                if (connection != null) {
                    try {
                        connection.sendPacket(msg);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    //String fromName = connection.getUser()..getAccountAttribues("name");
                    if(!LoginActivity.messages.containsKey(recipient))
                        LoginActivity.messages.put(recipient, new ArrayList<Spanned>());
                    LoginActivity.messages.get(recipient).add(Html.fromHtml("<font color='red'><b>" + "You" + "</b></font>: " + text));
                    setListAdapter();
                }
            }
        });
        connect();
    }

    private void setListAdapter() {
        if(!LoginActivity.messages.containsKey(recipient))
            LoginActivity.messages.put(recipient, new ArrayList<Spanned>());
        ArrayAdapter<Spanned> adapter = new ArrayAdapter<Spanned>(this, R.layout.listitem, LoginActivity.messages.get(recipient));
        listview.setAdapter(adapter);
        listview.setSelection(adapter.getCount() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void connect() {
        final Runnable r = new Runnable()
        {
            public void run()
            {
                handler.postDelayed(this, 1000);
                setListAdapter();
            }
        };

        handler.post(r);

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
            }
        });
        t.start();
    }
}