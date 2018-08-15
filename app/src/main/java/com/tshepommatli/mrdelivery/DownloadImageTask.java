package com.tshepommatli.mrdelivery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

/**
 * Created by Tshepo on 04/02/2018.
 *
 * Download image from server and set it to Image View
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap>
{
    private ImageView imgView;

    public DownloadImageTask(ImageView imageView)
    {
        this.imgView = imageView;

    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    @Override
    protected void onPostExecute(Bitmap result)
    {
        super.onPostExecute(result);

        this.imgView.setImageBitmap(result);
    }

}
