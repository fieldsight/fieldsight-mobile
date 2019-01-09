package org.bcss.collect.naxa.common;/*
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
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.odk.collect.android.utilities.ThemeUtils;

import java.util.ArrayList;


public class FilterDialogAdapterv2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_TEXT = 0, VIEW_TYPE_BUTTON = 2;
    private final FilterDialogAdapterv2.RecyclerViewClickListener listener;

    private final RecyclerView recyclerView;
    private final ThemeUtils themeUtils;
    private final ArrayList<Triplet> sortList;


    public FilterDialogAdapterv2(Context context, RecyclerView recyclerView, ArrayList<Triplet> sortList, RecyclerViewClickListener recyclerViewClickListener) {
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
        Triplet t = sortList.get(holder.getAdapterPosition());
        switch (viewType) {
            case VIEW_TYPE_BUTTON:
                break;
            case VIEW_TYPE_TEXT:
            default:
                ViewHolderText viewHolder = (ViewHolderText) holder;
                viewHolder.txtViewTitle.setText(String.valueOf(t.getSecond()));
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

    public ArrayList<Triplet> getAll() {
        return sortList;
    }

    @Override
    public int getItemViewType(int position) {


        return VIEW_TYPE_TEXT;
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
        }
    }

    public interface RecyclerViewClickListener {
        void onItemClicked(ViewHolderText holder, int position, Triplet filterOption);
    }
}
