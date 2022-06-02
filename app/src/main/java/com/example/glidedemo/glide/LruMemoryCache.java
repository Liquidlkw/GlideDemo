package com.example.glidedemo.glide;

import android.os.Build;
import android.util.LruCache;

import androidx.core.graphics.BitmapCompat;

import com.example.glidedemo.glide.recycle.Resource;

public class LruMemoryCache extends LruCache<Key, Resource> implements MemoryCache {

    private ResourceRemoveListener resourceRemoveListener;

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public LruMemoryCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(Key key, Resource value) {
        //系统版本大于等于4.4时，getAllocationByteCount 代表（Memory usage of Bitmap）而 getByteCount 代表图片实际大小（小于等于Memory）
        //系统版本小于4.4时，用getByteCount（）bitmap实际大小

        //Android4.4之前版本内存复用要求width=width&&height=height&&inSampleSize = 1 新图片和老图片的宽高像素以及采样率（缩放）必须一致
        // Android4.4及之后版本只要被复用bitmap内存大于所需内存即可
        return BitmapCompat.getAllocationByteCount(value.getBitmap());
    }

    @Override
    protected void entryRemoved(boolean evicted, Key key, Resource oldValue, Resource newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        if (null != resourceRemoveListener && null != oldValue) {
            resourceRemoveListener.onResourceRemoved(oldValue);
        }
    }

    @Override
    public void setResourceRemoveListener(ResourceRemoveListener resourceRemoveListener) {
        this.resourceRemoveListener = resourceRemoveListener;
    }


}
