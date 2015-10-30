package org.ei.opensrp.mcare.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


import java.util.Map;

/**
 * Created by Ahmed on 19-Oct-15.
 */
public class Client {


    private String id ;
    private  String relationalId;
    private String type;
    private final Map<String, String> details;
    private String photoPath;


    public Client(String id, String relationalId ,String type , Map<String, String> details){

        this.id=id;
        this.relationalId=relationalId;
        this.type=type;
        this.details=details;
    }
    public Client( String relationalId ,String type , Map<String, String> details){


        this.relationalId=relationalId;
        this.type=type;
        this.details=details;
    }

    public Client(String type , Map<String, String> details){


        this.type=type;
        this.details=details;
    }

    public Client( Map<String, String> details){



        this.details=details;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public Map<String, String> details() {
        return details;
    }



    public void setRelationalId(String relationalId) {
        this.relationalId = relationalId;
    }


    public String getRelationalId() {
        return relationalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public Client withPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
