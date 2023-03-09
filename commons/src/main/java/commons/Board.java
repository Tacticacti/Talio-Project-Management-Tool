package commons;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class Board {
    
    // instance variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long board_id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private List<BoardList> lists;

    // added to fix repo.findAll()
    public Board() {}

    // constructor
    public Board(String name) {
        this.name = name;
        this.lists = new ArrayList<>();
    }

    // getters and setters
    public Long getId() {
        return board_id;
    }

    public void setId(long board_id) {
        this.board_id = board_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Board functionality
    public void addList(BoardList list) {
        this.lists.add(list);
    }

    public void removeList(BoardList list) {
        this.lists.remove(list);
    }

    // other helper methods
    public List<BoardList> getLists() {
        return lists;
    }

    // toString() method for debugging purposes
    @Override
    public String toString() {
        return "Board{" +
                "id=" + board_id +
                ", name='" + name + '\'' +
                ", lists=" + lists +
                '}';
    }
}
