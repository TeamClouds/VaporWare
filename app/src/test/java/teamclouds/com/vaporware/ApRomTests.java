package teamclouds.com.vaporware;

import android.util.ArraySet;

import junit.framework.Assert;

import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import teamclouds.com.vaporware.hid.ApRom;
import teamclouds.com.vaporware.hid.DataFlash;
import teamclouds.com.vaporware.hid.HidUtils;

/**
 * Created by roman on 5/14/16.
 */
public class ApRomTests {
    @Test
    public void testVerify() throws IOException {
        final ApRom apRom = loadTestRom("test_helloworld.bin");
        Set<String> set = new LinkedHashSet<>();
        set.add("E052");

        try {
            apRom.verify(set, 106);
            return; // all good
        } catch (ApRom.VerifyException e) {
            // try a convert
            apRom.convert();
        }

        Exception exception = null;
        try {
            apRom.verify(set, 106);
        } catch (ApRom.VerifyException e) {
            exception = e;
        }

        Assert.assertNull(exception);
    }

    private ApRom loadTestRom() throws IOException {
        return loadTestRom("test_aprom.bin");
    }

    private ApRom loadTestRom(String loc) throws IOException {
        InputStream in = ApRomTests.this.getClass().getClassLoader()
                .getResourceAsStream(loc);
        DataInputStream dataIs = new DataInputStream(in);

        byte[] raw = new byte[dataIs.available()];
        dataIs.readFully(raw);
        in.close();

        return new ApRom(raw);
    }

    @Test
    public void testVerifyFail1() throws IOException {
        final ApRom apRom = loadTestRom("test_helloworld.bin");

        Set<String> set = new LinkedHashSet<>();
        set.add("W007");
        Exception exception = null;
        try {
            apRom.verify(set, 106);
        } catch (ApRom.VerifyException e) {
            exception = e;
            e.printStackTrace();
        }
        Assert.assertNotNull(exception);
    }
}
