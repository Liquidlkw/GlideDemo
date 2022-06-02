package com.example.glidedemo.glide;

import com.example.glidedemo.glide.recycle.Resource;

public interface MemoryCache {

    interface ResourceRemoveListener{
        void onResourceRemoved(Resource resource);
    }


    void setResourceRemoveListener(ResourceRemoveListener resourceRemoveListener);
}
