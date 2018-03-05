package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
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
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class ReplyFragment extends DialogFragment {

    public static final String TAG = "ReplyFragment";
    Context ctx;
    FragmentManager fm;
    ImageView delete_reply;
    CircleImageView profie_image_reply ;
    TextView replyingto;
    TextView counter;

    EditText edit_reply_tweet;
    Button reply_tweet;
    Tweet tweet;
    DraftTweetDAO dao;
    private FragmentReplyBinding binding;
    private TwitterClient client;


    @Override
    public void onAttach(Context  context) {
        super.onAttach(context);
        ctx = context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
    }

    public interface ReplyTweetListener{
        public void onReplyTweet();
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

        Glide.with(ctx).load(NavHeaderFragment.getCurrentUser().getProfile_imageURL()).into(profie_image_reply);

        String Name = getColoredSpanned("@"+tweet.getUser().getScreen_name(),"#1dcaff");


        replyingto.setText("Replying to "+Html.fromHtml(Name));
        edit_reply_tweet.setText("@"+tweet.getUser().getScreen_name());

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
        edit_reply_tweet.setSelection(edit_reply_tweet.getText().length());

        reply_tweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReplyTweet(edit_reply_tweet.getText().toString());

            }
        });



        return view ;
    }

    private void ReplyTweet(String reply) {
        client.replyTweet(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Log.i(TAG,"onReplyTweet : Onsuccess");
                    Tweet tweetreply = Tweet.fromJSON(response);
                    Log.i(TAG,"onReplyTweet : tweet = "+tweetreply.getBody());
                    ReplyTweetListener listener = (ReplyTweetListener) (getActivity());
                    listener.onReplyTweet();
                    dismiss();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        },reply,tweet.gettweet_id());
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



    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }



}
