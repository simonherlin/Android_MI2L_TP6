package com.example.simon.contentprovider01herlin;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

    private static final int CODE = 123;

    private EditText searchEditText;
    private ListView listView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.contactsList);
        searchEditText = (EditText) findViewById(R.id.searchContactEditText);
        linearLayout = (LinearLayout) findViewById(R.id.addContactLayout);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                displayContacts(searchEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void displayContacts(final String pattern) {
        ContentResolver contentResolver = getContentResolver();

        Cursor c = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?",
                new String[]{"%" + pattern.toLowerCase() + "%"},
                ContactsContract.Contacts.DISPLAY_NAME + " ASC"
        );

        if (c != null && c.getCount() == 0) {
            listView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            SimpleCursorAdapter ca = new SimpleCursorAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    c,
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                    new int[]{android.R.id.text1},
                    0
            );

            linearLayout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(ca);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        searchEditText.setText("");
        displayContacts("");
    }

    public void createContact(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, searchEditText.getText().toString());
        startActivityForResult(intent, CODE);
    }
}

