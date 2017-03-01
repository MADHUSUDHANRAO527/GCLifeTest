package mobile.gclifetest.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;

import java.util.List;

import mobile.gclifetest.activity.R;
import mobile.gclifetest.materialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.pojoGson.EventImages;

/**
 * Created by MRaoKorni on 8/2/2016.
 */
public class EventsImagesAdapter extends BaseAdapter {
    private LayoutInflater inflator;
    List<EventImages> eventImagesPojo;
    private Context context;
    ViewHolder holder = null;
    String eventName,videoPath;
    ProgressBar thumbPbar;
    Bitmap bitmap = null;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    JSONArray mediaArr;
    ProgressBarCircularIndeterminate pDialog;
    public EventsImagesAdapter(Context activity,
                                 List<EventImages> eventImagesPoj,String evenName,ProgressBar pBar) {
        // TODO Auto-generated constructor stub
        this.context = activity;
        this.eventImagesPojo = eventImagesPoj;
        inflator = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance();
        eventName=evenName;
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.no_media)
                .showImageOnFail(R.drawable.no_media)
                .showImageOnLoading(R.drawable.no_media).build();
        thumbPbar=pBar;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return eventImagesPojo.size();
    }

    @Override
    public Object getItem(int pos) {
        // TODO Auto-generated method stub
        return eventImagesPojo.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        // TODO Auto-generated method stub
        return pos;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub

        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflator.inflate(R.layout.thumnail, parent,
                    false);
            holder = new ViewHolder();
            holder.thumbnailImg = (ImageView) convertView
                    .findViewById(R.id.thumbnail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //imageViewThumb = new ImageView(context);
        final String imgUrl = eventImagesPojo.get(position).getImage_url();
      //  System.out.println(imgUrl);
        Log.d("POS : ",position+"");
        if (eventName == "Videos" || eventName.equals("Videos")) {
            // new BitmapCall(imgUrl).execute();
            thumbPbar.setVisibility(View.GONE);
            //getVideoId(imgUrl);
            imageLoader.displayImage("http://img.youtube.com/vi/" + imgUrl.substring(imgUrl.length() - 11) + "/default.jpg",  holder.thumbnailImg, options);
            holder.thumbnailImg.setScaleType(ImageView.ScaleType.FIT_XY);

        } else if(imgUrl!=null){
            thumbPbar.setVisibility(View.GONE);
            imageLoader.displayImage(imgUrl, holder.thumbnailImg, options);
            Log.d("Image url",  imgUrl);
            holder.thumbnailImg.setScaleType(ImageView.ScaleType.FIT_XY);
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView thumbnailImg;
    }
}
