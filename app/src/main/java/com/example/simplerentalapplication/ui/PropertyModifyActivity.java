package com.example.simplerentalapplication.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.simplerentalapplication.MainActivity;
import com.example.simplerentalapplication.R;
import com.example.simplerentalapplication.model.ImageURL;
import com.example.simplerentalapplication.model.Property;
import com.example.simplerentalapplication.model.User;
import com.example.simplerentalapplication.rest.APIClient;
import com.example.simplerentalapplication.rest.APIInterface;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PropertyModifyActivity extends AppCompatActivity {

    APIInterface apiInterface;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TextView propertyName;
    Spinner propertyType;
    Spinner propertyLeaseType;
    TextView propertyImageURL;
    TextView propertyAddress;
    TextView propertyContactPerson;
    TextView propertyContactPhone;
    TextView propertyCost;
    Button urlAddBtn;
    Button saveBtn;
    Button rmvPropertyBtn;
    Spinner dropdown;
    String[] items;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapterLease;
    ArrayAdapter<String> adapterPropertyType;
    ListView urlListView;
    List<String> listItems;
    ArrayAdapter<String> URLAdapter;
    Property property;
    String sessionID;
    String editType;
    String[] itemsLease;
    String[] itemsPropertyType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_modify);

        items = new String[]{"NONE", "DAY", "WEEK", "MONTH"};
        itemsPropertyType = new String[]{"FLAT", "HOUSE", "OFFICE", "COMMUNITY_CENTER", "HALL"};
        itemsLease = new String[]{"RENT", "PURCHASE", "VENUE"};

        listItems = new ArrayList<>();

        apiInterface = APIClient.getClient().create(APIInterface.class);

        prefs = getSharedPreferences("UserSessionID", MODE_PRIVATE);
        sessionID = prefs.getString("sessionId", "");
        editor = prefs.edit();
        editor.apply();

        property = (Property) getIntent().getSerializableExtra("property");
        propertyName = findViewById(R.id.editPropertyName);
        propertyType = findViewById(R.id.spinnerPropertyType);
        propertyLeaseType = findViewById(R.id.spinnerPropertyLease);
        propertyAddress = findViewById(R.id.editAddress);
        propertyContactPerson = findViewById(R.id.editContactPerson);
        propertyContactPhone = findViewById(R.id.editContactPhone);
        propertyCost = findViewById(R.id.editCost);
        propertyImageURL = findViewById(R.id.editURL);
        urlAddBtn = findViewById(R.id.addURLBtn);
        saveBtn = findViewById(R.id.saveBtn);
        rmvPropertyBtn = findViewById(R.id.rmvPropertyBtn);
        urlListView = findViewById(R.id.urlListView);
        dropdown = findViewById(R.id.spinnerCostType);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        adapterPropertyType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsPropertyType);
        propertyType.setAdapter(adapterPropertyType);

        adapterLease = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, itemsLease);
        propertyLeaseType.setAdapter(adapterLease);

        if (property != null) {
            propertyName.setText(property.name);
            propertyAddress.setText(property.address);
            propertyContactPerson.setText(property.contactPerson);
            propertyContactPhone.setText(property.contactPhone);
            propertyCost.setText(property.cost);
            listItems = property.pictureUrls;
            rmvPropertyBtn.setVisibility(View.VISIBLE);
            propertyImageURL.setVisibility(View.VISIBLE);
            urlAddBtn.setVisibility(View.VISIBLE);
            urlListView.setVisibility(View.VISIBLE);
            dropdown.setSelection(adapter.getPosition(property.timeUnit));
            propertyType.setSelection(adapterPropertyType.getPosition(property.propertyType));
            propertyLeaseType.setSelection(adapterLease.getPosition(property.leaseType));
            editType = "update";
        }
        else {
            rmvPropertyBtn.setVisibility(View.INVISIBLE);
            propertyImageURL.setVisibility(View.INVISIBLE);
            urlAddBtn.setVisibility(View.INVISIBLE);
            urlListView.setVisibility(View.INVISIBLE);
            editType = "add";
        }

        URLAdapter = new ArrayAdapter<>(this,
                R.layout.url_textview,
                listItems);
        urlListView.setAdapter(URLAdapter);

        urlAddBtn.setOnClickListener(v -> {
            ImageURL url = new ImageURL(propertyImageURL.getText().toString());
            Call<Property> call1 = apiInterface.addPropertyImageURL(property.id, sessionID, url);
            call1.enqueue(new Callback<Property>() {
                @Override
                public void onResponse(Call<Property> call, Response<Property> response) {
                    if (response.code() == 200) {
                        listItems.add(propertyImageURL.getText().toString());
                        propertyImageURL.setText("");
                        URLAdapter.notifyDataSetChanged();
                    } else {
                        new AlertDialog.Builder(PropertyModifyActivity.this)
                                .setTitle("Adding Image URL Failed!")
                                .setMessage(response.headers().get("Error-Message"))
                                .setPositiveButton("Ok", null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }

                @Override
                public void onFailure(Call<Property> call, Throwable t) {
                    call.cancel();
                }
            });
        });

        saveBtn.setOnClickListener(v -> {
            Property newProperty = new Property(propertyName.getText().toString(), propertyType.getSelectedItem().toString(), propertyLeaseType.getSelectedItem().toString(), propertyAddress.getText().toString(), propertyContactPerson.getText().toString(), propertyContactPhone.getText().toString(), propertyCost.getText().toString(), dropdown.getSelectedItem().toString(), listItems);

            if(editType.equals("add")) {
                new AlertDialog.Builder(PropertyModifyActivity.this)
                        .setTitle("Add Property!")
                        .setMessage("Are you sure you want to add " + newProperty.name + " in the property list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Call<Property> call1 = apiInterface.addProperty(sessionID, newProperty);
                                call1.enqueue(new Callback<Property>() {
                                    @Override
                                    public void onResponse(Call<Property> call, Response<Property> response) {
                                        if (response.code() == 200) {
                                            new AlertDialog.Builder(PropertyModifyActivity.this)
                                                    .setTitle("Adding Property Successful!")
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                            Intent intent = new Intent(PropertyModifyActivity.this, AdminActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_input_add)
                                                    .show();
                                        } else {
                                            new AlertDialog.Builder(PropertyModifyActivity.this)
                                                    .setTitle("Adding Property Failed!")
                                                    .setMessage(response.headers().get("Error-Message"))
                                                    .setPositiveButton("Ok", null)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Property> call, Throwable t) {
                                        call.cancel();
                                    }
                                });
                            }
                        })
                        .setIcon(android.R.drawable.ic_input_add)
                        .setNegativeButton("No", null)
                        .show();
            }
            else {
                new AlertDialog.Builder(PropertyModifyActivity.this)
                        .setTitle("Update Property!")
                        .setMessage("Are you sure you want to update " + propertyName.getText() + " in the property list?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Call<Property> call1 = apiInterface.updateProperty(property.id, sessionID, newProperty);
                                call1.enqueue(new Callback<Property>() {
                                    @Override
                                    public void onResponse(Call<Property> call, Response<Property> response) {
                                        if (response.code() == 200) {
                                            new AlertDialog.Builder(PropertyModifyActivity.this)
                                                    .setTitle("Adding Property Successful!")
                                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                            Intent intent = new Intent(PropertyModifyActivity.this, AdminActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .setIcon(android.R.drawable.ic_input_add)
                                                    .show();
                                        } else {
                                            new AlertDialog.Builder(PropertyModifyActivity.this)
                                                    .setTitle("Updating Property Failed!")
                                                    .setMessage(response.headers().get("Error-Message"))
                                                    .setPositiveButton("Ok", null)
                                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                                    .show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Property> call, Throwable t) {
                                        call.cancel();
                                    }
                                });
                            }
                        })
                        .setIcon(android.R.drawable.ic_input_add)
                        .setNegativeButton("No", null)
                        .show();
            }

        });

        rmvPropertyBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(PropertyModifyActivity.this)
                    .setTitle("Delete Property!")
                    .setMessage("Are you sure you want to delete " + propertyName.getText() + " from the property list?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Call<ResponseBody> call1 = apiInterface.removeProperty(property.id, sessionID);
                            call1.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    System.out.println(response.code());
                                    if (response.code() == 200) {
                                        finish();
                                        Intent intent = new Intent(PropertyModifyActivity.this, AdminActivity.class);
                                        startActivity(intent);
                                    }
                                    else {
                                        new AlertDialog.Builder(PropertyModifyActivity.this)
                                                .setTitle("Deleting Property Failed!")
                                                .setMessage(response.headers().get("Error-Message"))
                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if(response.headers().get("Error-Message").equals("Property not found.")) {
                                                            finish();
                                                            Intent intent = new Intent(PropertyModifyActivity.this, AdminActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    }
                                                })
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    call.cancel();
                                }
                            });
                        }
                    })
                    .setIcon(android.R.drawable.ic_input_add)
                    .setNegativeButton("No", null)
                    .show();
        });
    }
}