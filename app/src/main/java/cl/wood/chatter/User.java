package cl.wood.chatter;

/**
 * Created by gabrielpoblete on 08-08-17.
 */

public class User {

    private String mName;
    private String mLastName;
    private String mEmail;
    private String mPassword;
    private String mToken;

    public User(){}

    public User(String name, String lastName, String email, String token) {
        mName = name;
        mLastName = lastName;
        mEmail = email;
        mToken = token;
    }

    public User(String email, String password){
        mEmail = email;
        mPassword = password;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        mToken = token;
    }
}
