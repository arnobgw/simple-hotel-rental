package com.example.simplerentalapplication.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplerentalapplication.MainActivity;
import com.example.simplerentalapplication.R;
import com.example.simplerentalapplication.adapter.PropertyAdapter;
import com.example.simplerentalapplication.model.Property;
import com.example.simplerentalapplication.rest.APIClient;
import com.example.simplerentalapplication.rest.APIInterface;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingActivity extends AppCompatActivity {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    RecyclerView recyclerView;
    String sessionID;
    String userName;
    PropertyAdapter propertyAdapter;
    APIInterface apiInterface;
    List<Property> propertyList;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        propertyList = new ArrayList<>();
        prefs = getSharedPreferences("UserSessionID", MODE_PRIVATE);
        sessionID = prefs.getString("sessionId", "");
        userName = prefs.getString("username", "");
        editor = prefs.edit();
        editor.apply();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<List<Property>> call = apiInterface.getAllProperties(sessionID);
        apiCall(call);
    }

    private void apiCall(Call<List<Property>> call) {
        call.enqueue(new Callback<List<Property>>() {
            @Override
            public void onResponse(Call<List<Property>> call, Response<List<Property>> response) {
                System.out.println(userName);

                if (response.code() == 200) {
                    for (Property property : response.body()) {
                        if (property.userName != null) {
                            if (property.userName.equals(userName)) {
                                propertyList.add(property);
                            }
                        }
                    }

                    recyclerView = findViewById(R.id.recyclerViewBooking);
                    if (recyclerView != null) {
                        ((ViewGroup) recyclerView.getParent()).removeView(recyclerView); // <- fix
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(BookingActivity.this));
                    propertyAdapter = new PropertyAdapter(BookingActivity.this, propertyList, "booking_details");
                    recyclerView.setAdapter(propertyAdapter);

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_booking,
                            new PropertyFragment(recyclerView)).commit();

                } else if (response.code() == 403) {
                    new AlertDialog.Builder(BookingActivity.this)
                            .setTitle("Please Login!")
                            .setMessage(response.headers().get("Error-Message"))
                            .setPositiveButton("Login Now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    editor.putString("sessionId", "");
                                    editor.apply();
                                    finish();
                                    intent = new Intent(BookingActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            })
                            .setIcon(android.R.drawable.ic_input_add)
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<Property>> call, Throwable t) {
                call.cancel();
            }
        });
    }
}