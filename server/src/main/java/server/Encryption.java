package server;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class Encryption {

    private String algorithm = "SHA-256";
    public String getHash(String x) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            var bytes = md.digest(x.getBytes(StandardCharsets.UTF_8));
            HexFormat hf = HexFormat.of();
            return hf.formatHex(bytes);
        }
        catch(NoSuchAlgorithmException e) {
            System.out.println(e);
            return null;
        }
    }

    public void setAlgorithm(String al){
        algorithm = al;
    }

    public String getAlgorithm(){
        return algorithm;
    }

}
