package com.example.groceryapp;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.Locale;

public class filterProductUser extends Filter {
    private ArrayList<ModelProduct> list;
    private adapterProductUser adapter;

    public filterProductUser(ArrayList<ModelProduct> list, adapterProductUser adapter) {
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
            ArrayList<ModelProduct> filterModels = new ArrayList<>();
            //store are filtered list
            for(int i = 0 ; i < list.size();i++) {
                if(list.get(i).getCategory().toUpperCase().contains(charSequence) || list.get(i).getTitle().toUpperCase().contains(charSequence)) {
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
        adapter.productList = (ArrayList<ModelProduct>) filterResults.values;
        //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
