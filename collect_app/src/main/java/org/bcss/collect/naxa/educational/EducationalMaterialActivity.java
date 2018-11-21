package org.bcss.collect.naxa.educational;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.activities.CollectAbstractActivity;
import org.bcss.collect.naxa.common.Constant;
import org.bcss.collect.naxa.generalforms.data.GeneralForm;
import org.bcss.collect.naxa.previoussubmission.model.GeneralFormAndSubmission;
import org.bcss.collect.naxa.scheduled.data.ScheduleForm;
import org.bcss.collect.naxa.stages.data.SubStage;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;

import static org.bcss.collect.naxa.common.Constant.EXTRA_OBJECT;

public class EducationalMaterialActivity extends CollectAbstractActivity {

    @BindView(R.id.text)
    TextView textView;

    private ArrayList<String> fsFormIds;

    private static Single<List<String>> getFsFormIds(ArrayList<GeneralFormAndSubmission> list) {

        return Observable.just(list)
                .flatMapIterable((Function<ArrayList<GeneralFormAndSubmission>, Iterable<GeneralFormAndSubmission>>) generalForms -> generalForms)
                .map(generalFormAndSubmission -> generalFormAndSubmission.getGeneralForm().getFsFormId())
                .toList();
    }


    public static void start(Context context, ArrayList<GeneralFormAndSubmission> list) {
        WeakReference<Context> weakReference = new WeakReference<Context>(context);

        getFsFormIds(list)
                .subscribe(new DisposableSingleObserver<List<String>>() {
                    @Override
                    public void onSuccess(List<String> fsFormIds) {
                        Intent intent = new Intent(weakReference.get(), EducationalMaterialActivity.class);
                        intent.putStringArrayListExtra(EXTRA_OBJECT, (ArrayList<String>) fsFormIds);
                        weakReference.get().startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });


//        throw new IllegalArgumentException("Scheduled, SubStage or General form is required to load this activity");
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_educational_material);
        ButterKnife.bind(this);

        fsFormIds = getIntent().getStringArrayListExtra(EXTRA_OBJECT);
        textView.setText(fsFormIds.toString());
    }
}
