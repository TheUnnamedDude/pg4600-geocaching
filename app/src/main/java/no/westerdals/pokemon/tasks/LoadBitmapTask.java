package no.westerdals.pokemon.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

public class LoadBitmapTask extends AsyncTask<String, Void, Bitmap> {
    private BitmapCacheTask bitmapCacheTask;
    private ImageView imageView;

    public LoadBitmapTask(BitmapCacheTask bitmapCacheTask, ImageView imageView) {
        this.bitmapCacheTask = bitmapCacheTask;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        return bitmapCacheTask.getBitmap(params[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (!isCancelled()) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
