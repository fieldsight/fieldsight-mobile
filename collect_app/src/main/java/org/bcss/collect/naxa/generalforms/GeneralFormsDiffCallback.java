package org.bcss.collect.naxa.generalforms;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import org.bcss.collect.naxa.generalforms.data.GeneralForm;

import java.util.List;


public class GeneralFormsDiffCallback extends DiffUtil.Callback {

    private List<GeneralForm> oldGeneralForms;
    private List<GeneralForm> newGeneralForms;

    public GeneralFormsDiffCallback(List<GeneralForm> newGeneralForms, List<GeneralForm> oldGeneralForms) {
        this.newGeneralForms = newGeneralForms;
        this.oldGeneralForms = oldGeneralForms;
    }

    @Override
    public int getOldListSize() {

        return oldGeneralForms.size();
    }

    @Override
    public int getNewListSize() {

        return newGeneralForms.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldGeneralForms.get(oldItemPosition).getFsFormId()
                .equals(newGeneralForms.get(newItemPosition).getFsFormId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldGeneralForms.get(oldItemPosition).equals(newGeneralForms.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
