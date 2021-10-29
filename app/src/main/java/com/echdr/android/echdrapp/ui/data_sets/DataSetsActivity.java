package com.echdr.android.echdrapp.ui.data_sets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.paging.PagedList;

import com.echdr.android.echdrapp.R;
import com.echdr.android.echdrapp.data.Sdk;
import com.echdr.android.echdrapp.ui.base.ListActivity;

import org.hisp.dhis.android.core.dataset.DataSet;

public class DataSetsActivity extends ListActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context,DataSetsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUp(R.layout.activity_data_sets, R.id.dataSetsRecyclerView);
        observeDataSets();
    }

    private void observeDataSets() {
        DataSetsAdapter adapter = new DataSetsAdapter();
        recyclerView.setAdapter(adapter);

        LiveData<PagedList<DataSet>> liveData = Sdk.d2().dataSetModule().dataSets()
                .getPaged(20);

        liveData.observe(this, dataSetPagedList -> {
            adapter.submitList(dataSetPagedList);
            findViewById(R.id.dataSetsNotificator).setVisibility(
                    dataSetPagedList.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
