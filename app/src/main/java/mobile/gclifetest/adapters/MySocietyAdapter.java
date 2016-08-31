package mobile.gclifetest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import mobile.gclifetest.utils.Constants;
import mobile.gclifetest.activity.R;

/**
 * Created by MRaoKorni on 8/26/2016.
 */
public class MySocietyAdapter extends BaseAdapter {
    String[] result;
    Context context;
    int[] imageId;
    private static LayoutInflater inflater = null;

    public MySocietyAdapter(Context mContext, String[] prgmNameList,
                            int[] prgmImages) {
        // TODO Auto-generated constructor stub
        result = prgmNameList;
        context = mContext;
        imageId = prgmImages;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.home_grid_row, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textView1);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
        Constants.setRobotoLightFont(context, holder.tv);
        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);


        return rowView;
    }

}
