package com.codepath.apps.restclienttemplate.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


@Table(database = MyDatabase.class)
@org.parceler.Parcel(analyze={Friends.class})
public class Friends extends BaseModel implements Parcelable{

    @Column
    @PrimaryKey
    long friendId;


    @Column
    String name;

    @Column
    String screen_name;

    @Column
    String profile_imageURL;

    @Column
    String description;

    @Column
    boolean following;

    protected Friends() {

    }

    protected Friends(Parcel in) {
        friendId = in.readLong();
        name = in.readString();
        screen_name = in.readString();
        profile_imageURL = in.readString();
        description = in.readString();
        following = in.readByte() != 0;
    }


    public static ArrayList<Friends> fromJSON(JSONArray response) throws JSONException {
        ArrayList<Friends> friendlist = new ArrayList<>();
        for(int i=0;i<response.length();i++) {
            Friends friend = new Friends();
            JSONObject object = response.getJSONObject(i);
            friend.name = object.getString("name");
            friend.profile_imageURL = object.getString("profile_image_url");
            friend.screen_name = object.getString("screen_name");
            friend.description = object.getString("description");
            friend.friendId = object.getLong("id");
            friend.following = object.getBoolean("following");
            friendlist.add(friend);

        }
        return friendlist;
    }

    public static final Creator<Friends> CREATOR = new Creator<Friends>() {
        @Override
        public Friends createFromParcel(Parcel in) {
            return new Friends(in);
        }

        @Override
        public Friends[] newArray(int size) {
            return new Friends[size];
        }
    };

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getProfile_imageURL() {
        return profile_imageURL;
    }

    public void setProfile_imageURL(String profile_imageURL) {
        this.profile_imageURL = profile_imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(friendId);
        parcel.writeString(name);
        parcel.writeString(screen_name);
        parcel.writeString(profile_imageURL);
        parcel.writeString(description);
        parcel.writeByte((byte) (following ? 1 : 0));
    }
}
