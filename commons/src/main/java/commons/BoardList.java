package commons;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings("checkstyle:Indentation")
@Entity
public class BoardList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    public Board board;

    private String name;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    // @JoinColumn(name = "list_id")
    private List<Card> cards;

    // added to fix repo.findAll()
    public BoardList() {
        this.name = "";
        this.cards = new ArrayList<>();
    }

    public BoardList(String name) {
        this.name = name;
        this.cards = new ArrayList<>();
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(long list_id) {
        this.id = list_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Board functionality
    public void addCard(Card card) {
        card.board = this.board;
        card.boardList = this;
        this.cards.add(card);
    }

    public void removeCard(Card card) {
        this.cards.remove(card);
        //card.boardList = null;
    }

    // other helper methods
    public List<Card> getCards() {
        return cards;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BoardList) {
            BoardList bl = (BoardList) obj;

            if (bl.cards.equals(this.cards) && bl.name.equals(this.name)) {
                return true;
            }
            return false;
        }
        return false;
    }
}
