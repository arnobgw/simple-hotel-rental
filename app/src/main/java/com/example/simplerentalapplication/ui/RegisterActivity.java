package com.example.simplerentalapplication.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import com.example.simplerentalapplication.MainActivity;
import com.example.simplerentalapplication.R;
import com.example.simplerentalapplication.model.User;
import com.example.simplerentalapplication.rest.APIClient;
import com.example.simplerentalapplication.rest.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    APIInterface apiInterface;
    TextView registerUsername;
    TextView registerName;
    TextView registerEmail;
    TextView registerPassword;
    TextView registerConfirmPassword;
    Button registerButton;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerUsername = (TextView) findViewById(R.id.registerUsername);
        registerName = (TextView) findViewById(R.id.registerName);
        registerEmail = (TextView) findViewById(R.id.registerEmail);
        registerPassword = (TextView) findViewById(R.id.registerPassword);
        registerConfirmPassword = (TextView) findViewById(R.id.registerConfirmPassword);
        registerButton = (Button) findViewById(R.id.registerButton);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        registerButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(registerUsername.getText())) {
                registerUsername.setError("Please enter an username!");
            } else if (TextUtils.isEmpty(registerName.getText())) {
                registerName.setError("Please enter a name!");
            } else if (TextUtils.isEmpty(registerEmail.getText())) {
                registerEmail.setError("Please enter an email!");
            } else if (TextUtils.isEmpty(registerPassword.getText())) {
                registerPassword.setError("Please enter a password!");
            } else if (TextUtils.isEmpty(registerConfirmPassword.getText())) {
                registerConfirmPassword.setError("Please confirm your password!");
            } else if (!registerConfirmPassword.getText().toString().equals(registerPassword.getText().toString())) {
                registerConfirmPassword.setError("Password doesn't match!");
            } else {
                User user = new User(registerUsername.getText().toString(), registerName.getText().toString(),
                        registerEmail.getText().toString(), registerPassword.getText().toString());

                Call<User> call1 = apiInterface.registerUser(user);
                call1.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User responseUser = response.body();

                        if (response.code() == 200) {
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle("Registration Successful!")
                                    .setMessage(response.headers().get("Error-Message"))
                                    .setPositiveButton("Login Now", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                                    .setIcon(android.R.drawable.ic_input_add)
                                    .show();
                        } else {
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle("Registration Failed!")
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