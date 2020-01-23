package org.fieldsight.naxa.v3.project;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;

import org.bcss.collect.android.R;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class SubmissionFragment extends Fragment implements TabLayout.OnTabSelectedListener {

    @BindView(R.id.tv_error)
    TextView tv_error;

    @BindView(R.id.rv_forms)
    RecyclerView rvForms;

    @BindView(R.id.tabs)
    TabLayout tabLayout;
    List<Object> generalFormList = new ArrayList<>();
    List<Object> scheduleList = new ArrayList<>();
    List<Object> stagedFormList = new ArrayList<>();
    List<List<Object>> formList = new ArrayList<>();

    private Unbinder unbinder;

    class SubmissonFormsStat {
        String name;
        int responseCount;

        public SubmissonFormsStat(JSONObject json) {
            this.name = json.has("name") ? json.optString("name") : json.optString("title");
            this.responseCount = json.optInt("response_count");
        }
    }

    private SubmissionFragment() {
    }

    public static SubmissionFragment getInstance(String data) {
        Bundle bundle = new Bundle();
        bundle.putString("forms", data);
        SubmissionFragment fragment = new SubmissionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_submission, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null || !getArguments().containsKey("forms")) {
            return;
        }
        String forms = getArguments().getString("forms");
        Timber.i("forms = %s", forms);
        try {
            JSONObject jsonObject = new JSONObject(forms);
            JSONArray generalFormArray = jsonObject.optJSONArray("general_forms");
            JSONArray scheduledArray = jsonObject.optJSONArray("scheduled_forms");
            JSONArray stagedFromArray = jsonObject.optJSONArray("staged_forms");

            for (int i = 0; i < generalFormArray.length(); i++) {
                generalFormList.add(new SubmissonFormsStat(generalFormArray.optJSONObject(i)));
            }

            for (int i = 0; i < scheduledArray.length(); i++) {
                scheduleList.add(new SubmissonFormsStat(scheduledArray.optJSONObject(i)));
            }

            for (int i = 0; i < stagedFromArray.length(); i++) {
                JSONObject stageJSON = stagedFromArray.optJSONObject(i);
                String name = stageJSON.optString("name");
                stagedFormList.add(name);
                JSONArray subStageArray = stageJSON.optJSONArray("sub_stages");
                for (int j = 0; j < subStageArray.length(); j++) {
                    stagedFormList.add(new SubmissonFormsStat(subStageArray.optJSONObject(j)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        formList.add(0, generalFormList);
        formList.add(1, scheduleList);
        formList.add(2, stagedFormList);
        tabLayout.addOnTabSelectedListener(this);
        tabLayout.getTabAt(0).select();
        updateList(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    void updateList(int pos) {
        List<Object> selectedList = formList.get(pos);
        rvForms.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvForms.setAdapter(new FormsAdapter(selectedList));
        rvForms.setHasFixedSize(true);
        tv_error.setVisibility(selectedList.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        updateList(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}

class FormsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Object> submissionList;
    int HeaderType = 0, dataType = 1;

    public FormsAdapter(List<Object> submissionList) {
        this.submissionList = submissionList;
    }

    @Override
    public int getItemViewType(int position) {
        return submissionList.get(position) instanceof String ? HeaderType : dataType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HeaderType) {
            TextView tv = (TextView) LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            tv.setTypeface(null, Typeface.BOLD);
            return new FormTitleViewHolder(tv);
        } else {
            return new FormsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.form_project_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FormTitleViewHolder) {
            FormTitleViewHolder mHolder = (FormTitleViewHolder) holder;
            String name = (String) submissionList.get(position);
            ((TextView) mHolder.itemView).setText(name);
        } else {
            FormsViewHolder mHolder = (FormsViewHolder) holder;
            SubmissionFragment.SubmissonFormsStat sStat = (SubmissionFragment.SubmissonFormsStat) submissionList.get(position);
            mHolder.formName.setText("  " + sStat.name);

            mHolder.submissonCount.setText(String.valueOf(sStat.responseCount));
        }
    }

    @Override
    public int getItemCount() {
        return submissionList.size();
    }
}

class FormTitleViewHolder extends RecyclerView.ViewHolder {
    public FormTitleViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}

class FormsViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_form_name)
    TextView formName;

    @BindView(R.id.tv_submissonCount)
    TextView submissonCount;

    public FormsViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
