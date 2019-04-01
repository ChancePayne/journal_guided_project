package com.joshuahalvorson.journal;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class JournalEntrySharedPrefsRepository {
    public static final String JOURNAL_PREFERENCES = "JournalPreferences";

    private static final String ID_LIST_KEY = "id_list";
    private static final String ENTRY_ITEM_KEY_PREFIX = "entry_";
    private static final String NEXT_ID_KEY = "next_id";

    private SharedPreferences preferences;

    public JournalEntrySharedPrefsRepository(Context context) {
        this.preferences = context.getSharedPreferences(JOURNAL_PREFERENCES, Context.MODE_PRIVATE);
    }

    public void createEntry(JournalEntry entry){
        //read list of entry ids
        ArrayList<String> ids = getListOfIds();
        if(entry.getId() == JournalEntry.INVALID_ID && !ids.contains(Integer.toString(entry.getId()))){
            SharedPreferences.Editor editor = preferences.edit();

            //get next id
            int nextId = preferences.getInt(NEXT_ID_KEY, 0);
            entry.setId(nextId);
            //store updated next id
            editor.putInt(NEXT_ID_KEY, ++nextId);

            //add new id to list of ids
            ids.add(Integer.toString(entry.getId()));

            //store update id list
            StringBuilder newIdList = new StringBuilder();
            for(String id : ids){
                newIdList.append(id).append(",");
            }

            editor.putString(ID_LIST_KEY, newIdList.toString());

            //store new entry
            editor.putString(ENTRY_ITEM_KEY_PREFIX + entry.getId(), entry.toCsvString());
            editor.apply();
        }else{
            updateEntry(entry);
        }
    }

    public JournalEntry readEntry(int id){
        String entryCsv = preferences.getString(ENTRY_ITEM_KEY_PREFIX + id, "invalid");
        if(!entryCsv.equals("invalid")){
            JournalEntry entry = new JournalEntry(entryCsv);
            return entry;
        }else{
            return null;
        }
    }

    public ArrayList<JournalEntry> readAllEntries(){
        ArrayList<String> listOfIds = getListOfIds();
        ArrayList<JournalEntry> journalEntries = new ArrayList<>();
        for(String id : listOfIds){
            journalEntries.add(readEntry(Integer.parseInt(id)));
        }
        return journalEntries;
    }

    public void updateEntry(JournalEntry entry){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ENTRY_ITEM_KEY_PREFIX + entry.getId(), entry.toCsvString());
        editor.apply();
    }

    private ArrayList<String> getListOfIds(){
        String idList = preferences.getString(ID_LIST_KEY, "");
        String[] oldList = idList.split(",");
        ArrayList<String> ids = new ArrayList<>(oldList.length);
        if(!idList.equals("")){
            ids.addAll(Arrays.asList(oldList));
        }
        return ids;
    }
}
