package commons;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Board {
    
    // instance variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String password;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name = "board_id")
    private List<BoardList> lists;

    // added to fix repo.findAll()
    public Board() {
        this.name = "";
        this.lists = new ArrayList<>();
    }

    // constructors
    public Board(String name) {
        this.name = name;
        this.password = null;
        this.lists = new ArrayList<>();
    }

    public Board(String name, String password) {
        this.name = name;
        this.password = password;
        this.lists = new ArrayList<>();
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(long board_id) {
        this.id = board_id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Board functionality
    public void addList(BoardList list) {
        list.board = this;
        for(Card c : list.getCards())
            c.board = this;
        this.lists.add(list);
    }

    public void removeList(BoardList list) {
        this.lists.remove(list);
    }

    // other helper methods
    public List<BoardList> getLists() {
        return lists;
    }

    public void addToList(int listId, Card card) {
        lists.get(listId).addCard(card);
    }


}
