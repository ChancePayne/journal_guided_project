package com.joshuahalvorson.journal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class JournalEntryListAdapter extends RecyclerView.Adapter<JournalEntryListAdapter.ViewHolder> {

    public static final int EDIT_ENTRY_REQUEST_CODE = 2;
    private ArrayList<JournalEntry> journalEntries;
    Context context;

    public JournalEntryListAdapter(ArrayList<JournalEntry> journalEntries) {
        this.journalEntries = journalEntries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View entryView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.entry_list_element_layout, viewGroup, false);
        context = viewGroup.getContext();
        return new ViewHolder(entryView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final JournalEntry entry = journalEntries.get(i);

        viewHolder.dateText.setText(entry.getDate());
        String entryText = entry.getEntryText();
        if(entryText.length() > 30){
            entryText = entryText.substring(0, 25) + "...";
        }
        viewHolder.entryText.setText(entryText);

        viewHolder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(v.getContext(), JournalDetails.class);
                detailIntent.putExtra(JournalEntry.TAG, entry);
                ((Activity) v.getContext())
                        .startActivityForResult(detailIntent, EDIT_ENTRY_REQUEST_CODE);
            }
        });

        switch (entry.getRating()){
            case 0:
                viewHolder.parent.setBackgroundColor(context.getResources().getColor(R.color.colorRating0));
                viewHolder.entryRatingImageView.setImageDrawable(context.getDrawable(R.drawable.ic_day_rating_0));
                break;
            case 1:
                viewHolder.parent.setBackgroundColor(context.getResources().getColor(R.color.colorRating1));
                viewHolder.entryRatingImageView.setImageDrawable(context.getDrawable(R.drawable.ic_day_rating_1));
                break;
            case 2:
                viewHolder.parent.setBackgroundColor(context.getResources().getColor(R.color.colorRating2));
                viewHolder.entryRatingImageView.setImageDrawable(context.getDrawable(R.drawable.ic_day_rating_2));
                break;
            case 3:
                viewHolder.parent.setBackgroundColor(context.getResources().getColor(R.color.colorRating3));
                viewHolder.entryRatingImageView.setImageDrawable(context.getDrawable(R.drawable.ic_day_rating_3));
                break;
            case 4:
                viewHolder.parent.setBackgroundColor(context.getResources().getColor(R.color.colorRating4));
                viewHolder.entryRatingImageView.setImageDrawable(context.getDrawable(R.drawable.ic_day_rating_4));
                break;
            case 5:
                viewHolder.parent.setBackgroundColor(context.getResources().getColor(R.color.colorRating5));
                viewHolder.entryRatingImageView.setImageDrawable(context.getDrawable(R.drawable.ic_day_rating_5));
                break;

        }
    }

    @Override
    public int getItemCount() {
        return this.journalEntries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private CardView parent;
        private TextView dateText, entryText;
        private ImageView entryRatingImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.parent = itemView.findViewById(R.id.element_parent);
            this.dateText = itemView.findViewById(R.id.entry_date);
            this.entryRatingImageView = itemView.findViewById(R.id.element_day_rating);
            this.entryText = itemView.findViewById(R.id.entry_text);
        }
    }
}
