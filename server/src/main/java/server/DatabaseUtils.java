package server;

import org.springframework.stereotype.Service;

import commons.Board;
import commons.BoardList;
import commons.Card;


@Service
public class DatabaseUtils {
    public Board mockSimpleBoard() {
        Board board = new Board("test board");
        BoardList l1 = new BoardList("test list 1");
        l1.addCard(new Card("aa"));
        l1.addCard(new Card("bb"));
        l1.addCard(new Card("ca"));

        board.addList(l1);

        BoardList l2 = new BoardList("test list 2");
        l2.addCard(new Card("az"));
        l2.addCard(new Card("bz"));
        l2.addCard(new Card("cz"));

        board.addList(l2);
        return board;
    }

    public void updateCard(Card card, Card newCard) {
        System.out.println(newCard.getTitle());
        if (!newCard.getTitle().trim().isEmpty())
            card.setTitle(newCard.getTitle());

        card.setDescription(newCard.getDescription());
        card.setSubtasks(newCard.getSubtasks());
        card.setCompletedTasks(newCard.getCompletedTasks());
        card.setCompletedSubs(newCard.getCompletedSubs());
        card.getTags().clear();
        card.setTagColors(newCard.getTags());
    }
}
