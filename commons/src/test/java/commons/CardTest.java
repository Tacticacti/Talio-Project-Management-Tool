package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void emptyConstructorTest(){
        Card c1 = new Card();
        c1.setTitle("Slides");
        assertEquals("Slides", c1.getTitle());
        assertTrue(c1.getSubtasks().isEmpty());
        assertTrue(c1.getTags().isEmpty());
        assertEquals(0, c1.getCompletedSubs());
        assertTrue(c1.getCompletedTasks().isEmpty());
    }
    @Test
    void getTitle() {
        Card c1 = new Card("Slides", "prep slide3-5");
        assertEquals("Slides", c1.getTitle());
    }

    @Test
    void getDescription() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        assertEquals("prep slide 3-5", c1.getDescription());
    }

    @Test
    void getSubtasks() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        c1.addSubTask("research otters");
        List<String> tasks = new ArrayList<>();
        tasks.add("research otters");
        assertEquals(tasks, c1.getSubtasks());
    }

    @Test
    void getTags() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        c1.addTag("animals");
        List<String> tags = new ArrayList<>();
        tags.add("animals");
        assertEquals(tags, c1.getTags());

    }

    @Test
    void getCompletedSubs() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        c1.addSubTask("research otters");
        c1.addSubTask("research monkeys");
        c1.addSubTask("research donkeys");
        assertEquals(0, c1.getCompletedSubs());
        c1.completeSubTask("research otters");
        assertEquals(1, c1.getCompletedSubs());
        c1.completeSubTask("research otters");
        assertEquals(2, c1.getCompletedSubs());
        c1.completeSubTask("research otters");
        assertEquals(3, c1.getCompletedSubs());
        c1.completeSubTask("research otters");
        assertEquals(3, c1.getCompletedSubs());
    }



    @Test
    void setTitle() {
        Card c1 = new Card("Slides", "prep slide3-5");
        c1.setTitle("Schedule");
        assertEquals("Schedule", c1.getTitle());
    }

    @Test
    void setDescription() {
        Card c1 = new Card("Slides", "prep slide3-5");
        c1.setDescription("prep slide 5-6");
        assertEquals("prep slide 5-6", c1.getDescription());
    }

    @Test
    void setCardId()
    {
        Card c1 = new Card("Slides", "prep slide3-5");
        c1.setId(12345678);
        assertEquals(12345678,c1.getId());
    }

    /*
    @Test
    void setListId() {
        Card c1 = new Card("Slides", "prep slide3-5");
        c1.setListId(4563);
        assertEquals(4563, c1.getListId());
    }
    */

    @Test
    void addSubTask() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        c1.addSubTask("research otters");
        List<String> tasks = new ArrayList<>();
        tasks.add("research otters");
        assertEquals(tasks, c1.getSubtasks());
        assertEquals(1, c1.getSubtasks().size());

    }

    @Test
    void removeSubTask() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        c1.addSubTask("research otters");
        c1.addSubTask("research monkeys");
        List<String> tasks = new ArrayList<>();
        tasks.add("research otters");
        tasks.add("research monkeys");
        assertEquals(tasks, c1.getSubtasks());
        assertEquals(2, c1.getSubtasks().size());
        c1.removeSubTask("research otters");
        tasks.remove("research otters");
        assertEquals(tasks, c1.getSubtasks());
        assertEquals(1, c1.getSubtasks().size());
    }

    @Test
    void completeSubTask() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        c1.addSubTask("research otters");
        c1.addSubTask("research monkeys");
        c1.addSubTask("research donkeys");
        assertEquals(0, c1.getCompletedSubs());
        c1.completeSubTask("research otters");
        List<String> completed = new ArrayList<>();
        completed.add("research otters");
        assertTrue(c1.getCompletedTasks().contains("research otters"));
        assertEquals(1, c1.getCompletedSubs());
        completed.add("research monkeys");
        c1.completeSubTask("research monkeys");
        assertEquals(completed, c1.getCompletedTasks());
    }


    @Test
    void uncompleteSubTask() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        c1.addSubTask("research otters");
        c1.addSubTask("research monkeys");
        c1.addSubTask("research donkeys");
        c1.completeSubTask("research otters");
        assertEquals(1, c1.getCompletedSubs());
        c1.uncompleteSubTask("research otters");
        assertEquals(0, c1.getCompletedSubs());
        c1.completeSubTask("research monkeys");
        c1.completeSubTask("research otters");
        List<String> completed = new ArrayList<>();
        completed.add("research monkeys");
        completed.add("research otters");
        assertEquals(completed, c1.getCompletedTasks());
        c1.uncompleteSubTask("research monkeys");
        completed.remove("research monkeys");
        assertEquals(completed, c1.getCompletedTasks());
    }

    @Test
    void addTag() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        c1.addTag("animals");
        List<String> tags = new ArrayList<>();
        tags.add("animals");
        assertEquals(tags, c1.getTags());

    }

    @Test
    void removeTag() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        c1.addTag("animals");
        List<String> tags = new ArrayList<>();
        tags.add("animals");
        assertEquals(tags, c1.getTags());
        c1.removeTag("animals");
        tags.remove("animals");
        assertEquals(tags, c1.getTags());
    }

    @Test
    void testEquals() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        Card c2 = new Card("Slides", "prep slide 3-5");
        assertEquals(c1, c2);
    }

    @Test
    void testHashCode() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        Card c2 = new Card("Slides", "prep slide 3-5");
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    void testToString(){
        Card card = new Card("Card title", "Card description");
        card.getSubtasks().add("Subtask 1");
        card.getSubtasks().add("Subtask 2");
        card.getTags().add("Tag 1");
        card.getTags().add("Tag 2");
        card.getTags().add("Tag 3");

        String result = card.toString();
        String expected = new ToStringBuilder(card, MULTI_LINE_STYLE)
                .append("board=<null>")
                .append("boardList", card.getBoardList())
                .append("completedSubs", card.getCompletedSubs())
                .append("completedTasks", card.getCompletedTasks())
                .append("description", card.getDescription())
                .append("id", card.getId())
                .append("subtasks", card.getSubtasks())
                .append("tags", card.getTags())
                .append("title", card.getTitle())
                .toString();

        assertEquals(expected, result);
    }

}
