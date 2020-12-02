package com.saechaol.learningapp.model;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.pchmn.materialchips.model.ChipInterface;

/**
 * An implementation of the Material Design <i>Chips</i> component for Android UI. The library is provided by
 * @author pchmn
 */
public class ContactChip implements ChipInterface{

    private String id, email, name, type;

    /**
     * Contact chip object constructor
     * @param id unique user ID
     * @param email user email
     * @param name name
     * @param type info type
     */
    public ContactChip(String id, String email, String name,String type) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.type=type;
    }

    public String getEmail(){
        return  email;
    }

    @Override
    public Object getId() {
        return id;
    }

    @Override
    public String getInfo() {
        return type;
    }

    @Override
    public String getLabel() {
        return name + "(" + email + ")";
    }

    @Override
    public Drawable getAvatarDrawable() {
        return null;
    }

    @Override
    public Uri getAvatarUri() {
        return null;
    }

}
