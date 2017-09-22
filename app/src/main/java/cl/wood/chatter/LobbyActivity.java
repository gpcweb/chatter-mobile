package cl.wood.chatter;

import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.phoenixframework.channels.Channel;
import org.phoenixframework.channels.ChannelEvent;
import org.phoenixframework.channels.Envelope;
import org.phoenixframework.channels.IMessageCallback;
import org.phoenixframework.channels.ITimeoutCallback;
import org.phoenixframework.channels.Socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LobbyActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private ImageButton mButton;
    private EditText mMessage;
    private MessageAdapter mAdapter;
    private List<Message> mMessages = new ArrayList<Message>();
    private String mUser;
    private String mToken;

    private Socket mSocket;
    private Channel mChannel;

    private ObjectMapper mObjectMapper = new ObjectMapper();

    private static final String TAG = LobbyActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        Intent intent = getIntent();
        mToken        = intent.getStringExtra("token");
        mUser         = intent.getStringExtra("user");

        mButton  = (ImageButton) findViewById(R.id.buttonSend);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = mMessage.getText().toString();
                mMessage.setText("");
                new SendMessageTask().execute(message);
            }
        });

        mMessage = (EditText) findViewById(R.id.messageText);
        mRecyclerView = (RecyclerView) findViewById(R.id.messagesRecyclerView);

        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MessageAdapter(mMessages, mUser);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new ConnectTask().execute();
    }

    private class SendMessageTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            final String message = params[0];
            if (mChannel != null) {
                final ObjectNode payload = mObjectMapper.createObjectNode();
                payload.put("body", message);
                try {
                    mChannel.push("message:new", payload)
                            .receive("ok", new IMessageCallback() {
                                @Override
                                public void onMessage(final Envelope envelope) {
                                    Log.i(TAG, "Message was send");
                                }
                            })
                            .timeout(new ITimeoutCallback() {
                                @Override
                                public void onTimeout() {
                                    Log.w(TAG, "Message time out");
                                }
                            });
                } catch (IOException e) {
                    Log.e(TAG, "Failed to send message", e);
                }
            }
            return null;
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                String webSocketUrl = "http://chatter.coor.cl/socket/websocket?token=" + mToken;
                Log.d(TAG, mToken);
                mSocket = new Socket(webSocketUrl);
                mSocket.connect();

                mChannel = mSocket.chan("room:lobby", null);

                mChannel.join()
                        .receive("ignore", new IMessageCallback() {
                            @Override
                            public void onMessage(Envelope envelope) {
                                Log.d(TAG, "Some error.. :(");
                            }
                        })
                        .receive("ok", new IMessageCallback() {
                            @Override
                            public void onMessage(Envelope envelope) {
                                Log.d(TAG, "JOINED with " + envelope.toString());
                            }
                        });

                mChannel.on("message:new", new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        try {
                            Log.d(TAG, envelope.getPayload().toString());
                            final Message message = mObjectMapper.treeToValue(envelope.getPayload(), Message.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mMessages.add(message);
                                    mAdapter.notifyItemInserted(mMessages.size() - 1);
                                    mRecyclerView.scrollToPosition(mMessages.size() - 1);
                                }
                            });
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                });

                mChannel.on(ChannelEvent.CLOSE.getPhxEvent(), new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        System.out.println("CLOSED: " + envelope.toString());
                    }
                });

                mChannel.on(ChannelEvent.ERROR.getPhxEvent(), new IMessageCallback() {
                    @Override
                    public void onMessage(Envelope envelope) {
                        System.out.println("ERROR: " + envelope.toString());
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
