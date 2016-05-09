package teamclouds.com.vaporware;

import junit.framework.Assert;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import teamclouds.com.vaporware.hid.DataFlash;
import teamclouds.com.vaporware.hid.HidUtils;

/**
 * Created by roman on 5/8/16.
 */
public class DataFlashTests {

    @Test
    public void testChecksum() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("test_dataflash.bin");
        DataInputStream dataIs = new DataInputStream(in);

        System.out.println("available bytes: " + dataIs.available());

        byte[] rawDataFlash = new byte[dataIs.available()];
        dataIs.readFully(rawDataFlash);
        in.close();

        final ByteBuffer wrap = ByteBuffer.wrap(rawDataFlash);
        wrap.order(ByteOrder.nativeOrder());
        Integer checksum = HidUtils.checkSum(wrap);


        DataFlash dataFlash = new DataFlash(wrap);
        System.out.println("data flash: " + dataFlash);

        Assert.assertTrue(dataFlash.verifyChecksum(checksum, rawDataFlash));

    }
}
