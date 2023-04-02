package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    public Board board;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "CARD_ID")
    public Card card;


    public String title;

    public Tag(String title) {
        this.title = title;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(title, tag.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
