package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CardTest {


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
        c1.completeSubTask();
        assertEquals(1, c1.getCompletedSubs());
        c1.completeSubTask();
        assertEquals(2, c1.getCompletedSubs());
        c1.completeSubTask();
        assertEquals(3, c1.getCompletedSubs());
        c1.completeSubTask();
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
    void setListId() {
        Card c1 = new Card("Slides", "prep slide3-5");
        c1.setListId(4563);
        assertEquals(4563, c1.getListId());
    }


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
        c1.completeSubTask();
        assertEquals(1, c1.getCompletedSubs());
        c1.completeSubTask();
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
}