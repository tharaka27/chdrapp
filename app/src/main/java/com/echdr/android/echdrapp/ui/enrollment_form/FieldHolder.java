package com.echdr.android.echdrapp.ui.enrollment_form;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.service.forms.FormField;

abstract class FieldHolder extends RecyclerView.ViewHolder {
    final FormAdapter.OnValueSaved valueSavedListener;

    TextView label;

    FieldHolder(@NonNull View itemView, FormAdapter.OnValueSaved valueSavedListener) {
        super(itemView);
        this.label = itemView.findViewById(R.id.label);
        this.valueSavedListener = valueSavedListener;
    }

    void bind(FormField fieldItem) {
        label.setText(fieldItem.getFormLabel());
    }

    public void changeColor(int age, int weight, int height, String sex){

    }




}