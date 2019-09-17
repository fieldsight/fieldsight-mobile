package org.fieldsight.naxa.substages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.BaseFormListFragment;
import org.fieldsight.naxa.common.OnFormItemClickListener;
import org.fieldsight.naxa.common.RecyclerViewEmptySupport;
import org.fieldsight.naxa.common.SharedPreferenceUtils;
import org.fieldsight.naxa.common.ViewModelFactory;
import org.fieldsight.naxa.common.event.DataSyncEvent;
import org.fieldsight.naxa.common.utilities.SnackBarUtils;
import org.fieldsight.naxa.educational.EducationalMaterialActivity;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.stages.data.SubStage;
import org.fieldsight.naxa.submissions.PreviousSubmissionListActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.application.Collect;
import org.odk.collect.android.utilities.ToastUtils;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static org.fieldsight.naxa.common.Constant.EXTRA_FORM_DEPLOYED_FORM;
import static org.fieldsight.naxa.common.Constant.EXTRA_ID;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;
import static org.fieldsight.naxa.common.Constant.EXTRA_POSITION;
import static org.fieldsight.naxa.common.SharedPreferenceUtils.isFormSaveCacheSafe;
import static org.fieldsight.naxa.generalforms.data.FormType.TABLE_GENERAL_FORM;

public class SubStageListFragment extends BaseFormListFragment implements OnFormItemClickListener<SubStage> {

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.android_list)
    RecyclerViewEmptySupport recyclerView;

    @BindView(R.id.root_layout_general_form_frag)
    LinearLayout rootLayout;

    @BindView(R.id.root_layout_empty_layout)
    LinearLayout emptyLayout;

    private SubStageListAdapter listAdapter;
    private Site loadedSite;
    private String stageId;
    private String stagePosition;
    private String formDeployedFrom;

    private Unbinder unbinder;

    private SubStageViewModel viewModel;

    public static SubStageListFragment newInstance(@NonNull Site loadedSite, @NonNull String stageId, @NonNull String stagePosition, @NonNull String deployedFrom) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        bundle.putString(EXTRA_ID, stageId);
        bundle.putString(EXTRA_POSITION, stagePosition);
        bundle.putString(EXTRA_FORM_DEPLOYED_FORM, deployedFrom);
        SubStageListFragment subStageListFragment = new SubStageListFragment();
        subStageListFragment.setArguments(bundle);
        return subStageListFragment;

    }

    public SubStageListFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
        stageId = getArguments().getString(EXTRA_ID);
        stagePosition = getArguments().getString(EXTRA_POSITION);
        formDeployedFrom = getArguments().getString(EXTRA_FORM_DEPLOYED_FORM);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.general_forms_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        viewModelFactory = ViewModelFactory.getInstance(getActivity().getApplication());

        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(SubStageViewModel.class);
        setToolbarText();

        return rootView;
    }


    private void setToolbarText() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_substages);
        toolbar.setSubtitle(loadedSite.getName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();
        viewModel.loadSubStages(loadedSite.getId(), loadedSite.getProject(), stageId, loadedSite.getTypeId())
                .observe(this, substages -> {

                    listAdapter.updateList(substages);
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupListAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setEmptyView(emptyLayout, getString(R.string.empty_message, "staged forms"), () -> {
            viewModel.loadSubStages(loadedSite.getId(), loadedSite.getProject(), stageId, loadedSite.getTypeId());
        });
        listAdapter = new SubStageListAdapter(new ArrayList<>(0), stagePosition, this);
        recyclerView.setAdapter(listAdapter);


    }


    @Override
    public void onGuideBookButtonClicked(SubStage subStage, int position) {
        EducationalMaterialActivity.startFromSubstage(getActivity(), listAdapter.getAll(), position);
    }

    @Override
    public void onFormItemClicked(SubStage subStage, int position) {

        String formDeployedFrom = subStage.getSubStageDeployedFrom();

        String submissionUrl = generateSubmissionUrl(formDeployedFrom, loadedSite.getId(), subStage.getFsFormId());
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, submissionUrl);
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_SITE_ID, loadedSite.getId());

        if (isFormSaveCacheSafe(submissionUrl, loadedSite.getId())) {
            fillODKForm(subStage.getJrFormId());
        }

    }

    @Override
    public void onFormItemLongClicked(SubStage subStage) {
        ToastUtils.showShortToastInMiddle("Not implemented");
    }

    @Override
    public void onFormHistoryButtonClicked(SubStage subStage) {
        PreviousSubmissionListActivity.start(getActivity(),
                subStage.getFsFormId(),
                subStage.getName(),
                subStage.getName(),
                null,
                loadedSite.getId(),
                null,
                TABLE_GENERAL_FORM
        );
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DataSyncEvent event) {

        if (!isAdded() || getActivity() == null) {
            //Fragment is not added
            return;
        }


        Timber.i(event.toString());
        switch (event.getEvent()) {
            case DataSyncEvent.EventStatus.EVENT_START:
                SnackBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_start_message), true);
                break;
            case DataSyncEvent.EventStatus.EVENT_END:
                SnackBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_end_message), false);
                break;
            case DataSyncEvent.EventStatus.EVENT_ERROR:
                SnackBarUtils.showFlashbar(getActivity(), getString(R.string.forms_update_error_message), false);
                break;
        }
    }

}
