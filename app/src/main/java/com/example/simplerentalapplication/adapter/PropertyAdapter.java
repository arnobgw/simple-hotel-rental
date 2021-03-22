package com.example.simplerentalapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplerentalapplication.MainActivity;
import com.example.simplerentalapplication.R;
import com.example.simplerentalapplication.model.Property;
import com.example.simplerentalapplication.task.DownLoadImageTask;
import com.example.simplerentalapplication.ui.HomeActivity;
import com.example.simplerentalapplication.ui.PropertyDetailsActivity;
import com.example.simplerentalapplication.ui.PropertyModifyActivity;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {
    private final LayoutInflater layoutInflater;
    private final List<Property> propertyList;
    final String viewType;

    public PropertyAdapter(Context context, List<Property> propertyList, String viewType) {
        this.layoutInflater = LayoutInflater.from(context);
        this.propertyList = propertyList;
        this.viewType = viewType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.property_view, viewGroup, false);
        return new ViewHolder(view, propertyList.get(i), viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        // bind the textview with data received

        Property property = propertyList.get(i);
        Timestamp startTs = new Timestamp(property.leaseStartDate);
        Timestamp endTs = new Timestamp(property.leaseEndDate);
        viewHolder.propertyName.setText(property.name);
        viewHolder.propertyAddress.setText(property.address);
        viewHolder.propertyType.setText(property.leaseType);
        viewHolder.propertyDuration.setText("From " + startTs.toString().substring(0, 10) + " to " + endTs.toString().substring(0, 10));
        if (property.pictureUrls.size() != 0)
            new DownLoadImageTask(viewHolder.propertyImage).execute(property.pictureUrls.get(0));
        viewHolder.property = propertyList.get(i);
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        Property property;
        TextView propertyName;
        TextView propertyAddress;
        TextView propertyType;
        TextView propertyDuration;
        ImageView propertyImage;
        String propertyDisplayImageURL;
        Button detailsButton;
        String viewType;

        @SuppressLint("SetTextI18n")
        public ViewHolder(@NonNull View itemView, Property property, String viewType) {
            super(itemView);
            propertyName = itemView.findViewById(R.id.textTitle);
            propertyAddress = itemView.findViewById(R.id.textDesc);
            propertyImage = itemView.findViewById(R.id.imageView);
            detailsButton = itemView.findViewById(R.id.detailsButton);
            propertyType = itemView.findViewById(R.id.typeText);
            propertyDuration = itemView.findViewById(R.id.durationText);
            this.viewType = viewType;
            this.property = property;

            if (viewType.equals("property_fragment")) {
                detailsButton.setVisibility(View.VISIBLE);
                detailsButton.setText("View Details");
                detailsButton.setOnClickListener(this);

            } else if (viewType.equals("booking_details")) {
                Timestamp startTs = new Timestamp(property.leaseStartDate);
                Timestamp endTs = new Timestamp(property.leaseEndDate);
                propertyType.setVisibility(View.VISIBLE);
                propertyDuration.setVisibility(View.VISIBLE);

            } else if (viewType.equals("admin_view")) {
                detailsButton.setVisibility(View.VISIBLE);
                detailsButton.setText("Edit");
                detailsButton.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == detailsButton.getId() && viewType.equals("property_fragment")) {
                Intent intent = new Intent(v.getContext(), PropertyDetailsActivity.class);
                intent.putExtra("property", property);
                v.getContext().startActivity(intent);

            } else if (v.getId() == detailsButton.getId() && viewType.equals("admin_view")) {
                Intent intent = new Intent(v.getContext(), PropertyModifyActivity.class);
                intent.putExtra("property", property);
                v.getContext().startActivity(intent);
            }
        }
    }
}
