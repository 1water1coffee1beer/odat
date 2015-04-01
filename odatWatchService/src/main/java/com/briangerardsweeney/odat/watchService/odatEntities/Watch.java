package com.briangerardsweeney.odat.watchService.odatEntities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 3/23/2015.
 */
@Entity
public class Watch {

    @Id
    String searchTerm;

    @Index
    private List<String> ignoreTerms;

    private boolean steapAndCheap;

    private boolean chainLove;

    private boolean whiskeyMilitia;

    public Watch(){
        this.ignoreTerms = new ArrayList<String>();
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public List<String> getIgnoreTerms() {
        return ignoreTerms;
    }

    public void setIgnoreTerms(List<String> ignoreTerms) {
        this.ignoreTerms = ignoreTerms;
    }

    public boolean isSteapAndCheap() {
        return steapAndCheap;
    }

    public void setSteapAndCheap(boolean steapAndCheap) {
        this.steapAndCheap = steapAndCheap;
    }

    public boolean isChainLove() {
        return chainLove;
    }

    public void setChainLove(boolean chainLove) {
        this.chainLove = chainLove;
    }

    public boolean isWhiskeyMilitia() {
        return whiskeyMilitia;
    }

    public void setWhiskeyMilitia(boolean whiskeyMilitia) {
        this.whiskeyMilitia = whiskeyMilitia;
    }
}