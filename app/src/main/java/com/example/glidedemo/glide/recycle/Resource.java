package com.example.glidedemo.glide.recycle;

import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.glidedemo.glide.Key;

public class Resource {
    private Bitmap bitmap;

    /**
     * bitmap reference count
     * <p>
     * remove the bitmap to memoryCache and call
     * {@link Resource.ResourceListener#onResourceReleased(Key,Resource)} API
     * if acquired == 0
     */
    private int acquired;

    private ResourceListener resourceListener;

    private Key key;

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public interface ResourceListener {
        void onResourceReleased(Key key, Resource resource);
    }

    public void setResourceListener(Key key, ResourceListener resourceListener) {
        this.resourceListener = resourceListener;
        this.key = key;
    }

    public void release() {
        if (--acquired == 0) {
            resourceListener.onResourceReleased(this.key, this);
        }
    }

    public void acquire() {
        if (bitmap.isRecycled()) {
            throw new IllegalStateException("Acquire a recycled resource");
        }
        acquired++;
    }

    public void recycle() {
        if (acquired > 0) return;
        if (!bitmap.isRecycled()) bitmap.recycle();
    }


}
