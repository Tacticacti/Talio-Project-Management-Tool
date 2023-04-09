package commons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@SuppressWarnings("checkstyle:Indentation")
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

    @ElementCollection
    private Map<String, String> tags = new HashMap<>();

    // added to fix repo.findAll()
    public Board() {
        this.name = "";
        this.password = null;
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
        for (Card c : list.getCards())
            c.board = this;
        this.lists.add(list);
    }

    public void addBoardTag(String tag, String color) {
        tags.put(tag, color);
    }

    public void removeList(BoardList list) {
        this.lists.remove(list);
    }

    public void removeBoardTag(String tag) {
        this.tags.remove(tag);
    }

    // other helper methods
    public List<BoardList> getLists() {
        return lists;
    }

    public Map<String, String> getTagLists() {
        return tags;
    }

    public void addToList(int listId, Card card) {
        lists.get(listId).addCard(card);
    }


}
