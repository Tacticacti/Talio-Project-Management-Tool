package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void constructorTest(){
        Card c1 = new Card("Slides");
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
        Tag tag= new Tag("animals");
        c1.addTag(tag);
        List<Tag> tags = new ArrayList<>();
        tags.add(tag);
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
        c1.completeSubTask("research monkeys");
        assertEquals(2, c1.getCompletedSubs());
        c1.completeSubTask("research monkeys");
        assertEquals(2, c1.getCompletedSubs());
        c1.completeSubTask("research donkeys");
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
        assertEquals(12345678, c1.getId());
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
        Tag t = new Tag("");
        c1.addTag(t);
        List<Tag> tags = new ArrayList<>();
        tags.add(t);
        assertEquals(tags, c1.getTags());

    }

    @Test
    void removeTag() {
        Card c1 = new Card("Slides", "prep slide 3-5");
        Tag tag= new Tag("animals");
        c1.addTag(tag);
        List<Tag> tags = new ArrayList<>();
        tags.add(tag);
        assertEquals(tags, c1.getTags());
        c1.removeTag(tag);
        tags.remove(tag);
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
    void setBoardList(){
        Card card = new Card("Card title", "Card description");
        BoardList boardList = new BoardList("Board");
        card.setBoardList(boardList);
        assertEquals(boardList, card.getBoardList());
    }

    @Test
    void SubtaskAtIndex(){
        Card card = new Card("Card title", "Card description");
        card.getSubtasks().add("Subtask 1");
        card.getSubtasks().add("Subtask 3");
        card.addSubtaskAtIndex("Subtask 2", 1);
        assertEquals("Subtask 2", card.getSubtaskAtIndex(1));
    }

    @Test
    void setSubtasks(){
        Card card = new Card("Card title", "Card description");
        List<String> subtasks= new ArrayList<>();
        subtasks.add("Subtask 1");
        subtasks.add("Subtask 2");

        card.setSubtasks(subtasks);
        assertEquals(subtasks, card.getSubtasks());
    }

    @Test
    void completedTasksTest(){
        Card c1 = new Card("Slides", "prep slide 3-5");
        List<String> completed= new ArrayList<>();
        String sub1 = "Subtask 1";
        String sub2 = "Subtask 2";
        completed.add(sub1);
        completed.add(sub2);
        c1.setCompletedTasks(completed);
        c1.setSubtasks(completed);

        assertEquals(completed, c1.getCompletedTasks());
        c1.removeSubTask("Subtask 1");
        assertFalse(c1.getSubtasks().contains(sub1));
        assertTrue(c1.getSubtasks().contains(sub2));

        assertDoesNotThrow(() -> c1.uncompleteSubTask("randomName"));
    }

    @Test
    void CompletedSubsTest(){
        Card card = new Card("Card title", "Card description");
        card.getSubtasks().add("Subtask 1");
        card.getSubtasks().add("Subtask 3");

        card.setCompletedSubs(2);
        assertEquals(2, card.getCompletedSubs());
    }

    @Test
    public void testRemoveSub() {
        Card card = new Card("Card title", "Card description");
        String sub1 = "Subtask 1";
        String sub2 = "Subtask 2";
        String sub3 = "Subtask 3";
        card.addSubTask(sub1);
        card.addSubTask(sub2);

        assertTrue(card.getSubtasks().contains(sub1));
        assertTrue(card.getSubtasks().contains(sub2));

        card.completeSubTask(sub3);
        assertTrue(!card.getSubtasks().contains(sub3));
        assertTrue(card.getCompletedTasks().contains(sub3));

        card.removeSubTask(sub1);
        card.uncompleteSubTask(sub3);
        //card.removeSubTask(sub3);
        assertFalse(card.getSubtasks().contains(sub1));
        assertTrue(card.getSubtasks().contains(sub2));
        assertFalse(card.getSubtasks().contains(sub3));
        assertFalse(card.getCompletedTasks().contains(sub3));
        assertDoesNotThrow(() -> card.removeSubTask("random task"));
    }

    @Test
    void testToString(){
        Card card = new Card("Card title", "Card description");
        Tag tag=new Tag("tag");
        card.getSubtasks().add("Subtask 1");
        card.getSubtasks().add("Subtask 2");
        card.getTags().add(tag);
//        card.getTags().add("Tag 2");
//        card.getTags().add("Tag 3");

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
