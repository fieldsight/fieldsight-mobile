package org.odk.collect.naxa.stages;


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
import android.widget.LinearLayout;

import org.odk.collect.android.R;
import org.odk.collect.naxa.generalforms.DisplayGeneralFormsAdapter;
import org.odk.collect.naxa.generalforms.ViewModelFactory;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.scheduled.data.ScheduledFormViewModel;
import org.odk.collect.naxa.stages.data.Stage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;

public class StageListFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.android_list)
    RecyclerView recyclerView;

    @BindView(R.id.root_layout_general_form_frag)
    LinearLayout rootLayout;


    private StageListAdapter listAdapter;
    private Site loadedSite;
    Unbinder unbinder;

    private StageViewModel viewModel;

    public static StageListFragment newInstance(@NonNull Site loadedSite) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        StageListFragment scheduleListFragment = new StageListFragment();
        scheduleListFragment.setArguments(bundle);
        return scheduleListFragment;

    }

    public StageListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.general_forms_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());

        viewModel = ViewModelProviders.of(getActivity(), factory).get(StageViewModel.class);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();
        viewModel.loadStages(true)
                .observe(this, new Observer<List<Stage>>() {
                    @Override
                    public void onChanged(@Nullable List<Stage> stages) {
                        Timber.i("Stage forms data has been changed");
                        listAdapter.updateList(stages);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupListAdapter() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        listAdapter = new StageListAdapter(new ArrayList<>(0));
        recyclerView.setAdapter(listAdapter);
    }


}
