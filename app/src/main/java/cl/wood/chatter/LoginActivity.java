package cl.wood.chatter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView mEmail;
    private TextView mPassword;
    private Button mLogInButton;
    private User mUser = new User();

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail    = (TextView) findViewById(R.id.emailTextView);
        mPassword = (TextView) findViewById(R.id.passwordTextView);

        mLogInButton = (Button) findViewById(R.id.logInButton);
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email    = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if (!email.isEmpty()) {
                    loginUser(email, password);
                } else {
                    mEmail.setError("Por favor ingresa un correo.");
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        String apiUrl = "http://chatter.coor.cl/api/log_in";

        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();
        Request request     =  new Request.Builder().url(apiUrl).post(formBody).build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.v(TAG, "Exception caught: ", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String jsonData  = response.body().string();
                    if (response.isSuccessful()){
                        getCurrentUserDetails(jsonData);
                        goToLobbyActivity();
                    } else {
                        AlertDialogFragment dialog = new AlertDialogFragment();
                        dialog.show(getFragmentManager(), "error_dialog");
                        Log.v(TAG, "Unsuccessful response from api");
                    }
                    Log.v(TAG, jsonData);
                } catch (JSONException e) {
                    Log.v(TAG, "Exception caught: ", e);
                }
            }
        });
    }

    private void getCurrentUserDetails(String jsonData) throws JSONException {
        JSONObject response = new JSONObject(jsonData);
        mUser.setName(response.getString("name"));
        mUser.setLastName(response.getString("last_name"));
        mUser.setEmail(response.getString("email"));
        mUser.setToken(response.getString("token"));
    }

    private void goToLobbyActivity() {
        Intent intent = new Intent(LoginActivity.this, LobbyActivity.class);
        intent.putExtra("user", mUser.getName());
        intent.putExtra("token", mUser.getToken());
        startActivity(intent);
    }
}
