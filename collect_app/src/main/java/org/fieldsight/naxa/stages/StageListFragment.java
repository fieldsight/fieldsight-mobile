package org.fieldsight.naxa.stages;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.OnFormItemClickListener;
import org.fieldsight.naxa.common.RecyclerViewEmptySupport;
import org.fieldsight.naxa.common.ViewModelFactory;
import org.fieldsight.naxa.forms.data.local.FieldSightFormsLocalSourcev3;
import org.fieldsight.naxa.login.model.Project;
import org.fieldsight.naxa.login.model.Site;
import org.fieldsight.naxa.project.data.ProjectLocalSource;
import org.fieldsight.naxa.stages.data.Stage;
import org.fieldsight.naxa.substages.SubStageListFragment;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static org.fieldsight.naxa.common.Constant.ANIM.FRAGMENT_ENTER_ANIMATION;
import static org.fieldsight.naxa.common.Constant.ANIM.FRAGMENT_EXIT_ANIMATION;
import static org.fieldsight.naxa.common.Constant.ANIM.FRAGMENT_POP_ENTER_ANIMATION;
import static org.fieldsight.naxa.common.Constant.ANIM.FRAGMENT_POP_EXIT_ANIMATION;
import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class StageListFragment extends Fragment implements OnFormItemClickListener<Stage> {

    @Inject
    ViewModelFactory viewModelFactory;

    @BindView(R.id.android_list)
    RecyclerViewEmptySupport recyclerView;

    @BindView(R.id.root_layout_general_form_frag)
    LinearLayout rootLayout;

    private StageListAdapter listAdapter;
    private Site loadedSite;
    Unbinder unbinder;


    @BindView(R.id.root_layout_empty_layout)
    public LinearLayout emptyLayout;

    public static StageListFragment newInstance(@NonNull Site loadedSite) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_OBJECT, loadedSite);
        StageListFragment scheduleListFragment = new StageListFragment();
        scheduleListFragment.setArguments(bundle);
        return scheduleListFragment;

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
        setToolbarText();
        return rootView;
    }


    private void setToolbarText() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.toolbar_stages);
        toolbar.setSubtitle(loadedSite.getName());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();
        Project project = ProjectLocalSource.getInstance().getProject(loadedSite.getProject());
        FieldSightFormsLocalSourcev3.getInstance().getStageForms(loadedSite.getProject(), loadedSite.getSite(), loadedSite.getTypeId(), loadedSite.getRegionId(), project)
                .observe(this, stages -> listAdapter.updateList(stages));


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
        recyclerView.setEmptyView(emptyLayout, getString(R.string.empty_message, "staged FORMS")
                , new RecyclerViewEmptySupport.OnEmptyLayoutClickListener() {
                    @Override
                    public void onRetryButtonClick() {

                    }
                });
        listAdapter = new StageListAdapter(new ArrayList<>(0), this);
        recyclerView.setAdapter(listAdapter);
    }


    @Override
    public void onGuideBookButtonClicked(Stage stage, int position) {

    }

    @Override
    public void onFormItemClicked(Stage stage, int position) {
        Fragment fragment = SubStageListFragment.newInstance(loadedSite, stage.getSubStage(), String.valueOf(position));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(FRAGMENT_ENTER_ANIMATION, FRAGMENT_EXIT_ANIMATION, FRAGMENT_POP_ENTER_ANIMATION, FRAGMENT_POP_EXIT_ANIMATION);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack("myfrag3");
        fragmentTransaction.commit();
    }

    @Override
    public void onFormItemLongClicked(Stage stage) {

    }

    @Override
    public void onFormHistoryButtonClicked(Stage stage) {


    }


}
