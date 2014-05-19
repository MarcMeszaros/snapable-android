package ca.hashbrown.snapable.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertTrue;

// Test class for MyActivity
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class NetworkTest {

    @Test
    public void connectionAvailable() throws Exception {
        assertTrue(true);
    }
}