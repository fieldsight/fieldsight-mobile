package org.bcss.collect.naxa.onboarding;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.bcss.collect.android.R;
import org.bcss.collect.android.application.Collect;

public class CheckedItem extends RelativeLayout implements Checkable {

    private CheckBox c;
    private TextView tvUpdateInfo;
    private TextView title;
    private TextView tvSubtitle;
    private ProgressBar progressBar;
    private int sucessColor, failureColor, progressColor;
    private boolean isIndeterminate = true;


    public CheckedItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckedItem);
        String text = typedArray.getString(R.styleable.CheckedItem_checked_item_title);
        String buttonText = typedArray.getString(R.styleable.CheckedItem_checked_item_title);
        title.setText(text);
        tvSubtitle.setText(buttonText);
    }

    public CheckedItem(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckedItem);
        String text = typedArray.getString(R.styleable.CheckedItem_checked_item_title);
        String subtitle = typedArray.getString(R.styleable.CheckedItem_checked_item_subtitle);
        title.setText(text);
        tvSubtitle.setText(subtitle);
    }

    public CheckedItem(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        View rootView = inflate(context, R.layout.checkable_item, this);
        c = rootView.findViewById(R.id.checkbox);
        tvUpdateInfo = rootView.findViewById(R.id.update_info);
        title = rootView.findViewById(R.id.text1);
        tvSubtitle = rootView.findViewById(R.id.text3);
        sucessColor = Color.parseColor("#4CAF50");
        failureColor = Color.RED;
        progressColor = Color.parseColor("#FF5722");
        progressBar = rootView.findViewById(R.id.update_progress);
    }


    public void setText(String title, String subtitle) {
        this.title.setText(title);
        this.tvSubtitle.setText(subtitle);
    }

    public void showFailureMessage(String message) {
        tvUpdateInfo.setTextColor(failureColor);
        tvUpdateInfo.setText(message);
    }

    public void showSucessMessage(String message) {
        tvUpdateInfo.setTextColor(sucessColor);
        tvUpdateInfo.setText(message);
    }

    public void showProgressMessage(String message) {
        tvUpdateInfo.setTextColor(progressColor);
        tvUpdateInfo.setText(message);
    }

    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        showProgressMessage(Collect.getInstance().getString(R.string.please_wait_long));
    }

    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    public void setProgress(int total, int progress, String message) {
        progressBar.setMax(total);
        progressBar.setProgress(progress);
    }

    public void setup(boolean isIndeterminate) {
        progressBar.setIndeterminate(isIndeterminate);
    }

    @Override
    public void setChecked(boolean checked) {
        c.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return c.isChecked();

    }

    @Override
    public void toggle() {
        c.setChecked(!c.isChecked());
    }
}
