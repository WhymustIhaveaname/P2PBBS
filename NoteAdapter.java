package com.example.administrator.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NoteAdapter extends ArrayAdapter<Note>{
    private int resourceId;
    public NoteAdapter(Context context, int textViewResourceId, List<Note> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Note note = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView notehash = view.findViewById(R.id.note_hash);
        TextView notenote = view.findViewById(R.id.note_note);
        notehash.setText(note.getHash());
        notenote.setText(note.getNote());
        return view;
    }
}
