package com.briangerardsweeney.odat.watchService;

import com.briangerardsweeney.odat.watchService.odatEntities.OdatUser;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * The Objectify object model for device registrations we are persisting
 */
@Entity
public class RegistrationRecord {

    @Id
    Long id;

    @Index
    private String regId;
    // you can add more fields...

    private OdatUser user;

    public RegistrationRecord() {
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public OdatUser getUser() {
        return user;
    }

    public void setUser(OdatUser user) {
        this.user = user;
    }
}