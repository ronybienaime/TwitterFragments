package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.database.DraftTweetDAO;
import com.codepath.apps.restclienttemplate.databinding.FragmentReplyBinding;
import com.codepath.apps.restclienttemplate.models.DraftTweet;
import com.codepath.apps.restclienttemplate.models.Tweet;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * A simple {@link Fragment} subclass.
 * Reply fragment
 */
public class ReplyFragment extends DialogFragment {

    Context ctx;
    FragmentManager fm;
    ImageView delete_reply;
    ImageView profie_image_reply ;
    TextView replyingto;
    TextView counter;

    EditText edit_reply_tweet;
    Button reply_tweet;
    Tweet tweet;
    DraftTweetDAO dao;
    private FragmentReplyBinding binding;


    @Override
    public void onAttach(Context  context) {
        super.onAttach(context);
        ctx = context;

    }

    public interface ReplyTweetListener{
        public void onReplyTweet(String reply);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_reply, container, false);
        View view = binding.getRoot();
        tweet = getArguments().getParcelable("Tweet");



        delete_reply = binding.deleteReply;
        profie_image_reply = binding.profieImageReply;
        replyingto = binding.replyingto;
        edit_reply_tweet = binding.editReplyTweet;
        reply_tweet = binding.replyTweet;
        counter = binding.counter;

        Glide.with(ctx).load(tweet.getUser().getProfile_imageURL()).bitmapTransform(new RoundedCornersTransformation(ctx,4,1, RoundedCornersTransformation.CornerType.ALL)).into(profie_image_reply);

        replyingto.setText("Replying to @"+tweet.getUser().getScreen_name());

        dao = new DraftTweetDAO(ctx);
        delete_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edit_reply_tweet.getText().toString().isEmpty())
                    dismiss();
                else{
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                    dialog.setTitle("Save as Draft?");
                    dialog.setMessage("Do you want to save this tweet as a draft?");
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            AddDraftTask task = new AddDraftTask();
                            DraftTweet dt = new DraftTweet(edit_reply_tweet.getText().toString());
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


        reply_tweet.setEnabled(false);
        TextWatcher mTextEditorWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //This sets a textview to the current length
                counter.setText(String.valueOf(140-s.length()));
                if(s.length()>140){
                    reply_tweet.setEnabled(false);
                }
                else if(s.length()==0){
                    reply_tweet.setEnabled(false);
                }
                else{
                    reply_tweet.setEnabled(true);
                }
            }


            public void afterTextChanged(Editable s) {
            }
        };

        edit_reply_tweet.addTextChangedListener(mTextEditorWatcher);


        reply_tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReplyTweetListener listener = (ReplyTweetListener) getActivity();
                listener.onReplyTweet(edit_reply_tweet.getText().toString());
                dismiss();
            }
        });



        return view ;
    }

    private class AddDraftTask extends AsyncTask<DraftTweet,Void,Boolean> {


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





}
