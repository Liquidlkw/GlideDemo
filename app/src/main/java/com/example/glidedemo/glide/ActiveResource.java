package com.example.glidedemo.glide;

import com.example.glidedemo.glide.recycle.Resource;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class ActiveResource {


    private Map<Key, ResourceWeakReference> weakReferenceMap = new HashMap<>();

    private ReferenceQueue<? super Resource> referenceQueue;

    private Thread cleanReferenceQueueThread;


    private Resource.ResourceListener resourceListener;


    public ActiveResource(Resource.ResourceListener resourceListener) {
        this.resourceListener = resourceListener;
    }

    /**
     * 加入活动缓存
     *
     * @param key
     * @param resource
     */
    public void activate(Key key, Resource resource) {
        if (resourceListener != null) resource.setResourceListener(key, resourceListener);
        weakReferenceMap.put(key, new ResourceWeakReference(key, resource, getReferenceQueue()));
    }

    /**
     * 移除活动缓存
     * @param key
     * @return
     */

    public Resource deactivate(Key key) {
        ResourceWeakReference remove = weakReferenceMap.remove(key);
        if (remove != null) {
            return remove.get();
        }
        return null;
    }


    private ReferenceQueue<? super Resource> getReferenceQueue() {
        if (null == referenceQueue) {
            referenceQueue = new ReferenceQueue<>();
            cleanReferenceQueueThread = new Thread(() -> {
                try {
                    //referenceQueue.remove()内部利用锁+死循环实现，当ResourceWeakReference被GC时回回调
                    ResourceWeakReference removedRef = (ResourceWeakReference) referenceQueue.remove();
                    //这里removedRef对应的value 已经被回收为null，此处是反向把对应的entry[<Key, ResourceWeakReference>]删掉
                    weakReferenceMap.remove(removedRef.key);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            cleanReferenceQueueThread.start();
        }


        return referenceQueue;
    }


    public void shutdown(){
        if (cleanReferenceQueueThread!=null){
            cleanReferenceQueueThread.interrupt();
            try {
                //我感觉没必要。。。
                cleanReferenceQueueThread.join(5000);
                if (cleanReferenceQueueThread.isAlive()){
                    throw new RuntimeException(" Shut down ailed after 5s");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    static final class ResourceWeakReference extends WeakReference<Resource> {
        final Key key;

        public ResourceWeakReference(Key key, Resource referent, ReferenceQueue<? super Resource> q) {
            super(referent, q);
            this.key = key;
        }
    }

}
