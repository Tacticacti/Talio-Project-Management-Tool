package commons;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.*;


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
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "tagsDistribution",
            joinColumns = {@JoinColumn(name = "tag_id")},
            inverseJoinColumns = {@JoinColumn(name = "card_id")}
    )
    public Set<Card> cards;


    public String title;

    public String color;

    public Tag(String title, String color) {
        this.color = color;
        this.title = title;
        cards = new HashSet<>();
    }

    public Tag(String title) {
        this.color = "#ffffff";
        this.title = title;
        cards = new HashSet<>();
    }
    public Tag(){
        this.title = "";
        this.color = "#ffffff";
        cards = new HashSet<>();
    }

    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void addCard(Card card){
        cards.add(card);
    }

    public void removeCard(Card card){
        cards.remove(card);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(title, tag.title);
    }

    public void setColor(String color){

        this.color = color;
    }
    public String getColor(){
        return color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title);
    }
}
