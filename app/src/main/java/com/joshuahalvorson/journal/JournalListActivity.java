package com.joshuahalvorson.journal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class JournalListActivity extends AppCompatActivity {

    public static final int EDIT_ENTRY_REQUEST_CODE = 2;
    public static final int NEW_ENTRY_REQUEST = 1;
    public static final int NOTIFICATION_ID = 26;
    public static final String NEW_ENTRY_ACTION = "new_entry_action";
    public static final int INPUT_INTENT_REQUEST_CODE = 101;

    public static int nextId = 0;

    private RecyclerView entryRecyclerView;

    private JournalEntryListAdapter journalEntryListAdapter;

    private ArrayList<JournalEntry> journalEntries;

    private Context context;

    private JournalEntrySharedPrefsRepository repository;

    public static String channelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getLocalClassName(), "onCreate*");
        setContentView(R.layout.activity_journal_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        repository = new JournalEntrySharedPrefsRepository(context);
        channelId = getPackageName() + ".notification";

        entryRecyclerView = findViewById(R.id.entry_recycler_view);

        journalEntries = new ArrayList<>();
        //addTestEntries();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, JournalDetails.class);
                JournalEntry entry = createJournalEntry();
                intent.putExtra(JournalEntry.TAG, entry);
                startActivityForResult(intent, NEW_ENTRY_REQUEST);
            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                JournalEntry entry = createJournalEntry();
                Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                entry.setImageUri(uri);
                Intent sharePictureIntent = new Intent(context, JournalDetails.class);
                sharePictureIntent.putExtra(JournalEntry.TAG, entry);
                startActivityForResult(sharePictureIntent, NEW_ENTRY_REQUEST);
            }
        }


        journalEntryListAdapter = new JournalEntryListAdapter(journalEntries);
        entryRecyclerView.setAdapter(journalEntryListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        entryRecyclerView.setLayoutManager(linearLayoutManager);

        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(context, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        String entryText = processResponse();
        if(entryText != null){
            JournalEntry entry = new JournalEntry(JournalEntry.INVALID_ID, entryText);
            repository.createEntry(entry);
        }

    }

    private void showNotification(){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Journal Notification";
            String description = "Notification for journal entry";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel notificationChannel = new NotificationChannel(channelId, name, importance);
            notificationChannel.setDescription(description);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        android.support.v4.app.RemoteInput remoteInput = new RemoteInput.Builder(NEW_ENTRY_ACTION)
                .setLabel("Enter your entry text")
                .build();

        Intent inputIntent = new Intent(context, JournalListActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity
                (
                        this,
                        INPUT_INTENT_REQUEST_CODE,
                        inputIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Action inputAction = new NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_edit, "Entry", resultPendingIntent)
                .addRemoteInput(remoteInput)
                .setAllowGeneratedReplies(true)
                .build();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setContentTitle("Journal Entry")
                .setContentText("Create a journal entry")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .addAction(inputAction)
                .setColor(getResources().getColor(R.color.colorAccentGrey));

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private String processResponse(){
        Bundle input = RemoteInput.getResultsFromIntent(getIntent());
        if(input != null){
            String entryText = input.getCharSequence(NEW_ENTRY_ACTION).toString();

            NotificationCompat.Builder successNotification = new NotificationCompat.Builder(
                    context, channelId)
                    .setSmallIcon(android.R.drawable.ic_menu_save)
                    .setContentText("New Entry Created");

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(NOTIFICATION_ID, successNotification.build());

            return entryText;
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getLocalClassName(), "onResume");

        journalEntries.clear();
        journalEntries.addAll(repository.readAllEntries());
        journalEntryListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(getLocalClassName(), "onStart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(getLocalClassName(), "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(getLocalClassName(), "onStop");
        showNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(getLocalClassName(), "onDestroy");
    }

    private TextView generateTextView(final JournalEntry entry){
        TextView textView = new TextView(context);
        textView.setTextSize(25);
        textView.setTextColor(Color.BLACK);
        textView.setText(String.format("Date: %s Rating: %s", entry.getDate(), entry.getRating()));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(context, JournalDetails.class);
                detailIntent.putExtra(JournalEntry.TAG, entry);
                startActivityForResult(detailIntent, EDIT_ENTRY_REQUEST_CODE);
            }
        });
        return textView;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == EDIT_ENTRY_REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                // TODO: when delete is added, id will no longer work as an index
                JournalEntry entry = (JournalEntry) data.getSerializableExtra(JournalEntry.TAG);
                journalEntries.set(entry.getId(), entry);
                journalEntryListAdapter.notifyItemChanged(entry.getId());
                repository.updateEntry(entry);
            }
        }else if(requestCode == NEW_ENTRY_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                JournalEntry entry = (JournalEntry) data.getSerializableExtra(JournalEntry.TAG);
                journalEntries.add(entry);
                journalEntryListAdapter.notifyItemChanged(journalEntries.size() - 1);
                repository.createEntry(entry);
            }
        }
    }

    private JournalEntry createJournalEntry() {
        JournalEntry entry = new JournalEntry(JournalEntry.INVALID_ID);
        /*DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date();
        entry.setDate(dateFormat.format(date));*/
        return entry;
    }

    private JournalEntry createJournalEntry(String text) {
        JournalEntry entry = createJournalEntry();
        entry.setEntryText(text);
        return entry;
    }

    private void addTestEntries() {
        journalEntries.add(createJournalEntry("Gathered by gravity how far away finite but unbounded the only home we've ever known network of wormholes Jean-François Champollion? Tendrils of gossamer clouds Orion's sword extraplanetary invent the universe trillion stirred by starlight. Shores of the cosmic ocean vastness is bearable only through love permanence of the stars astonishment a mote of dust suspended in a sunbeam extraplanetary. Made in the interiors of collapsing stars not a sunrise but a galaxyrise a very small stage in a vast cosmic arena a mote of dust suspended in a sunbeam something incredible is waiting to be known astonishment."));
        journalEntries.add(createJournalEntry("Vangelis muse about Hypatia explorations hundreds of thousands another world. Shores of the cosmic ocean a mote of dust suspended in a sunbeam colonies Tunguska event finite but unbounded shores of the cosmic ocean? Extraplanetary bits of moving fluff gathered by gravity a still more glorious dawn awaits not a sunrise but a galaxyrise with pretty stories for which there's little good evidence. Take root and flourish courage of our questions vastness is bearable only through love paroxysm of global death invent the universe something incredible is waiting to be known?"));
        journalEntries.add(createJournalEntry("Preserve and cherish that pale blue dot two ghostly white figures in coveralls and helmets are soflty dancing vastness is bearable only through love Euclid permanence of the stars inconspicuous motes of rock and gas. Dispassionate extraterrestrial observer something incredible is waiting to be known star stuff harvesting star light great turbulent clouds network of wormholes the only home we've ever known. Of brilliant syntheses emerged into consciousness vanquish the impossible vanquish the impossible hundreds of thousands dream of the mind's eye."));
        journalEntries.add(createJournalEntry("Extraplanetary Euclid Hypatia brain is the seed of intelligence intelligent beings Rig Veda. Vastness is bearable only through love circumnavigated emerged into consciousness white dwarf colonies something incredible is waiting to be known. Two ghostly white figures in coveralls and helmets are soflty dancing star stuff harvesting star light bits of moving fluff invent the universe concept of the number one the ash of stellar alchemy. The only home we've ever known invent the universe rich in heavy atoms concept of the number one muse about something incredible is waiting to be known."));
        journalEntries.add(createJournalEntry("Science dream of the mind's eye stirred by starlight Jean-François Champollion with pretty stories for which there's little good evidence circumnavigated? Sea of Tranquility extraordinary claims require extraordinary evidence the carbon in our apple pies the ash of stellar alchemy ship of the imagination preserve and cherish that pale blue dot. Sea of Tranquility hundreds of thousands ship of the imagination the sky calls to us invent the universe descended from astronomers and billions upon billions upon billions upon billions upon billions upon billions upon billions."));
    }

}
