package mobile.gclifetest.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

import mobile.gclifetest.MaterialDesign.ProgressBarCircularIndeterminate;
import mobile.gclifetest.PojoGson.EventImages;
import mobile.gclifetest.activity.GalleryImageViewer;
import mobile.gclifetest.activity.R;

/**
 * Created by MRaoKorni on 8/2/2016.
 */
public class EventsImagesAdapter extends BaseAdapter {
    private LayoutInflater inflator;
    List<EventImages> eventImagesPojo;
    private Context context;
    ViewHolder holder = null;
    ImageView thumbnailImg;
    String eventName,videoPath;
    ProgressBar thumbPbar;
    Bitmap bitmap = null;
    ImageLoader imageLoader;
    DisplayImageOptions options;
    JSONArray mediaArr;
    ProgressBarCircularIndeterminate pDialog;
    public EventsImagesAdapter(Context activity,
                                 List<EventImages> eventImagesPoj) {
        // TODO Auto-generated constructor stub
        this.context = activity;
        this.eventImagesPojo = eventImagesPoj;
        inflator = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.no_media)
                .showImageOnFail(R.drawable.no_media)
                .showImageOnLoading(R.drawable.no_media).build();
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
            thumbnailImg = (ImageView) convertView
                    .findViewById(R.id.thumbnail);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //imageViewThumb = new ImageView(context);
        final String imgUrl = eventImagesPojo.get(position).getImage_url();
        System.out.println(imgUrl);
        if (imgUrl == "[]" || imgUrl.equals("[]") || imgUrl == ""
                || imgUrl.equals("")) {


        } else if (eventName == "Videos" || eventName.equals("Videos")) {
            new BitmapCall(imgUrl).execute();
        } else {
            thumbPbar.setVisibility(View.GONE);
            imageLoader.displayImage(imgUrl, thumbnailImg, options);
            thumbnailImg.setScaleType(ImageView.ScaleType.FIT_XY);
        }


        return convertView;
    }

    public class ViewHolder {
    }

    class OnImageClickListener implements View.OnClickListener {

        int _postion;

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(context, GalleryImageViewer.class);
            System.out.println(_postion + "CLICKED");
            i.putExtra("position", _postion);
            i.putExtra("Images", mediaArr.toString());
            context.startActivity(i);
        }

    }
    public class BitmapCall extends AsyncTask<Void, Void, Void> {

        public BitmapCall(String url) {
            videoPath=url;
        }

        @Override
        protected Void doInBackground(Void... params) {

            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                else
                    mediaMetadataRetriever.setDataSource(videoPath);
                //   mediaMetadataRetriever.setDataSource(videoPath);
                bitmap = mediaMetadataRetriever.getFrameAtTime();

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    throw new Throwable(
                            "Exception in retriveVideoFrameFromVideo(String videoPath)"
                                    + e.getMessage());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } finally {
                if (mediaMetadataRetriever != null) {
                    mediaMetadataRetriever.release();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pDialog.setVisibility(View.GONE);
            thumbnailImg.setImageBitmap(bitmap);
            thumbPbar.setVisibility(View.GONE);
        }
    }
}
