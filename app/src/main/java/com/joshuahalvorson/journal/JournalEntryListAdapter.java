package com.joshuahalvorson.journal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

public class JournalEntryListAdapter extends RecyclerView.Adapter<JournalEntryListAdapter.ViewHolder> {

    public static final int EDIT_ENTRY_REQUEST_CODE = 2;
    private ArrayList<JournalEntry> journalEntries;

    public JournalEntryListAdapter(ArrayList<JournalEntry> journalEntries) {
        this.journalEntries = journalEntries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View entryView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.entry_list_element_layout, viewGroup, false);
        return new ViewHolder(entryView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final JournalEntry entry = journalEntries.get(i);

        viewHolder.dateText.setText(entry.getDate());
        viewHolder.ratingText.setText("Rating: " + Integer.toString(entry.getRating()));
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

        if (entry.getRating() < 2){
            viewHolder.parent.setBackgroundColor(Color.RED);
        }else if (entry.getRating() >= 2 && entry.getRating() < 4){
            viewHolder.parent.setBackgroundColor(Color.YELLOW);
        }else if (entry.getRating() >= 4 && entry.getRating() < 6){
            viewHolder.parent.setBackgroundColor(Color.GREEN);
        }
    }

    @Override
    public int getItemCount() {
        return this.journalEntries.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private CardView parent;
        private TextView dateText, ratingText, entryText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.parent = itemView.findViewById(R.id.element_parent);
            this.dateText = itemView.findViewById(R.id.entry_date);
            this.ratingText = itemView.findViewById(R.id.element_day_rating);
            this.entryText = itemView.findViewById(R.id.entry_text);
        }
    }
}
