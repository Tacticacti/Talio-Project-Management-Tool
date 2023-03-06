package server.api;

import java.util.List;
import commons.Board;
import server.database.BoardRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardRepository repo;

    public BoardController(BoardRepository repo) {
        System.err.println("hello from controller");
        this.repo = repo;
        Board test = new Board("test");
        repo.save(test);
    }
    
    @GetMapping(path = {"", "/"})
    public String getAll() {
        List<Board> list = repo.findAll();
        String res = "";
        for(Board b : list)
            res += b + "<br> \n";
        return res;
    }

}
