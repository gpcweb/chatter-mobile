package cl.wood.chatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by gabrielpoblete on 08-08-17.
 */
@JsonIgnoreProperties({ "node" })
public class Message {
    private String mBody;
    private String mUserName;
    private long   mTime;

    public Message(@JsonProperty("body") String body, @JsonProperty("user") String userName, @JsonProperty("timestamp") long time) {
        mBody = body;
        mUserName = userName;
        mTime = time;
    }

    public String getBody() {
        return mBody;
    }

    public void setBody(String body) {
        mBody = body;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String user) {
        mUserName = user;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(int time) {
        mTime = time;
    }

    public String getFormattedTime(){
        Date date            = new Date(mTime);
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        return formatter.format(date);
    }

    public boolean isMine(String user) {
        return mUserName.trim().equals(user.trim());
    }
}
