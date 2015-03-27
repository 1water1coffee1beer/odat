package com.briangerardsweeney.odat.watchService.odatEntities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian on 3/22/2015.
 */
@Entity
public class OdatUser {

    @Id
    Long id;

    @Index
    private String email;

    private String name;

    public List<Watch> getWatches() {
        return watches;
    }

    public void setWatches(List<Watch> watches) {
        this.watches = watches;
    }

    private List<Watch> watches;

    public OdatUser(){
        this.watches = new ArrayList<Watch>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
