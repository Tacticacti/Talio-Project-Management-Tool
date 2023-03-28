package server;

import org.springframework.stereotype.Controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class SomeController {

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello world!";
    }

    @GetMapping(path = "/TalioPresent")
    public ResponseEntity<String> talioPresenceCheck() {
        return ResponseEntity.ok("Welcome to Talio!");
    }
}
