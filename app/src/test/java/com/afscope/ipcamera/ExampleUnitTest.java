package com.afscope.ipcamera;

import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testParamsValidation(){
        System.out.println(CmdAndParamsCodec.isValidParametersStr(
                "pos=0&msqh=1&qydx=1&lever=4&verti=4&facus=1&qh=1&red=3&green=3&blue=3&qh=1&bright=4&zengyi=4&bin=1&skip=0&rate=10&"));
    }

    @Test
    public void testDate(){
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(1527730831370L);
        Date date = c.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        System.out.println(format.format(date));
    }
}