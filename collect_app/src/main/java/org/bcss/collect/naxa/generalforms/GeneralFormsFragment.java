package org.bcss.collect.naxa.generalforms;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;

import org.bcss.collect.naxa.educational.EducationalMaterialActivity;
import org.bcss.collect.naxa.previoussubmission.model.GeneralFormAndSubmission;
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
import org.bcss.collect.naxa.common.ViewModelFactory;
import org.bcss.collect.naxa.common.event.DataSyncEvent;
import org.bcss.collect.naxa.common.utilities.FlashBarUtils;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.login.model.Site;
import org.bcss.collect.naxa.site.FragmentHostActivity;

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
import static org.bcss.collect.naxa.common.Constant.FormDeploymentFrom.SITE;
import static org.bcss.collect.naxa.generalforms.data.FormType.TABLE_GENERAL_FORM;

public class GeneralFormsFragment extends FieldSightFormListFragment implements OnFormItemClickListener<GeneralForm> {

    @Inject
    ViewModelFactory viewModelFactory;

    private GeneralFormViewModel viewModel;

    @BindView(R.id.android_list)
    RecyclerViewEmptySupport recyclerView;

    @BindView(R.id.root_layout_general_form_frag)
    LinearLayout rootLayout;

    @BindView(R.id.root_layout_empty_layout)
    View emptyLayout;



    Unbinder unbinder;
    private GeneralFormsAdapter generalFormsAdapter;

    private Site loadedSite;

    public static GeneralFormsFragment newInstance(Site loadedSite) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        GeneralFormsFragment generalFormsFragment = new GeneralFormsFragment();
        generalFormsFragment.setArguments(bundle);
        return generalFormsFragment;

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

        setToolbarText();
        return rootView;
    }

    private void setToolbarText() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_general_forms);
        toolbar.setSubtitle(loadedSite.getName());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadedSite = getArguments().getParcelable(EXTRA_OBJECT);
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

        viewModel.getFormsAndSubmission(loadedSite)
                .observe(this, new Observer<List<GeneralFormAndSubmission>>() {
                    @Override
                    public void onChanged(@Nullable List<GeneralFormAndSubmission> generalFormAndSubmissions) {
                        if (generalFormsAdapter.getItemCount() == 0) {
                            runLayoutAnimation(recyclerView);
                        }

                        generalFormsAdapter.updateList(generalFormAndSubmissions);

                    }
                });
    }


    private void setupListAdapter() {

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        generalFormsAdapter = new GeneralFormsAdapter(new ArrayList<>(0), this);

        recyclerView.setEmptyView(emptyLayout,
                getString(R.string.empty_message, "general forms"),
                () -> {

                });
        recyclerView.setAdapter(generalFormsAdapter);

    }

    @Override
    public void onGuideBookButtonClicked(GeneralForm generalForm, int position) {
        EducationalMaterialActivity.startFromGeneral(getActivity(),generalFormsAdapter.getAll(),position);
    }

    @Override
    public void onFormItemClicked(GeneralForm generalForm, int position) {

        String submissionUrl = generateSubmissionUrl(loadedSite.getGeneralFormDeployedFrom(), loadedSite.getId(), generalForm.getFsFormId());
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_URL, submissionUrl);
        SharedPreferenceUtils.saveToPrefs(Collect.getInstance().getApplicationContext(), SharedPreferenceUtils.PREF_VALUE_KEY.KEY_SITE_ID, loadedSite.getId());

        fillODKForm(generalForm.getIdString());
    }

    @Override
    public void onFormItemLongClicked(GeneralForm generalForm) {

    }

    @Override
    public void onFormHistoryButtonClicked(GeneralForm generalForm) {
        PreviousSubmissionListActivity.start(getActivity(),
                generalForm.getFsFormId(),
                generalForm.getName(),
                generalForm.getName(),
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
