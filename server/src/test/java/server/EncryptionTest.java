package server;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncryptionTest {

    @Test
    void getHash() {
        Encryption encryption = new Encryption();
        String expected = "H";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            var bytes = md.digest(expected.getBytes(StandardCharsets.UTF_8));
            HexFormat hf = HexFormat.of();
            expected = hf.formatHex(bytes);
        }
        catch(NoSuchAlgorithmException e) {

        }
        assertEquals(expected, encryption.getHash("H"));

    }
    @Test
    void wrongAlgorithm(){
        Encryption encryption = new Encryption();
        encryption.setAlgorithm("h");
        assertEquals(null, encryption.getHash("h"));
        //assertEquals("SHA-256", encryption.getAlgorithm());
    }

    @Test
    void setAlgorithm() {
        Encryption encryption = new Encryption();
        encryption.setAlgorithm("h");
        assertEquals("h", encryption.getAlgorithm());
    }

    @Test
    void getAlgorithm(){
        Encryption encryption = new Encryption();
        assertEquals("SHA-256", encryption.getAlgorithm());
    }
}
