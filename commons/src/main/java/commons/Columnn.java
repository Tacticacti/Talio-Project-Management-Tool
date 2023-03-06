package commons;

import javax.persistence.*;

@Entity
public class Columnn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
