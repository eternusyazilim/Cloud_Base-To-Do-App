package com.example.safakesberk.todoapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Safak Esberk on 11.3.2015.
 */
public class MyNotes extends Fragment {
    ParseObject note ;
    List<ParseObject> noteList;
    ListView listview;
    ArrayAdapter<String> adapter;
    ProgressDialog dialog;
    ParseUser currentUser;

    public static MyNotes newInstance() {
        MyNotes fragmentFirst = new MyNotes();

        return fragmentFirst;
    }
    public void addNote() {
        final EditText input = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.enter))
                .setMessage(getString(R.string.saveList))
                .setView(input)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                       createNewNote(input);

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Crouton.makeText(getActivity(), getString(R.string.cancelled), Style.ALERT).show();
            }
        }).show();
    }

    private void createNewNote(EditText input) {
        Editable value = input.getText();
        adapter.insert(value.toString(), 0);
        note = new ParseObject("ToDoApp");
        note.put("userID",currentUser.get("userID"));
        note.put("note", value.toString());
        note.saveEventually();
        Crouton.makeText(getActivity(), getString(R.string.saved), Style.CONFIRM).show();
    }

    private class LoadData extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog =new ProgressDialog(getActivity());
            dialog.setTitle(getString(R.string.notesLoad));
            dialog.setMessage(getString(R.string.load));
            dialog.setIndeterminate(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ToDoApp");
            query.orderByDescending("_created_at");
            query.whereEqualTo("userID",currentUser.get("userID"));
            try {
                noteList = query.find();
            } catch (ParseException e) {
                Crouton.makeText(getActivity(), getString(R.string.wentWrong) + e.getMessage(), Style.ALERT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listview= (ListView) getActivity().findViewById(R.id.list);
            adapter = new ArrayAdapter<String>(getActivity(),R.layout.listview_item);
            for (ParseObject note : noteList) {
                adapter.add((String) note.get("note"));
            }
            listview.setAdapter(adapter);
            dialog.dismiss();
            registerForContextMenu(listview);

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser= ParseUser.getCurrentUser();
        new LoadData().execute();
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        String[] menuItems = getResources().getStringArray(R.array.menu);
        if (v.getId()==R.id.list) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle("Note");

            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }

    }
    private void updateView(int index, String text){
        View v = listview.getChildAt(index -listview.getFirstVisiblePosition());
        if(v == null)
            return;

        TextView someText = (TextView) v.findViewById(R.id.text);
        someText.setText(text);
        Crouton.makeText(getActivity(),getString(R.string.updated), Style.CONFIRM).show();
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        String[] menuItems = getResources().getStringArray(R.array.menu);
        if((menuItems[0].equals(item.getTitle()))){  // UPDATE
            update(item);
        }
        else{ // DELETE
            initializeParseObject(item);
            noteList.get(0).deleteInBackground();
            Crouton.makeText(getActivity(),getString(R.string.Deleted), Style.CONFIRM).show();
            new LoadData().execute();
        }
        return true;
    }

    private void update(final MenuItem item) {
        final EditText input = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.update))
                .setMessage(getString(R.string.updateNote))
                .setView(input)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();

                        initializeParseObject(item);

                        noteList.get(0).put("note",value.toString());
                        noteList.get(0).saveInBackground();
                        updateView(0,value.toString());
                    }
                }).show();

    }

    private void initializeParseObject(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ToDoApp");
        query.whereEqualTo("userID",currentUser.get("userID"));
        query.whereEqualTo("note",adapter.getItem(info.position));
        try {
            noteList=query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notes_fragment, container, false);
        final Button button= (Button) view.findViewById(R.id.button);
        button.bringToFront();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });
        return view;
    }
}
