package com.enrico.categorizedapp;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SimpleViewHolder> {

    private List<String> mCategories;
    private Activity mActivity;

    //simple recycler view adapter with activity and string array as arguments
    RecyclerViewAdapter(Activity activity, List<String> categories) {
        mCategories = categories;
        mActivity = activity;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate recycler view items layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        return new SimpleViewHolder(mActivity, itemView, mCategories);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {

        //set text content according to position
        holder.textView.setText(mCategories.get(position));
    }

    @Override
    public int getItemCount() {

        //get length
        return mCategories.size();
    }

    //simple view holder implementing click and long click listeners and with activity and itemView as arguments
    static class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView textView;

        private Activity activity;

        private List<String> categories;

        SimpleViewHolder(Activity activity, View itemView, List<String> categories) {
            super(itemView);

            //get categories
            this.categories = categories;

            //get activity
            this.activity = activity;

            //get the views
            textView = itemView.findViewById(R.id.text);

            //enable click and on long click
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        //add click and long lick
        @Override
        public void onClick(View v) {

            //show me the clicked position
            Toast.makeText(activity, "Category: " + categories.get(getAdapterPosition()), Toast.LENGTH_SHORT)
                    .show();
        }

        @Override
        public boolean onLongClick(View v) {

            //show me the long clicked position
            Toast.makeText(activity, "Category: " + categories.get(getAdapterPosition()), Toast.LENGTH_SHORT)
                    .show();

            return false;
        }
    }
}