package com.example.groceryapp;

import android.widget.Filter;

import java.util.ArrayList;

public class filtersOrdersSeller extends Filter {
    private ArrayList<ModelOrderSeller> list;
    private AdapterConfirmOrdersSeller adapter;

    public filtersOrdersSeller(ArrayList<ModelOrderSeller> list, AdapterConfirmOrdersSeller adapter) {
        this.list = list;
        this.adapter = adapter;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        //validate data for search query
        if(charSequence  != null && charSequence.length() > 0) {
            //change to uppercase to make case sensitive
            charSequence = charSequence.toString().toUpperCase();
            ArrayList<ModelOrderSeller> filterModels = new ArrayList<>();
            //store are filtered list
            for(int i = 0 ; i < list.size();i++) {
                if(list.get(i).getOrderStatus().toUpperCase().contains(charSequence)) {
                    filterModels.add(list.get(i));
                }
            }
            results.count = filterModels.size();
            results.values = filterModels;
        }
        else {
            results.count = list.size();
            results.values = list;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.ordersList = (ArrayList<ModelOrderSeller>) filterResults.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
