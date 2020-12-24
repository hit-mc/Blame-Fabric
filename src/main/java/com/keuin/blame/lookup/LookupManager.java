package com.keuin.blame.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class LookupManager {

    public static final LookupManager INSTANCE = new LookupManager();

    private final BlockingQueue<LookupFilterWithCallback> queue = new LinkedBlockingDeque<>();
    private final List<LookupWorker> workers = new ArrayList<>();

    private LookupManager() {
        // initialize workers
        for (int i = 0; i < 10; ++i) {
            LookupWorker worker = new LookupWorker(i, queue);
            worker.start();
            workers.add(worker);
        }
    }

    public void stop() {
        workers.forEach(LookupWorker::disable);
    }

    public void lookup(AbstractLookupFilter filter, LookupCallback callback) {
        queue.add(new LookupFilterWithCallback(callback, filter));
    }

}
