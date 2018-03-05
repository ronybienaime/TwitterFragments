package com.codepath.apps.restclienttemplate.models;



import android.os.Parcel;
import android.os.Parcelable;

import com.codepath.apps.restclienttemplate.database.MyDatabase;
import com.codepath.apps.restclienttemplate.utils.ParseRelativeDate;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@Table(database = MyDatabase.class)
@org.parceler.Parcel(analyze={DirectMessage.class})
public class DirectMessage extends BaseModel implements Parcelable{

     @Column
     @PrimaryKey
     long directMsgId;

    @Column
    String created_at;

    @Column
    long recipient_id;

    @Column
    String recipient_screen_name;

    @Column
    String recipient_name;

    @Column
    String text;

    @Column
    String recipient_profile_url;


    protected DirectMessage(Parcel in) {
        directMsgId = in.readLong();
        created_at = in.readString();
        recipient_id = in.readLong();
        recipient_screen_name = in.readString();
        recipient_name = in.readString();
        text = in.readString();
        recipient_profile_url = in.readString();
    }

    public static final Creator<DirectMessage> CREATOR = new Creator<DirectMessage>() {
        @Override
        public DirectMessage createFromParcel(Parcel in) {
            return new DirectMessage(in);
        }

        @Override
        public DirectMessage[] newArray(int size) {
            return new DirectMessage[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeLong(directMsgId);
        parcel.writeString(created_at);
        parcel.writeLong(recipient_id);
        parcel.writeString(recipient_screen_name);
        parcel.writeString(recipient_name);
        parcel.writeString(text);
        parcel.writeString(recipient_profile_url);
    }

    public DirectMessage() {

    }

    public static ArrayList<DirectMessage> fromJSON(JSONArray response) throws JSONException {
        ArrayList<DirectMessage> messageList = new ArrayList<>();
        for(int i=0;i<response.length();i++) {
            DirectMessage message = new DirectMessage();
            JSONObject object = response.getJSONObject(i);
            message.directMsgId = object.getLong("id");
            message.text = object.getString("text");
            message.created_at = ParseRelativeDate.getRelativeTimeAgo(object.getString("created_at"));
            JSONObject recipient = object.getJSONObject("recipient");
            message.recipient_id = recipient.getLong("id");
            message.recipient_name = recipient.getString("name");
            message.recipient_screen_name = recipient.getString("screen_name");
            message.recipient_profile_url = recipient.getString("profile_image_url");
            messageList.add(message);

        }
        return messageList;
    }


    public static DirectMessage fromJSON(JSONObject response) throws JSONException {
            DirectMessage message = new DirectMessage();
            message.directMsgId = response.getLong("id");
            message.text = response.getString("text");
            message.created_at = ParseRelativeDate.getRelativeTimeAgo(response.getString("created_at"));
            JSONObject recipient = response.getJSONObject("recipient");
            message.recipient_id = recipient.getLong("id");
            message.recipient_name = recipient.getString("name");
            message.recipient_screen_name = recipient.getString("screen_name");
            message.recipient_profile_url = recipient.getString("profile_image_url");
        return message;
    }


    public long getDirectMsgId() {
        return directMsgId;
    }

    public void setDirectMsgId(long directMsgId) {
        this.directMsgId = directMsgId;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public long getRecipient_id() {
        return recipient_id;
    }

    public void setRecipient_id(long recipient_id) {
        this.recipient_id = recipient_id;
    }

    public String getRecipient_screen_name() {
        return recipient_screen_name;
    }

    public void setRecipient_screen_name(String recipient_screen_name) {
        this.recipient_screen_name = recipient_screen_name;
    }

    public String getRecipient_name() {
        return recipient_name;
    }

    public void setRecipient_name(String recipient_name) {
        this.recipient_name = recipient_name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRecipient_profile_url() {
        return recipient_profile_url;
    }

    public void setRecipient_profile_url(String recipient_profile_url) {
        this.recipient_profile_url = recipient_profile_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
