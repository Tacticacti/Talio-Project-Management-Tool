package server;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class Admin {
    private String psswd;

    public Admin() {

        Random rng = new Random();

        psswd = rng.ints('a', 'z').limit(20)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    @Bean
    public String getPassword() {
        return psswd;
    }
}
