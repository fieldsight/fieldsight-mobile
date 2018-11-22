package org.bcss.collect.naxa.scheduled.data;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.bcss.collect.naxa.educational.EducationalMaterialActivity;
import org.bcss.collect.naxa.previoussubmission.model.ScheduledFormAndSubmission;
import org.bcss.collect.naxa.submissions.PreviousSubmissionListActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;
import org.bcss.collect.naxa.common.FieldSightFormListFragment;
import org.bcss.collect.naxa.common.OnFormItemClickListener;
import org.bcss.collect.naxa.common.RecyclerViewEmptySupport;
import org.bcss.collect.naxa.common.SharedPreferenceUtils;
import org.bcss.collect.naxa.common.event.DataSyncEvent;
import org.bcss.collect.naxa.common.utilities.FlashBarUtils;
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.scheduled.ScheduledFormsAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static org.bcss.collect.naxa.common.AnimationUtils.runLayoutAnimation;
import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;
import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.PROJECT;
import static org.bcss.collect.naxa.generalforms.data.FormType.TABLE_GENERAL_FORM;

public class ScheduledFormsFragment extends FieldSightFormListFragment implements OnFormItemClickListener<ScheduleForm> {

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.recycler_view)
    RecyclerViewEmptySupport recyclerView;

    @BindView(R.id.root_layout_empty_layout)
    View emptyLayout;

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

        setToolbarText();
        return rootView;
    }

    private void setToolbarText() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_scheduled_forms);
        toolbar.setSubtitle(loadedSite.getName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();

        viewModel.getForms(loadedSite)
                .observe(this, new Observer<List<ScheduledFormAndSubmission>>() {
                    @Override
                    public void onChanged(@Nullable List<ScheduledFormAndSubmission> scheduledFormAndSubmissions) {
                        if (scheduledFormsAdapter.getItemCount() == 0) {
                            runLayoutAnimation(recyclerView);
                        }
                        scheduledFormsAdapter.updateList(scheduledFormAndSubmissions);
                    }
                });

    }


    private void setupListAdapter() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        scheduledFormsAdapter = new ScheduledFormsAdapter(new ArrayList<>(0), this);
        recyclerView.setEmptyView(emptyLayout,
                getString(R.string.empty_message, "scheduled forms"),
                new RecyclerViewEmptySupport.OnEmptyLayoutClickListener() {
                    @Override
                    public void onRetryButtonClick() {
                        viewModel.getForms(true, loadedSite);
                    }
                });
        recyclerView.setAdapter(scheduledFormsAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
    }

    @Override
    public void onGuideBookButtonClicked(ScheduleForm scheduleForm, int position) {
        EducationalMaterialActivity.startFromScheduled(getActivity(),scheduledFormsAdapter.getAll(),position);
    }

    @Override
    public void onFormItemClicked(ScheduleForm scheduleForm, int position) {

        String submissionUrl = generateSubmissionUrl(loadedSite.getGeneralFormDeployedFrom(), loadedSite.getId(), scheduleForm.getFsFormId());
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, submissionUrl);
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_SITE_ID, loadedSite.getId());

        fillODKForm(scheduleForm.getIdString());
    }

    @Override
    public void onFormItemLongClicked(ScheduleForm scheduleForm) {

    }

    @Override
    public void onFormHistoryButtonClicked(ScheduleForm scheduleForm) {
        PreviousSubmissionListActivity.start(getActivity(),
                scheduleForm.getFsFormId(),
                scheduleForm.getScheduleName(),
                scheduleForm.getFormName(),
                null,
                loadedSite.getId(),
                null,
                TABLE_GENERAL_FORM
        );
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DataSyncEvent event) {

        if (!isAdded() || getActivity() == null) {
            //Fragment is not added
            return;
        }


        Timber.i(event.toString());
        switch (event.getEvent()) {
            case DataSyncEvent.EventStatus.EVENT_START:
                FlashBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_start_message), true);
                break;
            case DataSyncEvent.EventStatus.EVENT_END:
                FlashBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_end_message), false);
                break;
            case DataSyncEvent.EventStatus.EVENT_ERROR:
                FlashBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_error_message), false);
                break;
        }
    }

}
