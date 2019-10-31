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

import org.fieldsight.collect.android.R;
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

public class FormsStateFragment extends Fragment {

    private RecyclerView recyclerView;
    private View emptyLayout;
    private View progressBar;
    private BaseRecyclerViewAdapter<FormState, FieldSightFormStateVH> adapter;
    private String submissionStatus;

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
        emptyLayout = view.findViewById(R.id.root_layout_empty_layout);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupListAdapter();
        getFormUsingProjectId()
                .subscribe(new SingleObserver<List<FormState>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onSuccess(List<FormState> formStates) {
                        updateList(formStates);
                        progressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

        requireActivity()
                .setTitle(submissionStatus + " form(s)");
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


    private Single<List<FormState>> getFormUsingProjectId() {
        return ServiceGenerator.getRxClient().create(ApiV3Interface.class)
                .getMyFlaggedSubmissionAsRaw(APIEndpoint.V3.GET_MY_FLAGGED_SUBMISSIONS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap((Function<ResponseBody, ObservableSource<JSONObject>>) responseBody -> {
                    JSONArray jsonArray = new JSONArray(responseBody.string());
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
                        return TextUtils.equals(formState.getStatusDisplay().toLowerCase(),
                                submissionStatus.toLowerCase());
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


                        FieldSightNotification notification = new FieldSightNotificationBuilder()
                                .setFormSubmissionId(String.valueOf(form.getPk()))
                                .setFormName(form.getFormName())
                                .setIdString("")
                                .setFormVersion("")
                                .createFieldSightNotification();

                        FlaggedInstanceActivity.startWithForm(requireActivity(), notification);
                    }
                };
            }
        };
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
    }


}
