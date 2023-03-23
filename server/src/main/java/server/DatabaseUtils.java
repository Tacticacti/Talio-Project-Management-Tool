package server;

import org.springframework.stereotype.Service;

import commons.Board;
import commons.BoardList;
import commons.Card;

@Service
public class DatabaseUtils {

    public Board PopagateIDs(Board board) {
        for(BoardList bl : board.getLists()) {
            bl.boardId = board.getId();
            for(Card c : bl.getCards())
                c.listId = bl.getId();
        }

        return board;
    }
}
