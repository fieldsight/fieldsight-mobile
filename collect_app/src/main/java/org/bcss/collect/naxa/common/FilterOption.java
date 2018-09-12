package org.bcss.collect.naxa.common;

import android.util.Pair;

import java.util.List;

public class FilterOption {

    public enum FilterType {
        SELECTED_REGION,
        SITE,
        ALL_SITES,
        OFFLINE_SITES,
        CONFIRM_BUTTON,
    }

    private FilterType type;
    private String label;
    private List<Pair> options;
    private Pair selection;

    public FilterOption(FilterType type, String label,List<Pair> site) {
        this.type = type;
        this.label = label;
        this.options = site;
    }

    public FilterType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public void setSelection(Pair selection) {
        this.selection = selection;
    }

    public String getSelectionId() {
        return String.valueOf(selection.first);
    }


    public String getSelectionLabel() {
        return String.valueOf(selection.second);
    }

    public List<Pair> getOptions() {
        return options;
    }


}
