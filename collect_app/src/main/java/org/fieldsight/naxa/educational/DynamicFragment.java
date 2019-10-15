package org.fieldsight.naxa.educational;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.fieldsight.collect.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by susan on 7/7/2017.
 */

public class DynamicFragment extends Fragment {


    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    View noDataLayout;

    public List<Object> listArryObjFrom = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dynamic_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.NewsList);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        noDataLayout = rootView.findViewById(R.id.root_layout_empty_layout);

        if (listArryObjFrom.isEmpty()) {

            shownodatalayout();
        } else {
            showlistlayout();
            bindDataToAdapter();

        }

        return rootView;
    }

    private void showlistlayout() {
        noDataLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void shownodatalayout() {
        noDataLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }


    private void bindDataToAdapter() {
        recyclerView.setAdapter(new EducationalMaterialRecyclerViewAdapter(getContext(), listArryObjFrom));
    }



    public void prepareAllFields(List<Object> listArryObjFrom) {
        this.listArryObjFrom = listArryObjFrom;
    }

}
