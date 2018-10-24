package com.example.serialprimes;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.TreeMap;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PrimesAdapter extends RecyclerView.Adapter<PrimesAdapter.PrimesViewHolder> {

    private ArrayList<String> items;

    public static class PrimesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.value)
        public TextView value;

        public PrimesViewHolder(View container) {
            super(container);
            ButterKnife.bind(this, container);
        }

    }

    public PrimesAdapter(TreeMap<Long, Long> data) {
        items = new ArrayList<>();
        for(long key : data.keySet()) {
            items.add("" + key + " [" + data.get(key) + "]");
        }
    }

    @Override
    public PrimesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.primes_item, parent, false);
        PrimesViewHolder holder = new PrimesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PrimesViewHolder holder, int position) {
        holder.value.setText(items.get(items.size() - position - 1));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}