package org.bcss.collect.naxa;

public interface OnItemClickListener<T> {

    void onClickPrimaryAction(T t);

    void onClickSecondaryAction(T t);
}
