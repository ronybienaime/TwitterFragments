package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.codepath.apps.restclienttemplate.activities.TimelineActivity;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Select;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anushree on 9/26/2017.
 */
@Table(database = MyDatabase.class)
@org.parceler.Parcel(analyze={Tweet.class})
public class Tweet extends BaseModel implements Parcelable
{
    @Column
    String created_at;

    @Column
    @PrimaryKey
    Long tweet_id;

    @Column
    String body;

    @Column
    @ForeignKey(saveForeignKeyModel = true)
    User user;



    @Column
    @ForeignKey(saveForeignKeyModel = true)
    Entities entities;


    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void settweet_id(Long uid) {
        this.tweet_id = uid;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setUser(User user) {
        this.user = user;
    }
    public Entities getEntities() {
        return entities;
    }

    public void setEntities(Entities entities) {
        this.entities = entities;
    }




    public Tweet() {
    }

    protected Tweet(Parcel in) {
        created_at = in.readString();
        tweet_id = in.readLong();
        body = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.entities = in.readParcelable(Entities.class.getClassLoader());
    }

    public static final Creator<Tweet> CREATOR = new Creator<Tweet>() {
        @Override
        public Tweet createFromParcel(Parcel in) {
            return new Tweet(in);
        }

        @Override
        public Tweet[] newArray(int size) {
            return new Tweet[size];
        }
    };

    public String getCreated_at() {
        return created_at;
    }

    public Long gettweet_id() {
        return tweet_id;
    }

    public String getBody() {
        return body;
    }

    public User getUser() {
        return user;
    }

    public static Tweet fromJSON(JSONObject object) throws JSONException {
        Tweet tweet = new Tweet();

       // Log.i(TimelineActivity.TAG,"Inside Tweet class "+object.getString("created_at"));
        tweet.created_at = ParseRelativeDate.getRelativeTimeAgo(object.getString("created_at"));
        tweet.tweet_id = object.getLong("id");
        tweet.body = object.getString("text");
        tweet.user =  User.fromJSON(object.getJSONObject("user"));
        if(object.has("extended_entities")) {
            JSONObject extended_entities = object.getJSONObject("extended_entities");
            Log.i("Tweet Model","Media exists "+tweet.user.getScreen_name());
            if (extended_entities != null) {
                tweet.entities = Entities.fromJSON(extended_entities, tweet.tweet_id);
            } else {
                tweet.entities = null;
            }
        }
        else{
            tweet.entities = null;
        }





        return tweet;
    }


    public static ArrayList<Tweet> getTweets(JSONArray array){
        ArrayList<Tweet> list = new ArrayList<>();

        for(int i=0;i<array.length();i++){
            try {
                Tweet tweet = Tweet.fromJSON(array.getJSONObject(i));
                list.add(tweet);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(created_at);
        parcel.writeLong(tweet_id);
        parcel.writeString(body);
        parcel.writeParcelable(user,i);
        parcel.writeParcelable(entities,i);
    }


   public static List<Tweet> loadRecentItemsfromDB(){
        return new Select().from(Tweet.class).orderBy(Tweet_Table.tweet_id,false).limit(100).queryList();
    }
}
