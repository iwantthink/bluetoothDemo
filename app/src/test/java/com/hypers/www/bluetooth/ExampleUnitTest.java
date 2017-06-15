package com.hypers.www.bluetooth;

import com.hypers.www.bluetooth.home.p.HomePresent;
import com.hypers.www.bluetooth.home.v.HomeActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static junit.framework.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Mock
    HomeActivity mHomeActivity;

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void atomicIntegerTest() {
        AtomicInteger integer = new AtomicInteger();
        integer.compareAndSet(0, 2);
        assertEquals(2, integer.get());
//        Log.d("HomeActivity", "integer.get():" + integer.get());

        AtomicReference atomicReference;
    }

    private HomePresent mHomePresent;

    @Before
    public void setUp() {
        mHomePresent = new HomePresent(mHomeActivity);
    }

    @Test
    public void checkHomePresent() {
        mHomePresent.openBle();
        Mockito.verify(mHomePresent).openBle();
    }


}