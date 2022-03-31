package it.cnr.iit.anonymousvoice;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import it.cnr.iit.anonymousvoice.entityextraction.filter.FilterEntity;

/**
 * Filter fragment.
 * Contains logic to select entities on which perform
 * entity recognition
 */
public class FilterFragment extends Fragment {

    private static final String TAG = "FilterFragment";

    public static final String FILTER_LIST_PARAM = "filters";
    public static final String SELECTED_PARAM = "selected";

    private ArrayList<FilterEntity> filters;
    private ArrayList<FilterEntity> selected;

    public FilterFragment() {
        // Required empty public constructor
    }

    public static FilterFragment newInstance(List<FilterEntity> filters, List<FilterEntity> selected) {
        FilterFragment fragment = new FilterFragment();
        Bundle args = new Bundle();
        args.putSerializable(FILTER_LIST_PARAM, (ArrayList<FilterEntity>)filters);
        args.putSerializable(SELECTED_PARAM, (ArrayList<FilterEntity>)selected);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filters = (ArrayList<FilterEntity>) getArguments().getSerializable(FILTER_LIST_PARAM);
            selected = (ArrayList<FilterEntity>) getArguments().getSerializable(SELECTED_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializesFilters();
    }

    /**
     * Creates filters section
     */
    private void initializesFilters() {
        LinearLayout ln = getView().findViewById(R.id.entityVerticalLayout);
        filters.forEach(filter -> {
            ln.addView(createOption(filter));
        });
    }

    /**
     * Creates a switch to enable/disable an entity
     *
     * @param filterEntity
     * @return
     */
    private Switch createOption(FilterEntity filterEntity) {
        Switch option = new Switch(getContext());
        option.setText(filterEntity.getDescription());
        option.setChecked(selected.stream().anyMatch(
                selectedEntity -> selectedEntity.getId() == filterEntity.getId()));
        option.setOnCheckedChangeListener(onCheckedChangeListener(filterEntity));
        return option;
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener(FilterEntity selectedEntity) {
        return (buttonView, isChecked) -> {
            selected.removeIf(filterEntity -> filterEntity.getId() == selectedEntity.getId());
            if(isChecked){
                Log.i(TAG, "onCheckedChangeListener: Selected " + buttonView.getText() + " [" + selectedEntity + "]");
                selected.add(selectedEntity);
            }else{
                Log.i(TAG, "onCheckedChangeListener: Deselected " + buttonView.getText() + " [" + selectedEntity + "]");
            }
        };
    }
}