package com.wt.sample.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.wt.sample.R;

import java.util.ArrayList;
import java.util.List;

public class PublisherAdapter extends RecyclerView.Adapter<PublisherAdapter.ViewHolder> {

    private final List<String> mPublishers = new ArrayList<>();

    PublisherAdapter() {
    }

    @NonNull
    @Override
    public PublisherAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        return new PublisherAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_publisher, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final PublisherAdapter.ViewHolder viewHolder, final int position) {
        final String publisher = mPublishers.get(viewHolder.getAdapterPosition());
        if (publisher != null) {
            viewHolder.name.setText(publisher);
        }
    }

    @Override
    public int getItemCount() {
        return mPublishers.size();
    }

    void addPublisher(final String subscriber) {
        mPublishers.add(subscriber);
        notifyItemInserted(getItemCount());
    }

    void removePublisher(final String publisher) {
        final int positionToRemove = mPublishers.indexOf(publisher);
        mPublishers.remove(publisher);
        notifyItemRemoved(positionToRemove);
    }

    void clearPublishers() {
        mPublishers.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final AppCompatTextView name;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
        }
    }
}