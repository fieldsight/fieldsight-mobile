package org.odk.collect.naxa.substages;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.odk.collect.android.R;
import org.odk.collect.android.utilities.ToastUtils;
import org.odk.collect.naxa.common.FieldSightFormListFragment;
import org.odk.collect.naxa.common.OnFormItemClickListener;
import org.odk.collect.naxa.common.RecyclerViewEmptySupport;
import org.odk.collect.naxa.common.event.DataSyncEvent;
import org.odk.collect.naxa.common.utilities.FlashBarUtils;
import org.odk.collect.naxa.generalforms.ViewModelFactory;
import org.odk.collect.naxa.login.model.Site;
import org.odk.collect.naxa.stages.data.SubStage;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

import static org.odk.collect.naxa.common.Constant.EXTRA_ID;
import static org.odk.collect.naxa.common.Constant.EXTRA_OBJECT;

public class SubStageListFragment extends FieldSightFormListFragment implements OnFormItemClickListener<SubStage> {

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.android_list)
    RecyclerViewEmptySupport recyclerView;

    @BindView(R.id.root_layout_general_form_frag)
    LinearLayout rootLayout;

    @BindView(R.id.root_layout_empty_layout)
    RelativeLayout emptyLayout;

    private SubStageListAdapter listAdapter;
    private Site loadedSite;
    private String stageId;

    private Unbinder unbinder;

    private SubStageViewModel viewModel;

    public static SubStageListFragment newInstance(@NonNull Site loadedSite, @NonNull String stageId) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        bundle.putString(EXTRA_ID, stageId);
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.general_forms_list_fragment, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        viewModelFactory = ViewModelFactory.getInstance(getActivity().getApplication());

        viewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(SubStageViewModel.class);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();
        viewModel.loadSubStages(true, loadedSite.getId(), loadedSite.getProject(), stageId)
                .observe(this, substages -> {
                    Timber.i("SubStage forms data has been changed");
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
            viewModel.loadSubStages(true,loadedSite.getId(),loadedSite.getProject(),stageId);
        });
        listAdapter = new SubStageListAdapter(new ArrayList<>(0));
        recyclerView.setAdapter(listAdapter);
    }


    @Override
    public void onGuideBookButtonClicked(SubStage subStage, int position) {

    }

    @Override
    public void onFormItemClicked(SubStage subStage) {

    }

    @Override
    public void onFormItemLongClicked(SubStage subStage) {
        ToastUtils.showShortToastInMiddle("Not implmeneted");
    }

    @Override
    public void onFormHistoryButtonClicked(SubStage subStage) {

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