package org.bcss.collect.naxa.contact;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.project.data.ProjectViewModel;
import org.bcss.collect.naxa.stages.StageViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProjectContactsFragment extends Fragment implements ContactAdapter.ContactDetailListener {
    private RecyclerView recyclerView;
    private ContactAdapter contactAdapter;

    private ProjectContactViewModel viewModel;

    public static ProjectContactsFragment getInstance() {
        return new ProjectContactsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_site_list, container, false);
        bindUI(view);
        setupRecycleView();


        ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());
        viewModel = ViewModelProviders.of(getActivity(), factory).get(ProjectContactViewModel.class);
        viewModel.getContacts()
                .observe(this, new Observer<List<FieldSightContactModel>>() {
                    @Override
                    public void onChanged(@Nullable List<FieldSightContactModel> fieldSightContactModels) {
                        if (fieldSightContactModels != null) {
                            contactAdapter = new ContactAdapter(fieldSightContactModels, ProjectContactsFragment.this, getActivity());
                            recyclerView.swapAdapter(contactAdapter, true);
                        }
                    }
                });

        return view;
    }

    private void bindUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view);
    }

    private void setupRecycleView() {
        contactAdapter = new ContactAdapter(new ArrayList<>(0), ProjectContactsFragment.this, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactAdapter);
    }

    @Override
    public void onContactClicked(FieldSightContactModel contactModel) {

    }

    @Override
    public void onPhoneButtonClick(FieldSightContactModel contactModel) {

    }
}
