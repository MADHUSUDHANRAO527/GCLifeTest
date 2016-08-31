package mobile.gclifetest.pojoGson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by goodworklabs on 04/02/2016.
 */
public class EventsPojo {

    private int id;

    private String association_id;

    private String sdesc;

    private String title;

    private String member_type;

    private String bdesc;

    private String event_type;

    private String society_name;

    private String association_name;

    private String society_id;

    private int user_id;

    private String url;

    public List<EventComments> getEvent_comments() {
        return event_comments;
    }

    public void setEvent_comments(List<EventComments> event_comments) {
        this.event_comments = event_comments;
    }

    public List<EventLikes> getEvent_likes() {
        return event_likes;
    }

    public void setEvent_likes(List<EventLikes> event_likes) {
        this.event_likes = event_likes;
    }

    public List<EventImages> getEvent_images() {
        return event_images;
    }

    public void setEvent_images(List<EventImages> event_images) {
        this.event_images = event_images;
    }

    private List<EventComments> event_comments;

    private List<EventLikes> event_likes;
    @SerializedName("eventimages")
    private List<EventImages> event_images;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAssociation_id() {
        return association_id;
    }

    public void setAssociation_id(String association_id) {
        this.association_id = association_id;
    }

    public String getSdesc() {
        return sdesc;
    }

    public void setSdesc(String sdesc) {
        this.sdesc = sdesc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMember_type() {
        return member_type;
    }

    public void setMember_type(String member_type) {
        this.member_type = member_type;
    }

    public String getBdesc() {
        return bdesc;
    }

    public void setBdesc(String bdesc) {
        this.bdesc = bdesc;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getSociety_name() {
        return society_name;
    }

    public void setSociety_name(String society_name) {
        this.society_name = society_name;
    }

    public String getSociety_id() {
        return society_id;
    }

    public void setSociety_id(String society_id) {
        this.society_id = society_id;
    }

    public String getAssociation_name() {
        return association_name;
    }

    public void setAssociation_name(String association_name) {
        this.association_name = association_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
