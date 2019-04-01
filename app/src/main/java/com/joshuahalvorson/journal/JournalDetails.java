package com.joshuahalvorson.journal;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JournalDetails extends AppCompatActivity {

    public static final int IMAGE_REQUEST_CODE = 1;

    private EditText journalEntry;
    private ImageView journalImage;
    private TextView journalDate, shareEntryButton;
    private Button addImageButton;
    private SeekBar journalDayRating;

    private JournalEntry entry;
    private String uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getLocalClassName(), "onCreate");
        setContentView(R.layout.activity_journal_details);

        entry = (JournalEntry) getIntent().getSerializableExtra(JournalEntry.TAG);

        if(entry == null) {
            entry = new JournalEntry(JournalEntry.INVALID_ID);
        }

        journalImage = findViewById(R.id.journal_image_view);
        journalEntry = findViewById(R.id.journal_entry_edit_text);
        journalDate = findViewById(R.id.journal_date_text);
        addImageButton = findViewById(R.id.add_image_button);
        shareEntryButton = findViewById(R.id.share_entry_button);
        journalDayRating = findViewById(R.id.journal_day_rating);

        if(entry != null){
            if(!entry.getDate().equals("")){
                journalDate.setText(String.format("Date: %s", entry.getDate()));
            }else{
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                Date date = new Date();
                entry.setDate(dateFormat.format(date));
                journalDate.setText(String.format("Date: %s", entry.getDate()));
            }
            journalEntry.setText(entry.getEntryText());
            journalDayRating.setProgress(entry.getRating());
            journalImage.setImageURI(entry.getImageUri());
            journalImage.setVisibility(View.VISIBLE);
        }

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getImageIntent.setType("image/*");
                startActivityForResult(getImageIntent, IMAGE_REQUEST_CODE);
            }
        });

        shareEntryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, journalEntry.getText().toString());
                startActivity(Intent.createChooser(sharingIntent, "Share Using"));
            }
        });

        journalDayRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Toast.makeText(
                        getApplicationContext(),
                        "Day Rating: " + progress,
                        Toast.LENGTH_LONG
                ).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        JournalEntry newEntry = new JournalEntry(
                entry.getDate(),
                journalEntry.getText().toString(),
                entry.getImageUri().toString(),
                journalDayRating.getProgress(),
                entry.getId()
        );
        resultIntent.putExtra(JournalEntry.TAG, newEntry);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                Uri imageUri = data.getData();
                entry.setImageUri(imageUri);
                journalImage.setImageURI(imageUri);
                journalImage.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(getLocalClassName(), "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(getLocalClassName(), "onResume");
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(getLocalClassName(), "onDestroy");
    }


}
