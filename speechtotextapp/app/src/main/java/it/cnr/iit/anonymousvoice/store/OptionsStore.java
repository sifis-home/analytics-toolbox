package it.cnr.iit.anonymousvoice.store;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.anonymousvoice.entityextraction.filter.FilterEntity;

/**
 * Stores options and filters
 */
public class OptionsStore {

    /**
     * Stores the selected entities to recognize
     */
    private List<FilterEntity> selectedEntity = new ArrayList<>();

    public void setSelectedEntity(List<FilterEntity> selectedEntity) {
        this.selectedEntity = selectedEntity;
    }

    public List<FilterEntity> getSelectedEntity() {
        return selectedEntity;
    }
}
