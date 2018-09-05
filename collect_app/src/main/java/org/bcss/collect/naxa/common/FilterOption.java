package org.bcss.collect.naxa.common;

import java.util.ArrayList;

public class FilterOption {

    public enum FilterType {
        SELECTED_REGION,
        OFFLINE_SITES,
        ALL_SITES
    }

    private FilterType type;
    private String label;
    private ArrayList<String> options;

    public FilterOption(FilterType type, String label, ArrayList<String> options) {
        this.type = type;
        this.label = label;
        this.options = options;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArrayList<String> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<String> options) {
        this.options = options;
    }


}
