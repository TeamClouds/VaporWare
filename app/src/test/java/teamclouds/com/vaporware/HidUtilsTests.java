package teamclouds.com.vaporware;

import org.junit.Test;

import teamclouds.com.vaporware.hid.HidUtils;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class HidUtilsTests {
    @Test
    public void testReadCommand() {
        final byte[] bytes = HidUtils.generateCmd((byte) 0x35, 0, 2048);
        final String strbytes = bytesToHex(bytes);
        System.out.println(strbytes);
        assertEquals("350e00000000000800004849444363010000", strbytes);
    }

    private static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

}