package org.fieldsight.naxa.common;/*
 * Copyright (C) 2017 Nishon
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.bcss.collect.android.R;;
import org.odk.collect.android.utilities.ThemeUtils;

import java.util.ArrayList;

public class FilterDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_TEXT = 0, VIEW_TYPE_SPINNER = 1, VIEW_TYPE_BUTTON = 2, VIEW_TYPE_LIST = 3;
    private final FilterDialogAdapter.RecyclerViewClickListener listener;
    private final RecyclerView recyclerView;
    private final ThemeUtils themeUtils;
    private final ArrayList<FilterOption> sortList;


    public FilterDialogAdapter(Context context, RecyclerView recyclerView, ArrayList<FilterOption> sortList, RecyclerViewClickListener recyclerViewClickListener) {
        themeUtils = new ThemeUtils(context);
        this.recyclerView = recyclerView;
        this.sortList = sortList;
        listener = recyclerViewClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLayoutView;
        switch (viewType) {
            case VIEW_TYPE_SPINNER:
                itemLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.filter_item_layout, parent, false);
                return new ViewHolderSpinner(itemLayoutView);

            case VIEW_TYPE_LIST:
                itemLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.filter_item_layout_list,parent,false);
                return new ViewHolderListView(itemLayoutView);
            case VIEW_TYPE_BUTTON:
                itemLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.filter_item_button, parent, false);
                return new ViewHolderButton(itemLayoutView);
            case VIEW_TYPE_TEXT:
            default:
                itemLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sort_item_layout, parent, false);
                return new ViewHolderText(itemLayoutView);

        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();

        switch (viewType) {
            case VIEW_TYPE_LIST:
                FilterOption filterOption1 = sortList.get(holder.getAdapterPosition());
                ViewHolderListView viewHolderListView = (ViewHolderListView) holder;
                PairSpinnerAdapter pairSpinnerAdapter = new PairSpinnerAdapter(((ViewHolderListView) holder).listView.getContext(), android.R.layout.simple_spinner_dropdown_item, filterOption1.getOptions());
                viewHolderListView.listView.setAdapter(pairSpinnerAdapter);


                break;
            case VIEW_TYPE_SPINNER:
                ViewHolderSpinner viewHolderSpinner = (ViewHolderSpinner) holder;
                viewHolderSpinner.imgViewIcon.setImageResource(R.drawable.ic_sort_by_alpha);
                viewHolderSpinner.txtViewTitle.setText(sortList.get(viewHolderSpinner.getAdapterPosition()).getLabel());

                FilterOption filterOption = sortList.get(viewHolderSpinner.getAdapterPosition());
                if (filterOption.getOptions() != null) {

                    PairSpinnerAdapter adapter = new PairSpinnerAdapter(((ViewHolderSpinner) holder).spinnerSiteCluster.getContext(), android.R.layout.simple_spinner_dropdown_item, filterOption.getOptions());
                    viewHolderSpinner.spinnerSiteCluster.setAdapter(adapter);

                    ((ViewHolderSpinner) holder).spinnerSiteCluster.setSelection(filterOption.getOptions().size() - 1);
                    Pair intialIdLabelPair = (Pair) ((ViewHolderSpinner) holder).spinnerSiteCluster.getSelectedItem();
                    if (intialIdLabelPair != null) {
                        filterOption.setSelection(intialIdLabelPair);
                    }


                    viewHolderSpinner.spinnerSiteCluster.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Pair tappedIdLabelPair = filterOption.getOptions().get(position);
                            if (tappedIdLabelPair != null){
                                filterOption.setSelection(tappedIdLabelPair);
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }

                break;
            case VIEW_TYPE_BUTTON:
                break;
            case VIEW_TYPE_TEXT:
            default:
                ViewHolderText viewHolder = (ViewHolderText) holder;
                viewHolder.txtViewTitle.setText(sortList.get(viewHolder.getAdapterPosition()).getLabel());
                viewHolder.imgViewIcon.setImageResource(R.drawable.ic_sort_by_alpha);
                viewHolder.imgViewIcon.setImageDrawable(DrawableCompat.wrap(viewHolder.imgViewIcon.getDrawable()).mutate());
                //        int color = position == selectedSortingOrder ? themeUtils.getAccentColor() : themeUtils.getPrimaryTextColor();
                //        viewHolder.txtViewTitle.setTextColor(color);
                //        DrawableCompat.setTintList(viewHolder.imgViewIcon.getDrawable(), position == selectedSortingOrder ? ColorStateList.valueOf(color) : null);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return sortList.size();
    }

    public ArrayList<FilterOption> getAll() {
        return sortList;
    }

    @Override
    public int getItemViewType(int position) {

        switch (sortList.get(position).getType()) {
            case OFFLINE_SITES:
            case SELECTED_REGION:
            case SITE:
                return VIEW_TYPE_SPINNER;
            case CONFIRM_BUTTON:
                return VIEW_TYPE_BUTTON;
            case ALL_SITES:

            default:
                return VIEW_TYPE_TEXT;
        }

    }


    public class ViewHolderSpinner extends RecyclerView.ViewHolder {

        TextView txtViewTitle;
        ImageView imgViewIcon;
        Spinner spinnerSiteCluster;

        ViewHolderSpinner(final View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = itemLayoutView.findViewById(R.id.title);
            imgViewIcon = itemLayoutView.findViewById(R.id.icon);
            spinnerSiteCluster = itemLayoutView.findViewById(R.id.filter_item_layout_spinner);
        }

        public void updateItemColor(int selectedSortingOrder) {
            ViewHolderText previousHolder = (ViewHolderText) recyclerView.findViewHolderForAdapterPosition(selectedSortingOrder);
            previousHolder.txtViewTitle.setTextColor(themeUtils.getPrimaryTextColor());
            DrawableCompat.setTintList(previousHolder.imgViewIcon.getDrawable(), null);

            txtViewTitle.setTextColor(themeUtils.getAccentColor());
            DrawableCompat.setTint(imgViewIcon.getDrawable(), themeUtils.getAccentColor());
        }
    }

    public class ViewHolderListView extends RecyclerView.ViewHolder {

        TextView txtViewTitle;
        ListView listView;

        ViewHolderListView(final View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = itemLayoutView.findViewById(R.id.title);
            listView = itemLayoutView.findViewById(R.id.filter_item_layout_listview);
        }

        public void updateItemColor(int selectedSortingOrder) {
            ViewHolderText previousHolder = (ViewHolderText) recyclerView.findViewHolderForAdapterPosition(selectedSortingOrder);
            previousHolder.txtViewTitle.setTextColor(themeUtils.getPrimaryTextColor());
            DrawableCompat.setTintList(previousHolder.imgViewIcon.getDrawable(), null);

            txtViewTitle.setTextColor(themeUtils.getAccentColor());
        }
    }


    public class ViewHolderText extends RecyclerView.ViewHolder {

        TextView txtViewTitle;
        ImageView imgViewIcon;

        ViewHolderText(final View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = itemLayoutView.findViewById(R.id.title);
            imgViewIcon = itemLayoutView.findViewById(R.id.icon);

            itemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(ViewHolderText.this, getLayoutPosition(), sortList.get(getLayoutPosition()));
                }
            });
        }

        public void updateItemColor(int selectedSortingOrder) {
            ViewHolderText previousHolder = (ViewHolderText) recyclerView.findViewHolderForAdapterPosition(selectedSortingOrder);
            previousHolder.txtViewTitle.setTextColor(themeUtils.getPrimaryTextColor());
            DrawableCompat.setTintList(previousHolder.imgViewIcon.getDrawable(), null);

            txtViewTitle.setTextColor(themeUtils.getAccentColor());
            DrawableCompat.setTint(imgViewIcon.getDrawable(), themeUtils.getAccentColor());
        }
    }

    public class ViewHolderButton extends RecyclerView.ViewHolder implements View.OnClickListener {

        Button button;


        ViewHolderButton(final View itemLayoutView) {
            super(itemLayoutView);
            button = itemLayoutView.findViewById(R.id.filter_apply);
            button.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            listener.onFilterButtonClicked(sortList);
        }
    }

    public interface RecyclerViewClickListener {
        void onFilterButtonClicked(ArrayList<FilterOption> sortList);

        void onItemClicked(ViewHolderText holder, int position, FilterOption filterOption);
    }
}
