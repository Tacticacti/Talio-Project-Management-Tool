import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Board {
    
    // instance variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private List<Column> columns;

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
    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public void removeColumn(Column column) {
        this.columns.remove(column);
    }

    // other helper methods
    public List<Column> getColumns() {
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
