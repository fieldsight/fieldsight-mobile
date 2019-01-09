package org.bcss.collect.naxa.submissions;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.naxa.common.DialogFactory;
import org.bcss.collect.naxa.generalforms.data.FormResponse;
import org.bcss.collect.naxa.generalforms.data.GetResponce;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.odk.collect.android.activities.CollectAbstractActivity;

import java.util.ArrayList;
import java.util.List;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class PreviousSubmissionDetailActivity extends CollectAbstractActivity implements MultiViewAdapter.OnCardClickListener {

    Toolbar toolbar;
    TextView tvQuestionAnswer;
    RecyclerView rvFormHistory;
    ArrayList<ViewModel> answers = new ArrayList<>();
    private MultiViewAdapter adapter;
    private NestedScrollView nestedScroll;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_history_detail);

        FormResponse model = getIntent().getParcelableExtra(EXTRA_OBJECT);


        bindUI();
        setupToolBar(model);


        if (model.getGetResponces() == null) {
            DialogFactory.createGenericErrorDialog(this, R.string.error_occured).show();
            finish();
        }

        //setQuestionAnswerAsync(model.getGetResponces());
        setupRecyclerView();
        mapJSONtoViewModel(model.getGetResponces());
    }


    private void bindUI() {
        toolbar = findViewById(R.id.toolbar_share_view);
        tvQuestionAnswer = findViewById(R.id.tv_question_answer);
        rvFormHistory = findViewById(R.id.form_history_detail_recycler_view);
        nestedScroll = findViewById(R.id.form_history_nested_scroll);
    }

    private void setupToolBar(FormResponse model) {
        toolbar.setTitle("Submitted by " + model.getSubmittedByUsername());
        toolbar.setSubtitle("on " + formatSubmissionDateTime(model.getDate()));
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                supportFinishAfterTransition();
            }
        });

    }

    @Override
    public void onBackPressed() {
        supportFinishAfterTransition();
    }

    private void setupRecyclerView() {
        adapter = new MultiViewAdapter();
        adapter.setOnCardClickListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvFormHistory.setLayoutManager(linearLayoutManager);
        rvFormHistory.setAdapter(adapter);
        rvFormHistory.setItemAnimator(new DefaultItemAnimator());

        rvFormHistory.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        rvFormHistory.setHasFixedSize(true);
        rvFormHistory.setNestedScrollingEnabled(false);
    }

    private void mapJSONtoViewModel(@NonNull final List<GetResponce> submissions) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                ViewModel answer;

                for (int i = 0; i < submissions.size(); i++) {
                    GetResponce response = submissions.get(i);
                    switch (response.getType()) {
                        case "start":
                        case "end":
                        case "calculate":
                        case "submitted_by":
                        case "submittion_time":
                            continue;

                    }

                    answer = new ViewModel(response.getQuestion(), response.getAnswer(), "id", "id");
                    answers.add(answer);

                }

                adapter.addAll(answers);
            }
        });


    }




    private String formatSubmissionDateTime(String dateTime) {


        String msg = "";
        try {
            DateTime dt = DateTime.parse(dateTime);
            msg = dt.toString(DateTimeFormat.longDateTime());


        } catch (Exception e) {
            e.printStackTrace();
            msg = "Cannot load date time";
        }


        return msg;
    }

    @Override
    public void onCardClicked(ViewModel viewModel) {

    }
}
