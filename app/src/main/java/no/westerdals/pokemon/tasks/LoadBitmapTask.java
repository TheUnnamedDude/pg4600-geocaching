package no.westerdals.pokemon.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

import no.westerdals.pokemon.model.Pokemon;

public class LoadBitmapTask extends AsyncTask<Pokemon, Void, Bitmap> {
    private BitmapCacheTask bitmapCacheTask;
    private ImageView imageView;

    public LoadBitmapTask(BitmapCacheTask bitmapCacheTask, ImageView imageView) {
        this.bitmapCacheTask = bitmapCacheTask;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Pokemon... params) {
        return bitmapCacheTask.getBitmap(params[0].getMongodbId(), params[0].getImageUrl());
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (!isCancelled()) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
