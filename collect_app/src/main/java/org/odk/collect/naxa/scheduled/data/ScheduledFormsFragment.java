package org.odk.collect.naxa.scheduled.data;

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

import org.odk.collect.android.R;
import org.odk.collect.android.application.Collect;
import org.odk.collect.naxa.common.FieldSightFormListFragment;
import org.odk.collect.naxa.common.OnFormItemClickListener;
import org.odk.collect.naxa.common.SharedPreferenceUtils;
import org.odk.collect.naxa.generalforms.ViewModelFactory;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.scheduled.ScheduledFormsAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.odk.collect.naxa.common.Constant.FormDeploymentFrom.PROJECT;

public class ScheduledFormsFragment extends FieldSightFormListFragment implements OnFormItemClickListener<ScheduleForm> {

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private Site loadedSite;
    private Unbinder unbinder;
    private ScheduledFormsAdapter scheduledFormsAdapter;
    private ScheduledFormViewModel viewModel;

    public static ScheduledFormsFragment newInstance(@NonNull Site loadedSite) {

        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        ScheduledFormsFragment scheduleFormListFragment = new ScheduledFormsFragment();
        scheduleFormListFragment.setArguments(bundle);
        return scheduleFormListFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.scheduled_forms_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        ViewModelFactory factory = ViewModelFactory.getInstance(getActivity().getApplication());

        viewModel =
                ViewModelProviders.of(getActivity(), factory).get(ScheduledFormViewModel.class);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();
        viewModel.getAll(true).observe(this, new Observer<List<ScheduleForm>>() {
            @Override
            public void onChanged(@Nullable List<ScheduleForm> scheduleForms) {
                scheduledFormsAdapter.updateList(scheduleForms);
            }
        });
    }


    private void setupListAdapter() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        scheduledFormsAdapter = new ScheduledFormsAdapter(new ArrayList<>(0), this);
        recyclerView.setAdapter(scheduledFormsAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
    }

    @Override
    public void onGuideBookButtonClicked(ScheduleForm scheduleForm, int position) {

    }

    @Override
    public void onFormItemClicked(ScheduleForm scheduleForm) {
        String submissionUrl = generateSubmissionUrl(PROJECT, loadedSite.getProject(), scheduleForm.getFsFormId());
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, submissionUrl);

        fillODKForm(scheduleForm.getIdString());
    }

    @Override
    public void onFormItemLongClicked(ScheduleForm scheduleForm) {

    }

    @Override
    public void onFormHistoryButtonClicked(ScheduleForm scheduleForm) {

    }
}
