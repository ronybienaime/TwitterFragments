package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.database.DraftTweetDAO;
import com.codepath.apps.restclienttemplate.databinding.NewPostTweetBinding;
import com.codepath.apps.restclienttemplate.fragments.DraftFragment;
import com.codepath.apps.restclienttemplate.models.DraftTweet;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by anushree on 9/28/2017.
 */

public class NewTweetFragment extends DialogFragment implements DraftFragment.DraftSelectedListener {

    Context ctx;
    DraftTweetDAO dao;
    FragmentManager fm;
    ImageView delete;
    ImageView draft ;
    EditText edit_tweet;
    private NewPostTweetBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ctx = context;
    }

    @Override
    public void draftSelected(String draftValue) {
        //Log.i("NEwTweet Frgment","DraftValue "+draftValue);
        edit_tweet.setText(draftValue);
    }

    public interface postTweetListener{
        void postTweet(String status);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        final Button TweetBtn;
        ImageView profie_image;
        final TextView watcher;

        binding = DataBindingUtil.inflate(inflater, R.layout.new_post_tweet,container,false);
        View view = binding.getRoot();
        Bundle bun = getArguments();
        draft = binding.draft;
        edit_tweet = binding.editTweet;
        delete = binding.delete;
        TweetBtn = binding.tweetBtn;
        profie_image = binding.profieImage;
        watcher = binding.watcher;
        watcher.setText(String.valueOf(140-edit_tweet.getText().length()));


        dao = new DraftTweetDAO(ctx);
        fm = getChildFragmentManager();
        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                watcher.setText(String.valueOf(140-s.length()));
                if(s.length()>140){
                    TweetBtn.setEnabled(false);
                }
                else if(s.length()==0){
                    TweetBtn.setEnabled(false);
                }
                else{
                    TweetBtn.setEnabled(true);
                }
            }


            public void afterTextChanged(Editable s) {
            }
        };

        edit_tweet.addTextChangedListener(mTextEditorWatcher);

        Glide.with(ctx).load(bun.getString("my_profile_image")).into(profie_image);

        TweetBtn.setEnabled(false);
        TweetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postTweetListener listener = (postTweetListener) getActivity();
                listener.postTweet(edit_tweet.getText().toString());
                dismiss();
            }
        });



        getDraftTask task = new getDraftTask();
        task.execute();

        draft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DraftFragment frag = new DraftFragment();
                frag.show(fm,"draft");
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_tweet.getText().toString().isEmpty())
                    dismiss();
                else{
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                    dialog.setTitle("Save as Draft?");
                    dialog.setMessage("Do you want to save this tweet as a draft?");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            AddDraftTask task = new AddDraftTask();
                            DraftTweet dt = new DraftTweet(edit_tweet.getText().toString());
                            task.execute(dt);
                            dismiss();
                        }
                    });

                    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                dismiss();
                        }
                    });

                    dialog.show();


                }
            }
        });



        if(bun.getString("intent_title")!=null&& bun.getString("intent_url")!=null ){
            edit_tweet.setText(bun.getString("intent_title")+"\n"+bun.getString("intent_url"));
        }

        return view;
    }



    public class AddDraftTask extends AsyncTask<DraftTweet,Void,Boolean> {


        @Override
        protected Boolean doInBackground(DraftTweet... params) {

            return dao.addDraft(params[0]);
        }


        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                dismiss();
            }

        }
    }


    private class getDraftTask extends AsyncTask<Void,Void,ArrayList<DraftTweet>>{

        @Override
        protected ArrayList<DraftTweet> doInBackground(Void... voids) {
            return dao.getDrafts();
        }

        @Override
        protected void onPostExecute(ArrayList<DraftTweet> draftTweets) {
            if(draftTweets.size()==0)
                draft.setVisibility(View.GONE);

            else {
                draft.setVisibility(View.VISIBLE);
                draft.setEnabled(true);
            }
        }
    }
}
