package com.example.safakesberk.todoapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseUser;

/**
 * Created by Safak Esberk on 11.3.2015.
 */
public class Settings extends Fragment {

    Button logout;
    public static Settings newInstance() {
        Settings fragmentSecond = new Settings();

        return fragmentSecond;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);
        ParseUser currentUser = ParseUser.getCurrentUser();
        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();

        TextView txtuser = (TextView) view.findViewById(R.id.txtuser);

        txtuser.setText(getString(R.string.loggedAs) + struser);

        logout = (Button) view.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                getActivity().finish();
            }
        });
        return view;
    }
}
