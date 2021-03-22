package com.example.simplerentalapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.simplerentalapplication.MainActivity;
import com.example.simplerentalapplication.R;
import com.example.simplerentalapplication.adapter.PropertyAdapter;
import com.example.simplerentalapplication.model.Property;
import com.example.simplerentalapplication.model.User;
import com.example.simplerentalapplication.rest.APIClient;
import com.example.simplerentalapplication.rest.APIInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    String sessionID;
    String userName;
    RecyclerView recyclerView;
    PropertyAdapter propertyAdapter;
    List<Property> propertyList;
    APIInterface apiInterface;
    Intent intent;
    BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefs = getSharedPreferences("UserSessionID", MODE_PRIVATE);
        sessionID = prefs.getString("sessionId", "");
        userName = prefs.getString("username", "");
        editor = prefs.edit();
        editor.apply();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            Call<List<Property>> call = apiInterface.getAllAvailableRentPropertiesType(sessionID);
            apiCall(call);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logout:
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Logout!")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putString("sessionId", "");
                                editor.apply();
                                finish();
                                intent = new Intent(HomeActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_lock_lock)
                        .show();
                break;
            case R.id.bookingDetails:
                intent = new Intent(HomeActivity.this, BookingActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void apiCall(Call<List<Property>> call) {
        call.enqueue(new Callback<List<Property>>() {
            @Override
            public void onResponse(Call<List<Property>> call, Response<List<Property>> response) {
                propertyList = response.body();

                if (response.code() == 200) {
                    recyclerView = findViewById(R.id.recyclerView);
                    if (recyclerView != null) {
                        ((ViewGroup) recyclerView.getParent()).removeView(recyclerView); // <- fix
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                    propertyAdapter = new PropertyAdapter(HomeActivity.this, propertyList, "property_fragment");
                    recyclerView.setAdapter(propertyAdapter);

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new PropertyFragment(recyclerView)).commit();

                } else if (response.code() == 403) {
                    new AlertDialog.Builder(HomeActivity.this)
                            .setTitle("Please Login!")
                            .setMessage(response.headers().get("Error-Message"))
                            .setPositiveButton("Login Now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    editor.putString("sessionId", "");
                                    editor.apply();
                                    finish();
                                    intent = new Intent(HomeActivity.this, MainActivity.class);
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

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Call<List<Property>> call = null;
                    switch (item.getItemId()) {
                        case R.id.nav_rent:
                            call = apiInterface.getAllAvailableRentPropertiesType(sessionID);
                            break;
                        case R.id.nav_purchase:
                            call = apiInterface.getAllAvailablePurchasePropertiesType(sessionID);

                            break;
                        case R.id.nav_venue:
                            call = apiInterface.getAllAvailableVenuePropertiesType(sessionID);
                            break;
                    }
                    apiCall(call);
                    return true;
                }
            };
}