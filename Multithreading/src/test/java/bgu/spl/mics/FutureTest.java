package bgu.spl.mics;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FutureTest {
    private Future<String> future;

    @Before
    public void setUp() {
        future = new Future<>();
    }

    @Test  //tests get, resolve and isDone methods
    public void get() {
        String a = "testme";
        future.resolve(a);
        assertTrue(future.isDone());
        assertEquals(a, future.get());
    }

    @Test
    public void testGet() {
        String a = "testme";
        future.resolve(a);
        assertTrue(a.equals(future.get(1000, TimeUnit.MILLISECONDS)));
    }
}