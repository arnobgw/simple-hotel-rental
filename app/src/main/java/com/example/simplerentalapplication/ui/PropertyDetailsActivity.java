package com.example.simplerentalapplication.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.simplerentalapplication.MainActivity;
import com.example.simplerentalapplication.R;
import com.example.simplerentalapplication.model.Property;
import com.example.simplerentalapplication.task.DownLoadImageTask;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PropertyDetailsActivity extends AppCompatActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Property property;
    TextView propertyName;
    TextView addressValue;
    TextView typeValue;
    TextView landlordName;
    TextView landlordPhone;
    TextView cost;
    ImageView imageView;
    List<String> urls;
    LinearLayout imageViewLayout;
    Button bookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);

        property = (Property) getIntent().getSerializableExtra("property");
        propertyName = (TextView) findViewById(R.id.propertyName);
        addressValue = (TextView) findViewById(R.id.addressValue);
        typeValue = (TextView) findViewById(R.id.typeValue);
        landlordName = (TextView) findViewById(R.id.landlordNameValue);
        landlordPhone = (TextView) findViewById(R.id.landlordPhoneValue);
        cost = (TextView) findViewById(R.id.costValue);
        imageViewLayout = (LinearLayout) findViewById(R.id.imgageViewLayout);
        bookButton = (Button) findViewById(R.id.bookButton);
        imageView = new ImageView(PropertyDetailsActivity.this);
        urls = new ArrayList<>();

        propertyName.setText(property.name);
        addressValue.setText(property.address);
        typeValue.setText(property.propertyType);
        landlordName.setText(property.contactPerson);
        landlordPhone.setText(property.contactPhone);
        urls = property.pictureUrls;

        prefs = getSharedPreferences("UserSessionID", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        if (property.timeUnit.equals("NONE"))
            cost.setText(property.cost);
        else
            cost.setText(property.cost + "/" + property.timeUnit);

        switch (property.leaseType) {
            case "RENT":
                bookButton.setText("RENT");
                break;
            case "PURCHASE":
                bookButton.setText("BUY");
                break;
            case "VENUE":
                bookButton.setText("BOOK");
                break;
        }

        for(String url : urls) {
            imageView = new ImageView(PropertyDetailsActivity.this);
            new DownLoadImageTask(imageView).execute(url);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(500, 500);
            imageView.setLayoutParams(lp);
            imageViewLayout.addView(imageView);
        }

        bookButton.setOnClickListener(v -> {
            editor.putString("propertyId", Integer.toString(property.id));
            editor.putString("propertyLeaseType", property.leaseType);
            editor.apply();
            finish();
            Intent intent = new Intent(PropertyDetailsActivity.this, BookingConfirmationActivity.class);
            startActivity(intent);
        });
    }
}