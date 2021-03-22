package com.example.simplerentalapplication.task;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

public class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
    @SuppressLint("StaticFieldLeak")
    ImageView imageView;

    public DownLoadImageTask(ImageView imageView) {
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlOfImage = urls[0];
        Bitmap logo = null;
        try {
            InputStream is = new URL(urlOfImage).openStream();

            logo = BitmapFactory.decodeStream(is);
        } catch (Exception e) { // Catch the download exception
            e.printStackTrace();
        }
        return logo;
    }

    protected void onPostExecute(Bitmap result) {
        imageView.setImageBitmap(result);
    }
}