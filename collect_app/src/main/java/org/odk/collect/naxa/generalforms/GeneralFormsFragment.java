package org.odk.collect.naxa.generalforms;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.odk.collect.android.R;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.generalforms.db.GeneralFormViewModel;
import org.odk.collect.naxa.site.FragmentHostActivity;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GeneralFormsFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    SyncCommentLifecycleObserver syncCommentLifecycleObserver;

    private org.odk.collect.naxa.generalforms.db.GeneralFormViewModel viewModel;

    @BindView(R.id.android_list)
    RecyclerView recyclerView;
    Unbinder unbinder;
    private DisplayGeneralFormsAdapter generalFormsAdapter;

    public static GeneralFormsFragment newInstance() {
        return new GeneralFormsFragment();
    }

    public GeneralFormsFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.general_forms_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        viewModel = FragmentHostActivity.obtainViewModel(getActivity());

        viewModel.loadGeneralForms(true)
                .observe(this, generalForms -> {
                    ToastUtils.showShortToastInMiddle(String.format("%s forms", generalForms != null ? generalForms.size() : 0));
                    generalFormsAdapter.updateList(generalForms);
                });
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();
    }

    private void setupListAdapter() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        generalFormsAdapter = new DisplayGeneralFormsAdapter(new ArrayList<>(0));
        recyclerView.setAdapter(generalFormsAdapter);

    }


}
