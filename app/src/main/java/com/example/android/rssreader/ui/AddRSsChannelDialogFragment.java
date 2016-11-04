package com.example.android.rssreader.ui;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.rssreader.R;

public class AddRSSChannelDialogFragment extends DialogFragment {
    public static final String LOG_TAG=AddRSSChannelDialogFragment.class.getSimpleName();

    // Use this instance of the interface to deliver action events
    AddChannelDialogListener mListener;
    private EditText channelLinkEt;

    public AddRSSChannelDialogFragment(){}



    public static AddRSSChannelDialogFragment newInstance(String title) {
        AddRSSChannelDialogFragment frag = new AddRSSChannelDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout =inflater.inflate(R.layout.fragment_add_rss_channel,null);

        channelLinkEt= (EditText) layout.findViewById(R.id.fragment_add_channel_link_et);

        alertDialogBuilder.setView(layout);

        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Are you sure?");
        alertDialogBuilder.setPositiveButton("Add",null);
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNeutralButton("Help", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(AddRSSChannelDialogFragment.this);

                // in this case, we want to show the help text, but
                // come back to the previous dialog when we're done
                ft.addToBackStack(null);
                //null represents no name for the back stack transaction

                HelpDialogFragment hdf =
                        HelpDialogFragment.newInstance(R.string.help_channel_link);
                // FIXME no magic strings
                hdf.show(ft, "HELP_DIALOG");
            }
        });

        return alertDialogBuilder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the AddChannelDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the AddChannelDialogListener so we can send events to the host
            mListener = (AddChannelDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AddChannelDialogListener");
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...
                    // on success
                    String link = channelLinkEt.getText().toString();
                    // check if is valid url
                    if(!Patterns.WEB_URL.matcher(link).matches()){
                        // TODO extract to resources for localization
                        channelLinkEt.setError("Feed link is not valid!");
                        return;
                    }else{
                        // ok
                        mListener.onDialogPositiveClick(link);
                        wantToCloseDialog=true;
                    }
                    if(wantToCloseDialog)
                        d.dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }

    }


    public interface AddChannelDialogListener {

        /* The activity that creates an instance of this dialog fragment must
         * implement this interface in order to receive event callbacks.
         * Each method passes the DialogFragment in case the host needs to query it. */
        public void onDialogPositiveClick(String url);
        public void onDialogNegativeClick(android.app.DialogFragment dialog);
    }

}
