package com.example.simplerentalapplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.example.simplerentalapplication.MainActivity;
import com.example.simplerentalapplication.R;
import com.example.simplerentalapplication.adapter.PropertyAdapter;
import com.example.simplerentalapplication.model.Property;
import com.example.simplerentalapplication.rest.APIClient;
import com.example.simplerentalapplication.rest.APIInterface;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_admin);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.drawer_admin_menu, menu);
        return true;
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logoutAdmin:
                new AlertDialog.Builder(AdminActivity.this)
                        .setTitle("Logout!")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                editor.putString("sessionId", "");
                                editor.apply();
                                finish();
                                intent = new Intent(AdminActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_lock_lock)
                        .show();
                break;
            case R.id.addProperty:
                intent = new Intent(AdminActivity.this, PropertyModifyActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    private void apiCall(Call<List<Property>> call) {
        call.enqueue(new Callback<List<Property>>() {
            @Override
            public void onResponse(Call<List<Property>> call, Response<List<Property>> response) {

                if (response.code() == 200) {
                    propertyList = response.body();

                    recyclerView = findViewById(R.id.recyclerViewAdmin);
                    if (recyclerView != null) {
                        ((ViewGroup) recyclerView.getParent()).removeView(recyclerView); // <- fix
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(AdminActivity.this));
                    propertyAdapter = new PropertyAdapter(AdminActivity.this, propertyList, "admin_view");
                    recyclerView.setAdapter(propertyAdapter);

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_booking,
                            new PropertyFragment(recyclerView)).commit();

                } else if (response.code() == 403) {
                    new AlertDialog.Builder(AdminActivity.this)
                            .setTitle("Please Login!")
                            .setMessage(response.headers().get("Error-Message"))
                            .setPositiveButton("Login Now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    editor.putString("sessionId", "");
                                    editor.apply();
                                    finish();
                                    intent = new Intent(AdminActivity.this, MainActivity.class);
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