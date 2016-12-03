package mobile.gclifetest.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import mobile.gclifetest.activity.HomeActivity;
import mobile.gclifetest.activity.R;
import mobile.gclifetest.fragments.IdeasDetailFragment;
import mobile.gclifetest.http.EvenstPost;
import mobile.gclifetest.http.MemsPost;
import mobile.gclifetest.pojoGson.EventsPojo;
import mobile.gclifetest.pojoGson.FlatDetailsPojo;
import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.utils.MyApplication;

/**
 * Created by MRaoKorni on 8/2/2016.
 */
public class ListIdeasAdapter extends BaseAdapter {
    private List<EventsPojo> eventsPojos = new ArrayList<EventsPojo>();
    private LayoutInflater inflator;
    private Context context;

    private String eventName, eid, deleteEveId;
    private SharedPreferences userPref;
    private FlatDetailsPojo flats;
    private Dialog m_dialog;
    private JSONObject jsonLike, jsonDelete;
    View v;
    public ListIdeasAdapter(Context activity,
                            List<EventsPojo> eventsPojo,FlatDetailsPojo flat,String eventNam) {
        // TODO Auto-generated constructor stub
        this.context = activity;
        this.eventsPojos = eventsPojo;
        inflator = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        userPref = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        this.flats=flat;
        eventName=eventNam;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return eventsPojos.size();
    }

    @Override
    public Object getItem(int pos) {
        // TODO Auto-generated method stub
        return eventsPojos.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        // TODO Auto-generated method stub
        return pos;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        EventsPojo eventPojo = eventsPojos.get(position);
        v = convertView;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.idea_list_row, parent,
                    false);
            holder = new ViewHolder();
            holder.titleTxt = (TextView) convertView
                    .findViewById(R.id.titleTxt);
            holder.sDiscTxt = (TextView) convertView
                    .findViewById(R.id.sDiscTxt);
            holder.attchCountTxt = (TextView) convertView
                    .findViewById(R.id.attchcountTxt);
            holder.comntCountTxt = (TextView) convertView
                    .findViewById(R.id.cmntcountTxt);
            holder.likesCountTxt = (TextView) convertView
                    .findViewById(R.id.likecountTxt);
            holder.detailClick = (RelativeLayout) convertView
                    .findViewById(R.id.detailClick);
            holder.shareImg = (ImageView) convertView
                    .findViewById(R.id.shareImg);
            holder.likeImg = (ImageView) convertView
                    .findViewById(R.id.likeImg);
            holder.deleteImg = (ImageView) convertView
                    .findViewById(R.id.deleteImg);

