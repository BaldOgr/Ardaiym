package com.turlygazhy.entity;

import patterns.KeyboardPattern;

/**
 * Created by Eshu on 11.07.2017.
 */
public class Voting {
    private long            id;
    private String          voteText;
    private String          photo;
    private String[]        voteSelections;
    private String[]        votesInSelections;
    private KeyboardPattern keyboardPattern;
    private long            voteCreatorId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVoteText() {
        return voteText;
    }

    public void setVoteText(String voteText) {
        this.voteText = voteText;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String[] getVoteSelections() {
        return voteSelections;
    }

    public void setVoteSelections(String[] voteSelections) {
        this.voteSelections = voteSelections;
    }

    public String[] getVotesInSelections() {
        return votesInSelections;
    }

    public void setVotesInSelections(String[] votesInSelections) {
        this.votesInSelections = votesInSelections;
    }

    public KeyboardPattern getKeyboardPattern() {
        return keyboardPattern;
    }

    public void setKeyboardPattern(KeyboardPattern keyboardPattern) {
        this.keyboardPattern = keyboardPattern;
    }

    public Voting(long id, String voteText, String photo, String[] voteSelections, String[] votesInSelections, KeyboardPattern keyboardPattern, long voteCreatorId) {
        this.id                = id;
        this.voteText          = voteText;
        this.photo             = photo;
        this.voteSelections    = voteSelections;
        this.votesInSelections = votesInSelections;
        this.keyboardPattern   = keyboardPattern;
        this.voteCreatorId     = voteCreatorId;
    }

    public long getVoteCreatorId() {
        return voteCreatorId;
    }

    public void setVoteCreatorId(long voteCreatorId) {
        this.voteCreatorId = voteCreatorId;
    }
}