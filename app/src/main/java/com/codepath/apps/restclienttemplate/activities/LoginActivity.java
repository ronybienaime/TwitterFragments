package com.codepath.apps.restclienttemplate.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.codepath.apps.restclienttemplate.R;
import com.codepath.apps.restclienttemplate.databinding.ActivityLoginBinding;
import com.codepath.apps.restclienttemplate.fragments.NavHeaderFragment;
import com.codepath.apps.restclienttemplate.models.User;
import com.codepath.apps.restclienttemplate.utils.TwitterApplication;
import com.codepath.apps.restclienttemplate.utils.TwitterClient;
import com.codepath.oauth.OAuthLoginActionBarActivity;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends OAuthLoginActionBarActivity<TwitterClient>{

    public static final String TAG = "LoginActivity";
    Button btn;
    ActivityLoginBinding binding;
    User CurrentUser;
    private TwitterClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        btn = binding.btn;

	}


	// Inflate the menu; this adds items to the action bar if it is present.
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	// OAuth authenticated successfully, launch primary authenticated activity
	// i.e Display application "homepage"
	@Override
	public void onLoginSuccess() {
        btn.setVisibility(View.GONE);
		new LongOperation().execute("");

	}

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            //some heavy processing resulting in a Data String
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }
            return "whatever result you have";
        }

        @Override
        protected void onPostExecute(String result) {
            client = TwitterApplication.getRestClient();
            if(CurrentUser==null) {
                getUserInformation();
            }
            else{
                Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
                if (CurrentUser != null)
                    i.putExtra("Profile_url", CurrentUser.getProfile_imageURL());
                startActivity(i);
            }


        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

	// OAuth authentication flow failed, handle the error
	// i.e Display an error dialog or toast
	@Override
	public void onLoginFailure(Exception e) {
		e.printStackTrace();
	}

	// Click handler method for the button used to start OAuth flow
	// Uses the client to initiate OAuth authorization
	// This should be tied to a button used to login
	public void loginToRest(View view) {
		getClient().connect();
	}


	public void getUserInformation(){
        if(!isOnline()) {
            Log.i(TAG, "Internet is not there ");
        }
        else {
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.i(TAG, response.toString());

                    try {
                        CurrentUser = User.fromJSON(response);
                        Intent i = new Intent(LoginActivity.this, TimelineActivity.class);
                        if (CurrentUser != null)
                            i.putExtra("Profile_url", CurrentUser.getProfile_imageURL());
                        startActivity(i);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            });
        }
    }


    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

}
