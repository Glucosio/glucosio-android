package org.glucosio.android;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.manifest.BroadcastReceiverData;

import java.util.ArrayList;
import java.util.List;

public class GlucosioTestRunner extends RobolectricGradleTestRunner {
    public GlucosioTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected AndroidManifest getAppManifest(org.robolectric.annotation.Config config) {
        AndroidManifest manifest = super.getAppManifest(config);
        List<BroadcastReceiverData> broadcastReceivers = manifest.getBroadcastReceivers();
        List<BroadcastReceiverData> removeList = new ArrayList<>();
        for (BroadcastReceiverData receiverData : broadcastReceivers) {
            if (receiverData.getClassName().toLowerCase().contains("instabug")) {
                removeList.add(receiverData);
            }
        }
        broadcastReceivers.removeAll(removeList);
        return manifest;
    }
}
