package cl.wood.chatter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gabrielpoblete on 08-08-17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> mMessages;
    private String mUser;
    private static final int CURRENT_USER = 1;
    private static final int OTHER_USER   = 2;

    public MessageAdapter(List<Message> messages, String user) {
        mMessages = messages;
        mUser     = user;
    }

    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == CURRENT_USER) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_message, parent, false);
        } else if(viewType == OTHER_USER){
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        holder.mUserMessage.setText(message.getUserName());
        holder.mMessage.setText(message.getBody());
        holder.mTime.setText("at " + message.getFormattedTime());
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        if(message.isMine(mUser)){
            return CURRENT_USER;
        } else {
            return OTHER_USER;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mUserMessage;
        private TextView mMessage;
        private TextView mTime;

        ViewHolder(View itemView) {
            super(itemView);

            mUserMessage = itemView.findViewById(R.id.userTextView);
            mMessage     = itemView.findViewById(R.id.messageTextView);
            mTime = itemView.findViewById(R.id.dateTextView);
        }
    }

}
