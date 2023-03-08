package commons;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Columnn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    public Columnn(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "Columnn{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
