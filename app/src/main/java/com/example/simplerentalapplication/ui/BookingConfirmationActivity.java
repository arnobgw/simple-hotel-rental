package com.example.simplerentalapplication.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.simplerentalapplication.MainActivity;
import com.example.simplerentalapplication.R;
import com.example.simplerentalapplication.model.Property;
import com.example.simplerentalapplication.model.User;
import com.example.simplerentalapplication.rest.APIClient;
import com.example.simplerentalapplication.rest.APIInterface;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingConfirmationActivity extends AppCompatActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TextView endDateText;
    Button endDateBtn;
    Button confirmButton;
    String propertyId;
    String propertyLeaseType;
    long endDate;
    Bundle extras;
    String sessionID;
    APIInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        endDateText = findViewById(R.id.endDateView);
        confirmButton = findViewById(R.id.confirmButton);
        endDateBtn = findViewById(R.id.endDateBtn);
        apiInterface = APIClient.getClient().create(APIInterface.class);

        prefs = getSharedPreferences("UserSessionID", MODE_PRIVATE);
        sessionID = prefs.getString("sessionId", "");
        propertyId = prefs.getString("propertyId", "");
        propertyLeaseType = prefs.getString("propertyLeaseType", "");
        editor = prefs.edit();
        editor.apply();

        extras = getIntent().getExtras();
        if (extras != null) {
            propertyId = extras.getString("propertyId");
        }

        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 1);
        long now = today.getTimeInMillis();

        DatePickerDialog.OnDateSetListener endDatePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDateText.setText(dayOfMonth + "/" + (month+1) + "/" + year);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                endDate = calendar.getTimeInMillis();
            }
        };

        DatePickerDialog endDatePickerDialog = new DatePickerDialog(BookingConfirmationActivity.this, endDatePickerListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH));

        endDatePickerDialog.getDatePicker().setMinDate(now);

        endDateBtn.setOnClickListener(v -> {
            endDatePickerDialog.show();
        });

        confirmButton.setOnClickListener(v -> {
            new AlertDialog.Builder(BookingConfirmationActivity.this)
                    .setTitle("Booking Confirmation!")
                    .setMessage("Are you sure you want to book until " + endDateText.getText().toString() + "?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if(propertyLeaseType.equals("PURCHASE"))
                                endDate = 0;
                                Call<Property> call1 = apiInterface.setBookingEndDate(Integer.parseInt(propertyId), endDate, sessionID);
                            call1.enqueue(new Callback<Property>() {
                                @Override
                                public void onResponse(Call<Property> call, Response<Property> response) {
                                    finish();
                                    Intent gotoScreenVar = new Intent(BookingConfirmationActivity.this, HomeActivity.class);
                                    gotoScreenVar.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(gotoScreenVar);
                                }

                                @Override
                                public void onFailure(Call<Property> call, Throwable t) {
                                    call.cancel();
                                }
                            });
                        }
                    })
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_input_add)
                    .show();
        });
    }
}