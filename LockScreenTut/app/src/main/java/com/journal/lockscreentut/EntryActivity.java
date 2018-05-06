package com.journal.lockscreentut;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.journal.database.HelperEntry;
import com.journal.database.model.Entry;
import com.journal.utils.MyDividerItemDecoration;
import com.journal.utils.Properties;
import com.journal.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class EntryActivity extends AppCompatActivity{
    private EntryAdapter adapter;
    private List<Entry> itemList = new ArrayList<>();
    private TextView noNotesView;

    private HelperEntry db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        noNotesView = findViewById(R.id.empty_notes_view);

        db = new HelperEntry(this);

        itemList.addAll(db.getAllEntries());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });

        adapter = new EntryAdapter(this, itemList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(
                new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(adapter);

        toggleEmptyNotes();

        /*
          On long press on RecyclerView item, open alert dialog
          with options to choose
          Edit and Delete
          */
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionsDialog(position);
            }
        }));
    }

    /**
     * Inserting new note in db
     * and refreshing the list
     */
    private void createNote(String note) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertEntry(note);

        // get the newly inserted note from db
        Entry n = db.getEntry(id);

        if (n != null) {
            // adding new note to array list at 0 position
            itemList.add(0, n);

            // refreshing the list
//            adapter.notifyDataSetChanged();
            adapter.notifyItemInserted(0);

            toggleEmptyNotes();
        }
    }

    /**
     * Create Entry
     * @param entry to be created
     */
    private void createEntry(Entry entry) {
        // inserting note in db and getting
        // newly inserted note id
        long id = db.insertEntry(entry);

        // get the newly inserted note from db
        Entry n = db.getEntry(id);

        if (n != null) {
            // adding new note to array list at 0 position
            itemList.add(0, n);

            // refreshing the list
            adapter.notifyDataSetChanged();

            toggleEmptyNotes();
        }
    }

    /**
     * Updating note in db and updating
     * item in the list by its position
     */
    private void updateEntry(String note, int position) {
        Entry n = itemList.get(position);

        // updating brief
        n.setTittle(note);
        // updating brief
        n.setBrief(note);

        // updating note in db
        db.updateEntry(n);

        // refreshing the list
        itemList.set(position, n);
        adapter.notifyItemChanged(position);

        toggleEmptyNotes();
    }

    /**
     * Deleting note from SQLite and removing the
     * item from the list by its position
     */
    private void deleteNote(int position) {
        // deleting the note from db
        db.deleteEntry(itemList.get(position));

        // removing the note from the list
        itemList.remove(position);
        adapter.notifyItemRemoved(position);

        toggleEmptyNotes();
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{Properties.K_EDIT, Properties.K_DELETE};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Properties.M_CHOOSE);
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, itemList.get(position), position);
                } else {
                    deleteNote(position);
                }
            }
        });
        builder.show();
    }


    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private void showNoteDialog(final boolean shouldUpdate, final Entry entry, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.entry_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput =
                new AlertDialog.Builder(EntryActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputBrief = view.findViewById(R.id.brief);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate
                ? getString(R.string.lbl_new_note_title)
                : getString(R.string.lbl_edit_note_title));

        if (shouldUpdate && entry != null) {
            inputBrief.setText(entry.getBrief());
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? Properties.K_UPDATE : Properties.K_SAVE,
                        new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton(Properties.K_CANCEL,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputBrief.getText().toString())) {
                    Toast.makeText(EntryActivity.this,
                            Properties.M_ENTER_NOTE, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && entry != null) {
                    // update note by it's id
                    updateEntry(inputBrief.getText().toString(), position);
                } else {
                    // create new entry
                    Entry en = new Entry(inputBrief.getText().toString(),
                            inputBrief.getText().toString());
                    createEntry(en);
//                    createNote(inputBrief.getText().toString());

                }
            }
        });
    }

    /**
     * Toggling list and empty notes view
     */
    private void toggleEmptyNotes() {
        // you can check itemList.size() > 0

        if (db.getEntriesCount() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }
}
