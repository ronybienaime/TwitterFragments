package com.codepath.apps.restclienttemplate.fragments;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.database.DraftTweetDAO;
import com.codepath.apps.restclienttemplate.databinding.DraftListItemBinding;
import com.codepath.apps.restclienttemplate.models.DraftTweet;

import java.util.ArrayList;

/**
 * Created by anushree on 9/29/2017.
 */

public class DraftFragment extends DialogFragment {



    public interface DraftSelectedListener{
        public void draftSelected(String draftValue);
    }
    ArrayList<DraftTweet> draftlist;
    DraftTweetDAO dao;

    ImageView draftClose;

    ArrayAdapter<DraftTweet> draftadp;
    ListView draftlv;
    Context ctx;
    DraftListItemBinding binding;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ctx = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {



        binding = DataBindingUtil.inflate(inflater,R.layout.draft_list_item,container,false);
        View view = binding.getRoot();
        draftlist = new ArrayList<>();
        draftlv = binding.draftList;
        draftClose = binding.draftClose;

        dao = new DraftTweetDAO(ctx);

        draftadp = new ArrayAdapter<DraftTweet>(ctx,android.R.layout.simple_list_item_1,draftlist);
        draftlv.setAdapter(draftadp);
        getDraftTask task  = new getDraftTask();
        task.execute();
        Log.i("DraftFragment","draftTweets "+draftlist.size());

        draftlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DraftSelectedListener listener = (DraftSelectedListener) getParentFragment();
                listener.draftSelected(draftlist.get(i).getDraft());
                deleteDraft task = new deleteDraft();
                task.execute(draftlist.get(i));
                dismiss();
            }
        });

        draftClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;

    }


    class getDraftTask extends AsyncTask<Void,Void,ArrayList<DraftTweet>>{

        @Override
        protected ArrayList<DraftTweet> doInBackground(Void... voids) {
            return dao.getDrafts();
        }

        @Override
        protected void onPostExecute(ArrayList<DraftTweet> draftTweets) {
            Log.i("DraftFragment","draftTweets "+draftTweets.size());
            draftlist.addAll(draftTweets);
            draftadp.notifyDataSetChanged();
        }
    }


    class deleteDraft extends AsyncTask<DraftTweet,Void,Boolean>{



        @Override
        protected Boolean doInBackground(DraftTweet... draftTweets) {
            return dao.deleteDraft(draftTweets[0]);
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.i("DraftFragment","Draft deleted");
        }
    }
}
