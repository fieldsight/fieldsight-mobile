package org.fieldsight.naxa.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.bcss.collect.android.R;
import org.fieldsight.naxa.common.ViewModelFactory;
import org.fieldsight.naxa.sync.ContentDownloadActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class ProjectContactsFragment extends Fragment implements ContactAdapter.ContactDetailListener {

    private ContactAdapter contactAdapter;


    @BindView(R.id.root_layout_empty_layout)
    LinearLayout emptyLayout;
    private Unbinder unbinder;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    public static ProjectContactsFragment newInstance() {
        return new ProjectContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupRecycleView();

        ContactRemoteSource.getInstance().getAll();
        ViewModelFactory factory = ViewModelFactory.getInstance();
        ProjectContactViewModel viewModel = ViewModelProviders.of(requireActivity(), factory).get(ProjectContactViewModel.class);
        viewModel.getContacts()
                .observe(this, fieldSightContactModels -> {
                    if (fieldSightContactModels != null) {
                        contactAdapter = new ContactAdapter(fieldSightContactModels, ProjectContactsFragment.this, getActivity());
                        recyclerView.swapAdapter(contactAdapter, true);
                        if(contactAdapter.getItemCount() > 0){
                            emptyLayout.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }else {
                            emptyLayout.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    }
                });

        return view;
    }

    private void setupRecycleView() {
        contactAdapter = new ContactAdapter(new ArrayList<>(0), this, getActivity());

        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        emptyLayout.findViewById(R.id.btn_retry)
                .setOnClickListener(v -> ContentDownloadActivity.start(getActivity()));
        recyclerView.setAdapter(contactAdapter);
    }

    @Override
    public void onContactClicked(FieldSightContactModel contactModel) {

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
