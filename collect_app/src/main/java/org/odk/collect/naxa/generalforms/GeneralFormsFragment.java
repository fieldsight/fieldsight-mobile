package org.odk.collect.naxa.generalforms;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.odk.collect.android.R;
import org.odk.collect.android.utilities.SnackbarUtils;
import org.odk.collect.naxa.generalforms.db.GeneralFormViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class GeneralFormsFragment extends Fragment {

    private GeneralFormViewModel viewmodel;
    private GeneralFormsFragBinding binding;
    private DisplayGeneralFormsAdapter generalFormsAdapter;


    @BindView(R.id.no_message)
    TextView emptyMessage;
    @BindView(R.id.android_list)
    RecyclerView recyclerView;
    Unbinder unbinder;

    public static GeneralFormsFragment newInstance() {
        return new GeneralFormsFragment();
    }

    public GeneralFormsFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        viewmodel.start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.general_forms_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        viewmodel =


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
        generalFormsAdapter = new DisplayGeneralFormsAdapter(new ArrayList<>(0), getActivity(), getActivity());
        recyclerView.setAdapter(generalFormsAdapter);

    }


}
