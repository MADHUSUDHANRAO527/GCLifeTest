package mobile.gclifetest.PojoGson;

/**
 * Created by goodworklabs on 04/02/2016.
 */
public class EventImages {
    private int id;

    private String updated_at;

    private String image_type;

    private String image_url;

    private String created_at;

    private int event_id;

    public int getId ()
    {
        return id;
    }

    public void setId (int id)
    {
        this.id = id;
    }

    public String getUpdated_at ()
    {
        return updated_at;
    }

    public void setUpdated_at (String updated_at)
    {
        this.updated_at = updated_at;
    }

    public String getImage_type ()
    {
        return image_type;
    }

    public void setImage_type (String image_type)
    {
        this.image_type = image_type;
    }

    public String getImage_url ()
    {
        return image_url;
    }

    public void setImage_url (String image_url)
    {
        this.image_url = image_url;
    }

    public String getCreated_at ()
    {
        return created_at;
    }

    public void setCreated_at (String created_at)
    {
        this.created_at = created_at;
    }

    public int getEvent_id ()
    {
        return event_id;
    }

    public void setEvent_id (int event_id)
    {
        this.event_id = event_id;
    }
}