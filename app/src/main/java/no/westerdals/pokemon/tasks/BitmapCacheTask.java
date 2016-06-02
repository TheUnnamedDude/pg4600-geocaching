package no.westerdals.pokemon.tasks;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.jakewharton.disklrucache.DiskLruCache;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BitmapCacheTask extends AsyncTask<String, Void, Void> {
    private Activity activity;
    private DiskLruCache diskCache;
    ReentrantLock lock = new ReentrantLock();
    Condition condition = lock.newCondition();

    public BitmapCacheTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        lock.lock();
        try {
            File cacheDir = new File(activity.getCacheDir(), params[0]);
            diskCache = DiskLruCache.open(cacheDir, 1, 1, 1024*1024*10);
            condition.signalAll();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return null;
    }

    public Bitmap getBitmap(String key, String url) {
        lock.lock();
        try {
            waitForCache();
            DiskLruCache.Snapshot snapshot = diskCache.get(key);
            if (snapshot == null) {
                // Download
                Bitmap bitmap = getScaledBitmap(BitmapFactory.decodeStream(new URL(url).openStream()));
                DiskLruCache.Editor editor = diskCache.edit(key);
                OutputStream outputStream = editor.newOutputStream(0);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
                editor.commit();
                return bitmap;
            }
            return BitmapFactory.decodeStream(diskCache.get(key).getInputStream(0));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        } finally {
            lock.unlock();
        }
    }

    private Bitmap getScaledBitmap(Bitmap sourceBitmap) {
        if (sourceBitmap.getWidth() <= 512 && sourceBitmap.getHeight() <= 512)
            return sourceBitmap;
        int width;
        int height;
        if (sourceBitmap.getWidth() > sourceBitmap.getHeight()) {
            double scaleFactor = ((double) sourceBitmap.getWidth() / 512.0);
            width = 512;
            height = (int) (((double)sourceBitmap.getHeight()) / scaleFactor);
        } else {
            double scaleFactor = ((double) sourceBitmap.getHeight() / 512.0);
            width = (int) (((double)sourceBitmap.getWidth()) / scaleFactor);
            height = 512;
        }
        return Bitmap.createScaledBitmap(sourceBitmap, width, height, false);
    }

    private void waitForCache() throws InterruptedException {
        while (diskCache == null) {
            condition.await();
        }
    }
}