            Constants.setRobotoLightFont(context, holder.titleTxt);
            Constants.setRobotoLightFont(context, holder.sDiscTxt);
            Constants.setRobotoLightFont(context, holder.attchCountTxt);
            Constants.setRobotoLightFont(context, holder.comntCountTxt);
            Constants.setRobotoLightFont(context, holder.likesCountTxt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.titleTxt.setText(eventPojo.getTitle());
        holder.sDiscTxt.setText(eventPojo.getSdesc());
        holder.attchCountTxt.setText(String.valueOf(eventPojo.getEvent_images().size()));
        // }
        // if (eventsPojos.get(position).getEvent_comments().size() > 0) {
        holder.comntCountTxt.setText(String.valueOf(eventPojo.getEvent_comments().size()));
        // }
        eid = String.valueOf(eventsPojos.get(position).getId());
        final ArrayList<String> likeCheckArr = new ArrayList<String>();
        if (eventsPojos.get(position).getEvent_likes().size() > 0) {
            holder.likesCountTxt.setText(String.valueOf(eventsPojos.get(position).getEvent_likes().size()));
            for (int i = 0; i < eventsPojos.get(position).getEvent_likes().size(); i++) {
                likeCheckArr.add(String.valueOf(eventsPojos.get(position).getEvent_likes().get(i).getUser_id()));
            }

        }

        holder.detailClick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                IdeasDetailFragment fragment = new IdeasDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("EventName", eventName);
                bundle.putString("id", String.valueOf(eventsPojos.get(position).getId()));
                fragment.setArguments(bundle);
                ((HomeActivity) context).addFragment(fragment);
            }
        });
        if (String.valueOf(eventsPojos.get(position).getUser_id()) == userPref.getString("USERID", "NV") || String.valueOf(eventsPojos.get(position).getUser_id()).equals(userPref.getString("USERID", "NV"))) {
            holder.deleteImg.setVisibility(View.VISIBLE);
        } else if (flats.getMember_type() == "Treasurer" || flats.getMember_type().equals("Treasurer")) {
            holder.deleteImg.setVisibility(View.VISIBLE);
        } else {
            holder.deleteImg.setVisibility(View.GONE);
        }
        holder.deleteImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // to remove row

                m_dialog = new Dialog(context);
                m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                m_dialog.setContentView(R.layout.areyousuredelepopup);
                m_dialog.getWindow().getAttributes().windowAnimations = R.style.popup_login_dialog_animation;

                TextView noTxt = (TextView) m_dialog.findViewById(R.id.noTxt);
                TextView yesTxt = (TextView) m_dialog.findViewById(R.id.yesTxt);
                noTxt.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        m_dialog.dismiss();
                    }
                });
                yesTxt.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        deleteEveId = String.valueOf(eventsPojos.get(position).getId());
                        System.out.println(deleteEveId + " !!!!!!!!!!!!!!!!!!!" + String.valueOf(eventsPojos.get(position).getId()));
                        new DeleteEvent().execute();
                        eventsPojos.remove(position);
                        m_dialog.dismiss();
                    }
                });

                m_dialog.show();
            }
        });
        holder.shareImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d("SHARE CONTENT", eventsPojos.get(position).getTitle() + " " + eventsPojos.get(position).getSdesc() + "\n"
                        + eventsPojos.get(position).getBdesc());
                Intent sharingIntent = new Intent(
                        Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(
                        Intent.EXTRA_SUBJECT,
                        eventsPojos.get(position).getTitle());
                sharingIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        eventsPojos.get(position).getTitle() + "\n" + eventsPojos.get(position).getSdesc() + "\n"
                                + eventsPojos.get(position).getBdesc());
                context.startActivity(Intent.createChooser(sharingIntent,
                        "Share via"));
            }
        });
        System.out.println(eventsPojos.get(position).getEvent_likes() + " !!!!!!!!!SIZE!!!!!!!!!!" + likeCheckArr.toString());

        if (eventsPojos.get(position).getEvent_likes().size() > 0) {
            if (likeCheckArr.contains(userPref.getString("USERID", "NV"))) {
                holder.likeImg.setImageResource(R.drawable.liked);
            } else {
                holder.likeImg.setImageResource(R.drawable.unlike);
            }
        }

        holder.likeImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (likeCheckArr.contains(userPref.getString("USERID", "NV"))) {
                    Constants.showSnack(v, "Oops! You already liked it!",
                                "OK");
                } else {
                    eid = String.valueOf(eventsPojos.get(position).getId());
                    int likes = Integer.valueOf(String.valueOf(eventsPojos.get(position).getEvent_likes().size()));
                    int lik = likes + 1;
                    holder.likesCountTxt.setText(String.valueOf(lik));
                    holder.likeImg.setImageResource(R.drawable.liked);
                    LikeUnlike();
                    }
            }
        });
        return convertView;
    }

    protected void LikeUnlike() {
        // TODO Auto-generated method stub
        new Like().execute();
    }

    public class ViewHolder {
        TextView titleTxt, sDiscTxt, attchCountTxt, comntCountTxt,
                likesCountTxt;
        ImageView shareImg, likeImg, deleteImg;
        RelativeLayout detailClick;
    }

    private class Like extends AsyncTask<Void, Void, Void> {
        @SuppressLint("InlinedApi")
        protected void onPreExecute() {
            // pDialog.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(Void... params) {
            JSONObject jsonLik = new JSONObject();
            try {
                JSONObject json = new JSONObject();
                json.put("event_id", eid);
                json.put("user_id", userPref.getString("USERID", "NV"));
                jsonLik.put("event_like", json);
                jsonLike = MemsPost.PostLike(jsonLik, MyApplication.HOSTNAME);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            System.out.println(jsonLike
                    + "^^^^^^^^^^^^ RESULT OF LIKES ^^^^^^^^^^^^^^");

            if (jsonLike != null) {

            } else {
                Constants.showSnack(v,
                        "Oops! Something went wrong. Please check internet connection!",
                        "OK");
            }
        }
    }

    public class DeleteEvent extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            //	pDialogPop.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {

                jsonDelete = EvenstPost.makeDelete(MyApplication.HOSTNAME, deleteEveId, "events");
                System.out.println(jsonDelete + " **************");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            /*Constants.showSnack(v,
                    "Deleted!",
                    "OK");*/
            notifyDataSetChanged();
        }
    }
}
