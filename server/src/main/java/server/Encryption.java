package server;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class Encryption {
    public String getHash(String x) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String res = md.digest(x.getBytes(StandardCharsets.UTF_8)).toString();
            return res;
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println(e);
            return null;
        }
    }
}
