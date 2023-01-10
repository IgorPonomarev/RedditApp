package com.example.redditapp.Account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.redditapp.FeedAPI;
import com.example.redditapp.R;
import com.example.redditapp.data.URLS;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private ProgressBar mProgressBar;
    private EditText mUserName;
    private EditText mPassWord;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPassWord = findViewById(R.id.input_password);
        mUserName = findViewById(R.id.input_username);
        mProgressBar = findViewById(R.id.login_progressbar);
        mProgressBar.setVisibility(View.GONE);

        Button btnLogin = findViewById(R.id.button_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in");
                String username = mUserName.getText().toString();
                String password = mPassWord.getText().toString();

                if (!username.equals("") && !password.equals("")) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    logIn(username, password);

                    //sign in method
                }
            }
        });

    }

    private void logIn(final String username, final String password) {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(URLS.LOGIN_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        FeedAPI feedAPI = retrofit.create(FeedAPI.class);
//
//        //disabled because returned invalid password error with this
////        HashMap<String, String> headerMap = new HashMap<>();
////        headerMap.put("Content-Type", "application/json");
//
//
//        Call<ResponseBody> call = feedAPI.getBody(username, username, password, "json");
//        Request request = call.request();
//        Log.d(TAG, "POST URL: "+ call.request().url() + ", \n" + call.request().header("Content-Type"));
//
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    Log.d(TAG, "onResponse: Got server response: " + response.body().string());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                mProgressBar.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                mProgressBar.setVisibility(View.GONE);
//                Log.e(TAG, "onFailure: Unable to Log In " + t.getMessage());
//                Toast.makeText(LoginActivity.this, "Unable to Log In", Toast.LENGTH_SHORT).show();
//            }
//        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLS.LOGIN_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        FeedAPI feedAPI = retrofit.create(FeedAPI.class);

        //disabled because returned invalid password error with this
//        HashMap<String, String> headerMap = new HashMap<>();
//        headerMap.put("Content-Type", "application/json");


        Call<CheckLogin> call = feedAPI.signIn(username, username, password, "json");
        Log.d(TAG, "POST URL: " + call.request().url());

        call.enqueue(new Callback<CheckLogin>() {
            @Override
            public void onResponse(Call<CheckLogin> call, Response<CheckLogin> response) {
                try {

                    Log.d(TAG, "onResponse: Got server response: " + response.body().toString());

                    String modhash = response.body().getJson().getData().getModhash();
                    String cookie = response.body().getJson().getData().getCookie();

                    Log.d(TAG, "onResponse: modhash: " + modhash);
                    Log.d(TAG, "onResponse: cookie: " + cookie);

                    if (!modhash.equals("")) {
                        setSessionParams(username, modhash, cookie);
                        mUserName.setText("");
                        mPassWord.setText("");
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        //navigate back to previous activity
                        finish();
                    }

                    mProgressBar.setVisibility(View.GONE);

                } catch (NullPointerException e) {
                    Log.e(TAG, "onResponse: NullPointerException: ", e);
                }
            }

            @Override
            public void onFailure(Call<CheckLogin> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: Unable to Log In " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Unable to Log In", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Save session params if login is successful
     *
     * @param username
     * @param modhash
     * @param cookie
     */
    private void setSessionParams(String username, String modhash, String cookie) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = preferences.edit();

        Log.d(TAG, "setSessionParams: Storing session variables: \n" +
                "username: " + username + "\n" +
                "modhash: " + modhash + "\n" +
                "cookie: " + cookie);

        editor.putString("@string/session_username", username);
        editor.commit();
        editor.putString("@string/session_modhash", modhash);
        editor.commit();
        editor.putString("@string/session_cookie", cookie);
        editor.commit();
    }
}
