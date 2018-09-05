package org.bcss.collect.naxa.common;/*
 * Copyright (C) 2017 Shobhit
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
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.utilities.ApplicationConstants;
import org.bcss.collect.android.utilities.ThemeUtils;

import java.util.ArrayList;

public class FilterDialogAdapter extends RecyclerView.Adapter<FilterDialogAdapter.ViewHolder> {
    private final FilterDialogAdapter.RecyclerViewClickListener listener;
    private final FilterOption.FilterType selectedSortingOrder;
    private final RecyclerView recyclerView;
    private final ThemeUtils themeUtils;
    private final ArrayList<FilterOption> sortList;

    public FilterDialogAdapter(Context context, RecyclerView recyclerView, ArrayList<FilterOption> sortList, FilterOption.FilterType selectedSortingOrder, RecyclerViewClickListener recyclerViewClickListener) {
        themeUtils = new ThemeUtils(context);
        this.recyclerView = recyclerView;
        this.sortList = sortList;
        this.selectedSortingOrder = selectedSortingOrder;
        listener = recyclerViewClickListener;
    }

    @NonNull
    @Override
    public FilterDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sort_item_layout, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.txtViewTitle.setText(sortList.get(viewHolder.getAdapterPosition()).getLabel());
        viewHolder.imgViewIcon.setImageResource(R.drawable.ic_sort_by_alpha);
        viewHolder.imgViewIcon.setImageDrawable(DrawableCompat.wrap(viewHolder.imgViewIcon.getDrawable()).mutate());

//        int color = position == selectedSortingOrder ? themeUtils.getAccentColor() : themeUtils.getPrimaryTextColor();
//        viewHolder.txtViewTitle.setTextColor(color);
//        DrawableCompat.setTintList(viewHolder.imgViewIcon.getDrawable(), position == selectedSortingOrder ? ColorStateList.valueOf(color) : null);
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return sortList.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtViewTitle;
        ImageView imgViewIcon;

        ViewHolder(final View itemLayoutView) {
            super(itemLayoutView);
            txtViewTitle = itemLayoutView.findViewById(R.id.title);
            imgViewIcon = itemLayoutView.findViewById(R.id.icon);

            itemLayoutView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClicked(ViewHolder.this, getLayoutPosition(), sortList.get(getLayoutPosition()));
                }
            });
        }

        public void updateItemColor(int selectedSortingOrder) {
            ViewHolder previousHolder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(selectedSortingOrder);
            previousHolder.txtViewTitle.setTextColor(themeUtils.getPrimaryTextColor());
            DrawableCompat.setTintList(previousHolder.imgViewIcon.getDrawable(), null);

            txtViewTitle.setTextColor(themeUtils.getAccentColor());
            DrawableCompat.setTint(imgViewIcon.getDrawable(), themeUtils.getAccentColor());
        }
    }

    public interface RecyclerViewClickListener {

        void onItemClicked(ViewHolder holder, int position, FilterOption filterOption);
    }
}
