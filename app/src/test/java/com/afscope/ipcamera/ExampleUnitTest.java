package com.afscope.ipcamera;

import com.afscope.ipcamera.wscontroller.CmdAndParamsCodec;

import org.junit.Test;

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
        System.out.println(CmdAndParamsCodec.isValidParametersStr("adi=9238&fs=8a"));
    }
}