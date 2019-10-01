package org.fieldsight.naxa.forms.ui;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.forms.data.local.FieldSightFormDetails;
import org.fieldsight.naxa.forms.data.local.FieldsightFormDetailsv3;
import org.odk.collect.android.logic.FormDetails;

public class FieldSightFormVH extends RecyclerView.ViewHolder {
    private TextView tvTitle, tvSubtitle, tvIconText;

    protected FieldSightFormVH(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.tv_form_primary);
        tvSubtitle = itemView.findViewById(R.id.tv_form_secondary);
        tvIconText = itemView.findViewById(R.id.form_icon_text);


    }

    public void bindView(FieldsightFormDetailsv3 form) {
        FormDetails formDetails = form.getFormDetails();

        tvTitle.setText(formDetails.getFormName());
        tvSubtitle.setText(formDetails.getFormName());
        tvIconText.setText(formDetails.getFormName().substring(0, 1));
        itemView.setOnClickListener(view -> openForm(form));

    }

    public void openForm(FieldsightFormDetailsv3 form) {

    }


}
