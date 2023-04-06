package commons;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 */
@Entity
public class Card implements Serializable {

    //instance variables
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "BOARDLIST_ID")
    public BoardList boardList;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    public Board board;

    public String title;
    public String description;
    @ElementCollection
    public List<String> subtasks;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Tag> tags;
    private int completedSubs;

    @ElementCollection
    private List<String> completedTasks;


    //constructor
    public Card(String title, String description){
        this.title = title;
        this.description = description;
        subtasks = new ArrayList<>();
        tags = new ArrayList<>();
        completedSubs = 0;
        completedTasks=new ArrayList<>();
    }

    public Card(String title){
        this.title = title;
        subtasks = new ArrayList<>();
        tags = new ArrayList<>();
        completedSubs = 0;
        completedTasks = new ArrayList<>();
    }

    public Card() {
        subtasks = new ArrayList<>();
        tags = new ArrayList<>();
        completedSubs = 0;
        completedTasks = new ArrayList<>();
    }
    //getters and setters

    public long getId() {
        return id;
    }

    /*
    public long getListId() {
        return list_id;
    }
    */

    public void setId(long id){
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSubtasks() {
        return subtasks;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public int getCompletedSubs() {
        return completedSubs;
    }

    /*
    public void setListId(long list_id) {
        this.list_id = list_id;
    }
    */
    
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    //adding subtask
    public void addSubTask(String task){
        subtasks.add(task);
    }

    //remove subtask
    public void removeSubTask(String task){
        subtasks.remove(task);
        if(completedTasks.contains(task)){
            uncompleteSubTask(task);
        }
    }
    //complete a subtask
    public void completeSubTask(String text)  {
        if(!completedTasks.contains(text)){
            completedTasks.add(text);
            if(completedSubs!=subtasks.size())
                completedSubs++;
        }
    }

    public void uncompleteSubTask(String text){

        if(completedTasks.contains(text)){
            completedTasks.remove(text);
            if(completedSubs!=0)
                completedSubs--;
        }

    }
    //add a tag to card
    public void addTag(Tag tag){
        tags.add(tag);
        tag.card=this;
    }

    //remove a tag from card
    public void removeTag(Tag tag){
        tags.remove(tag);
    }
     //equals method
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
    //hashcode method
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
    //toString method
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    public BoardList getBoardList() {
        return boardList;
    }

    public void setBoardList(BoardList boardList) {
        this.boardList = boardList;
    }

    public List<String> getCompletedTasks() {
        return completedTasks;
    }

    public void addSubtaskAtIndex(String s, int index){
        subtasks.add(index, s);
    }

    public String getSubtaskAtIndex(int index){
        return subtasks.get(index);
    }

    public void setCompletedSubs(int number){
        completedSubs = number;
    }

    public void setSubtasks(List<String> subtasks){
        this.subtasks = subtasks;
    }

    public void setCompletedTasks(List<String> complete){
        this.completedTasks = complete;
    }
}
