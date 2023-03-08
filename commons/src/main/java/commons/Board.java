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
    private Long id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private List<Columnn> columns;

    // added to fix repo.findAll()
    public Board() {}

    // constructor
    public Board(String name) {
        this.name = name;
        this.columns = new ArrayList<>();
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Board functionality
    public void addColumn(Columnn column) {
        this.columns.add(column);
    }

    public void removeColumn(Columnn column) {
        this.columns.remove(column);
    }

    // other helper methods
    public List<Columnn> getColumns() {
        return columns;
    }

    // toString() method for debugging purposes
    @Override
    public String toString() {
        return "Board{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", columns=" + columns +
                '}';
    }
}
