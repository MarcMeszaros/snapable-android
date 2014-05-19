package ca.hashbrown.snapable.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class NetworkTest {

    @Test
    public void connectionAvailable() throws Exception {
        Network net = new Network(Robolectric.application.getApplicationContext());
        assertTrue(!net.isConnected());
    }
}