package mobile.gclifetest.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import mobile.gclifetest.pojoGson.EventComments;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.MyApplication;
import mobile.gclifetest.activity.R;

/**
 * Created by MRaoKorni on 8/2/2016.
 */
public class EventsCommentsAdapter extends BaseAdapter {
    List<EventComments> eventComntsPojo;
    private LayoutInflater inflator;
    private Context context;
    MyApplication app;

    public EventsCommentsAdapter(Context context2,
                                 List<EventComments> eventComntsPoj) {
        // TODO Auto-generated constructor stub
        this.context = context2;
        this.eventComntsPojo = eventComntsPoj;
        app = new MyApplication();
        inflator = (LayoutInflater) context2
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public boolean isEnabled(int position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return eventComntsPojo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO mylist_add_ind-generated method stub
        return eventComntsPojo.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return eventComntsPojo.get(position).hashCode();
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.comments_listrow, null);
            holder = new ViewHolder();
            holder.dpNameTxt = (TextView) convertView
                    .findViewById(R.id.nameTxt);
            holder.comntDetailTxt = (TextView) convertView
                    .findViewById(R.id.commentTxt);
            holder.timeTxt = (TextView) convertView
                    .findViewById(R.id.timeTxt);
            Constants.setRobotoLightFont(context, holder.dpNameTxt);
            Constants.setRobotoLightFont(context, holder.comntDetailTxt);
            Constants.setRobotoLightFont(context, holder.timeTxt);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.dpNameTxt.setTag(position);
        holder.comntDetailTxt.setTag(position);

        holder.dpNameTxt.setText(eventComntsPojo.get(position).getUsername());
        holder.comntDetailTxt.setText(Html.fromHtml(eventComntsPojo.get(position).getComment()));

        String createdAt =eventComntsPojo.get(position).getCreated_at();
        MyApplication app = new MyApplication();
        createdAt = app.convertDateEmail(createdAt);
        String time = eventComntsPojo.get(position)
                .getCreated_at().substring(11, 19);
        time = app.convertTimeEmail(time);

        holder.timeTxt.setText(createdAt + "," + time);


     //   String date = app.convertTimeComnts(eventComntsPojo.get(position).getCreated_at());
      //  holder.timeTxt.setText(date);

        return convertView;
    }

    public class ViewHolder {
        public TextView dpNameTxt, comntDetailTxt, timeTxt;
    }

}
