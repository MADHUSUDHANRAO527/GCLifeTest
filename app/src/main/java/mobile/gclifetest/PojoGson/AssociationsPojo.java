package mobile.gclifetest.pojoGson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by goodworklabs on 12/02/2016.
 */
public class AssociationsPojo {
    public String getStatecode() {
        return statecode;
    }

    public void setStatecode(String statecode) {
        this.statecode = statecode;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getLocationcode() {
        return locationcode;
    }

    public void setLocationcode(String locationcode) {
        this.locationcode = locationcode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAssociationname() {
        return associationname;
    }

    public void setAssociationname(String associationname) {
        this.associationname = associationname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public int getPincode() {
        return pincode;
    }

    public void setPincode(int pincode) {
        this.pincode = pincode;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getActiveflag() {
        return activeflag;
    }

    public void setActiveflag(String activeflag) {
        this.activeflag = activeflag;
    }

    public String getTownship_master_id() {
        return township_master_id;
    }

    public void setTownship_master_id(String township_master_id) {
        this.township_master_id = township_master_id;
    }

    public String getDistriccode() {
        return districcode;
    }

    public void setDistriccode(String districcode) {
        this.districcode = districcode;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public List<SocietiesPojo> getScotitiesPojo() {
        return scotitiesPojo;
    }

    public void setScotitiesPojo(List<SocietiesPojo> scotitiesPojo) {
        this.scotitiesPojo = scotitiesPojo;
    }

    private String statecode;

    private String countrycode;

    private String locationcode;

    private String address1;

    private String address2;

    private String associationname;

    private int id;

    private String citycode;

    private int pincode;

    private String updated_at;

    private String activeflag;

    private String township_master_id;

    private String districcode;

    private String created_at;

    @SerializedName("society_masters")
    List<SocietiesPojo> scotitiesPojo;
}
