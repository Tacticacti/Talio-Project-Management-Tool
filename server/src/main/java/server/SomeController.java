package server;

import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/")
public class SomeController {

    private final Admin admin;
    public SomeController(Admin admin) {
        this.admin = admin;
    }

    @GetMapping(path = {"", "/", "/TalioPresent"})
    public ResponseEntity<String> talioPresenceCheck() {
        return ResponseEntity.ok("Welcome to Talio!");
    }

    @PostMapping(path = "/adminLogin")
    public ResponseEntity<Boolean> checkAdminPassword(@RequestBody String psswd) {
        if(psswd == null || psswd.equals(""))
            return ResponseEntity.ok(false);

        // wait around 0.5 sec to avoid bruteforce attack
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        if(admin.getPassword().equals(psswd))
            return ResponseEntity.ok(true);
        return ResponseEntity.ok(false);
    }
}
