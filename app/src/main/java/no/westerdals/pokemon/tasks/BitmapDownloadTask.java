package no.westerdals.pokemon.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

public class BitmapDownloadTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;

    public BitmapDownloadTask(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        try {
            return BitmapFactory.decodeStream(new URL(params[0]).openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
