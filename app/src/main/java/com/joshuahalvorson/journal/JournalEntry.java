package com.joshuahalvorson.journal;

import android.net.Uri;

import java.io.Serializable;

public class JournalEntry implements Serializable {
    public static final String TAG = "journalentry";

    public static final int INVALID_ID = -1;

    private String entryText, date, imageUri;
    private int rating, id;

    public JournalEntry(String date, String entryText, String imageUri, int rating, int id) {
        this.entryText = entryText;
        this.date = date;
        this.imageUri = imageUri;
        this.rating = rating;
        this.id = id;
    }

    public JournalEntry(int id) {
        this.id = id;
        this.imageUri = "";
        this.entryText = "";
        this.date = "";
    }

    public JournalEntry(String csvString){
        String[] values = csvString.split(",");
        if(values.length == 5){
            try {
                this.id = Integer.parseInt(values[0]);
            }catch (NumberFormatException e){
                e.printStackTrace();
            }
            this.date = values[1];
            try {
                this.rating = Integer.parseInt(values[2]);
            }catch (NumberFormatException e ){
                e.printStackTrace();
            }
            this.entryText = values[3].replace("~@", ",");
            this.imageUri = values[4].equals("unused") ? "" : values[4];
        }
    }

    public String toCsvString(){
        return String.format(
                "%d,%s,%d,%s,%s",
                id,
                date,
                rating,
                entryText.replace(",", "~@"),
                imageUri == "" ? "unused" : imageUri
                );
    }

    public String getEntryText() {
        return entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Uri getImageUri() {
        return Uri.parse(imageUri);
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri.toString();
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
