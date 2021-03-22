package com.example.simplerentalapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.simplerentalapplication.model.User;
import com.example.simplerentalapplication.rest.APIClient;
import com.example.simplerentalapplication.rest.APIInterface;
import com.example.simplerentalapplication.ui.AdminActivity;
import com.example.simplerentalapplication.ui.HomeActivity;
import com.example.simplerentalapplication.ui.PropertyDetailsActivity;
import com.example.simplerentalapplication.ui.RegisterActivity;

import java.io.Serializable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    APIInterface apiInterface;
    TextView loginUsername;
    TextView loginPassword;
    Button loginButton;
    TextView registerText;
    Intent intent;
    SharedPreferences prefs;
    String sessionID;
    String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginUsername = (TextView) findViewById(R.id.loginUsername);
        loginPassword = (TextView) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerText = (TextView) findViewById(R.id.registerText);
        registerText.setTextColor(Color.parseColor("#0064B8"));
        apiInterface = APIClient.getClient().create(APIInterface.class);

        prefs = getSharedPreferences("UserSessionID", MODE_PRIVATE);
        sessionID = prefs.getString("sessionId", "");
        userType = prefs.getString("userType", "");

        SharedPreferences.Editor editor = prefs.edit();

        if (!sessionID.equals("")) {
            if(userType.equals("user")) {
                intent = new Intent(MainActivity.this, HomeActivity.class);
            }
            else {
                intent = new Intent(MainActivity.this, AdminActivity.class);
            }
            startActivity(intent);
        } else {
            registerText.setOnClickListener(v -> {
                intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            });

            loginButton.setOnClickListener(v -> {
                if (TextUtils.isEmpty(loginUsername.getText())) {
                    loginUsername.setError("Please enter an username!");
                } else if (TextUtils.isEmpty(loginPassword.getText())) {
                    loginPassword.setError("Please enter a password!");
                } else {
                    User user = new User(loginUsername.getText().toString(), loginPassword.getText().toString());
                    Call<User> call1 = apiInterface.loginUser(user);
                    call1.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> call, Response<User> response) {
                            User responseUser = response.body();

                            if (response.code() == 200) {
                                editor.putString("sessionId", response.headers().get("Session-ID"));
                                editor.putString("username", response.body().userName);
                                editor.apply();

                                if(responseUser.userName.equals("admin")) {
                                    intent = new Intent(MainActivity.this, AdminActivity.class);
                                    editor.putString("userType","admin");
                                } else {
                                    intent = new Intent(MainActivity.this, HomeActivity.class);
                                    editor.putString("userType","user");
                                }
                                startActivity(intent);


                            } else {
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("Login Failed!")
                                        .setMessage(response.headers().get("Error-Message"))
                                        .setPositiveButton(android.R.string.yes, null)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        }

                        @Override
                        public void onFailure(Call<User> call, Throwable t) {
                            call.cancel();
                        }
                    });
                }
            });
        }
    }
}
