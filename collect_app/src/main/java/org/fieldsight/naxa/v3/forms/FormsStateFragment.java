package org.fieldsight.naxa.v3.forms;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.bcss.collect.android.R;;
import org.fieldsight.naxa.common.GSONInstance;
import org.fieldsight.naxa.common.view.BaseRecyclerViewAdapter;
import org.fieldsight.naxa.data.FieldSightNotification;
import org.fieldsight.naxa.data.FieldSightNotificationBuilder;
import org.fieldsight.naxa.flagform.FlaggedInstanceActivity;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.network.ServiceGenerator;
import org.fieldsight.naxa.v3.FormState;
import org.fieldsight.naxa.v3.network.ApiV3Interface;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static org.fieldsight.naxa.common.Constant.EXTRA_MESSAGE;
import static org.fieldsight.naxa.common.Constant.FormStatus.FLAGGED;

public class FormsStateFragment extends Fragment {

    private RecyclerView recyclerView;

    private SwipeRefreshLayout swipeToRefresh;
    private View emptyLayout;
    private BaseRecyclerViewAdapter<FormState, FieldSightFormStateVH> adapter;
    private String submissionStatus;
    private String url = APIEndpoint.V3.GET_MY_FLAGGED_SUBMISSIONS;

    public static FormsStateFragment newInstance(String type) {
        FormsStateFragment fragment = new FormsStateFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_MESSAGE, type);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fieldsight_list_fragment, container, false);
        submissionStatus = getArguments().getString(EXTRA_MESSAGE);
        bindUI(rootView);

        return rootView;
    }

    private void bindUI(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_list);
        swipeToRefresh = view.findViewById(R.id.swipe_container);
        emptyLayout = view.findViewById(R.id.root_layout_empty_layout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();
        setupSwipeToRefresh();
        forceReload();
        requireActivity().setTitle(FLAGGED.equalsIgnoreCase(submissionStatus) ? R.string.nav_flagged_form
                : R.string.nav_rejected_form);
    }

    private void setupSwipeToRefresh() {
        swipeToRefresh.setOnRefreshListener(() -> {
            url = APIEndpoint.V3.GET_MY_FLAGGED_SUBMISSIONS;
            forceReload();
        });
    }

    private void forceReload() {
        getFormUsingProjectId(url)
                .subscribe(new SingleObserver<List<FormState>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        swipeToRefresh.setRefreshing(true);
                    }

                    @Override
                    public void onSuccess(List<FormState> formStates) {
                        updateList(formStates);
                        swipeToRefresh.setRefreshing(false);
                        emptyLayout.setVisibility(formStates.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        swipeToRefresh.setRefreshing(false);
                        emptyLayout.setVisibility(View.VISIBLE);
                    }
                });

    }


    public void updateList(List<FormState> fieldSightForms) {
        adapter.getData().clear();
        adapter.getData().addAll(fieldSightForms);
        adapter.notifyDataSetChanged();
    }

    public void appendToList(List<FormState> fieldSightForms) {
        adapter.getData().addAll(fieldSightForms);
        adapter.notifyDataSetChanged();
    }


    private Single<List<FormState>> getFormUsingProjectId(String url) {
        return ServiceGenerator.createCacheService(ApiV3Interface.class)
                .getMyFlaggedSubmissionAsRaw(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<ResponseBody, ObservableSource<JSONObject>>) responseBody -> {
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    JSONArray jsonArray = jsonObject.optJSONArray("results");
                    this.url = "null".equals(jsonObject.optString("next")) ? null : jsonObject.optString("next");

                    return Observable.range(0, jsonArray.length())
                            .map(jsonArray::getJSONObject);
                })
                .map(new Function<JSONObject, FormState>() {
                    @Override
                    public FormState apply(JSONObject jsonObject) throws Exception {
                        return GSONInstance.getInstance().fromJson(jsonObject.toString(), FormState.class);
                    }
                })
                .filter(new Predicate<FormState>() {
                    @Override
                    public boolean test(FormState formState) throws Exception {
                        return TextUtils.equals(formState.getStatusDisplay().toLowerCase(Locale.getDefault()),
                                submissionStatus.toLowerCase(Locale.getDefault()));
                    }
                })
                .toList();
    }

    private void setupListAdapter() {
        LinearLayoutManager manager = new LinearLayoutManager(requireActivity());
        adapter = new BaseRecyclerViewAdapter<FormState, FieldSightFormStateVH>(new ArrayList<>(), R.layout.list_item_form_state) {
            @Override
            public void viewBinded(FieldSightFormStateVH activityVH, FormState activity) {
                activityVH.bindView(activity);
            }

            @Override
            public FieldSightFormStateVH attachViewHolder(View view) {
                return new FieldSightFormStateVH(view) {
                    @Override
                    public void onItemTapped(FormState form) {

                        if (isSafeToLoad(form)) {
                            FieldSightNotification notification = new FieldSightNotificationBuilder()
                                    .setFormSubmissionId(String.valueOf(form.getFsFormId()))
                                    .setSiteId(String.valueOf(form.getSite()))
                                    .setFormName(form.getFormName())
                                    .setIdString(form.getIdString())
                                    .setFormVersion(form.getVersion())
                                    .setFsFormId(form.getProjectFxf() != null ?
                                            form.getProjectFxf() :
                                            String.valueOf(form.getSiteFxf()))
                                    .createFieldSightNotification();

                            FlaggedInstanceActivity.startWithForm(requireActivity(), notification);
                        }


                    }
                };
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)
                        && newState == RecyclerView.SCROLL_STATE_IDLE
                        && !TextUtils.isEmpty(url)
                ) {

                    getFormUsingProjectId(url)
                            .subscribe(new SingleObserver<List<FormState>>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    swipeToRefresh.setRefreshing(true);
                                }

                                @Override
                                public void onSuccess(List<FormState> formStates) {
                                    appendToList(formStates);
                                    swipeToRefresh.setRefreshing(false);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    swipeToRefresh.setRefreshing(false);
                                }
                            });
                }
            }
        });
    }

    private boolean isSafeToLoad(FormState form) {
        return form != null;
    }
}
