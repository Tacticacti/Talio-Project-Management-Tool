package commons;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ElementCollection;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
@Entity
public class Card implements Serializable {

    //instance variables
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long card_id;

    public long list_id;


    public String title;
    public String description;
    @ElementCollection
    public List<String> subtasks;
    @ElementCollection
    public List<String> tags;
    private int completedSubs;


    //constructor
    public Card(String title, String description){
        this.title = title;
        this.description = description;
        subtasks = new ArrayList<>();
        tags = new ArrayList<>();
        completedSubs = 0;
    }

    public Card() {

    }
    //getters and setters

    public long getId() {
        return card_id;
    }

    public long getListId() {
        return list_id;
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

    public List<String> getTags() {
        return tags;
    }

    public int getCompletedSubs() {
        return completedSubs;
    }

    public void setListId(long list_id) {
        this.list_id = list_id;
    }
    
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
    }
    //complete a subtask
    public void completeSubTask()  {

        if(completedSubs==subtasks.size()){
            
        }else
            completedSubs++;
    }
    //add a tag to card
    public void addTag(String tag){
        tags.add(tag);
    }

    //remove a tag from card
    public void removeTag(String tag){
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

}
