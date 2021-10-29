package com.echdr.android.echdrapp.ui.base;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.echdr.android.echdrapp.R;

public class ListItemHolder extends BaseListItemHolder {

    public final TextView rightText;
    public final TextView subtitle2;

    public ListItemHolder(@NonNull View view) {
        super(view);
        rightText = view.findViewById(R.id.rightText);
        subtitle2 = view.findViewById(R.id.itemSubtitle2);
    }
}