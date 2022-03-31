package it.cnr.iit.anonymousvoice.entityextraction.filter;

import java.io.Serializable;

/**
 * This class contains data about entity to use to filter entity extraction
 */
public class FilterEntity implements Serializable {

    /**
     * Id of the entity
     */
    private int id;
    /**
     * Description of the entity
     */
    private String description;

    public FilterEntity() {
        //Empty constructor for serialization
    }

    public FilterEntity(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
