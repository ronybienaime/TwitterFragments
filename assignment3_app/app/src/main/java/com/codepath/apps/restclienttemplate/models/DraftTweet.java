package com.codepath.apps.restclienttemplate.models;

import java.io.Serializable;

/**
 * Created by anushree on 9/29/2017.
 */

public class DraftTweet implements Serializable {

    long _id;
    String draft;

    public DraftTweet(long _id, String draft) {
        this._id = _id;
        this.draft = draft;
    }

    public long get_id() {
        return _id;
    }

    public DraftTweet(String draft) {
        this.draft = draft;
    }

    public String getDraft() {
        return draft;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setDraft(String draft) {
        this.draft = draft;
    }

    @Override
    public String toString() {
        return getDraft().length()<=30 ? getDraft() : getDraft().substring(0,30);
    }
}
