package org.bcss.collect.naxa.common;

public interface OnFormItemClickListener<T> {

    void onGuideBookButtonClicked(T t, int position);

    void onFormItemClicked(T t, int position);

    void onFormItemLongClicked(T t);

    void onFormHistoryButtonClicked(T t);

}
