package mobile.gclifetest.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import mobile.gclifetest.Utils.Constants;
import mobile.gclifetest.activity.R;

/**
 * Created by MRaoKorni on 8/1/2016.
 */
public class HomeAdapter extends BaseAdapter {

    String[] result;
    Context context;
    int[] imageId;
    LayoutInflater inflater = null;

    public HomeAdapter(Context mContext, String[] prgmNameList,
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
        LinearLayout rowLay;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.home_grid_row, null);
        holder.tv = (TextView) rowView.findViewById(R.id.textView1);
        holder.img = (ImageView) rowView.findViewById(R.id.imageView1);
        holder.rowLay = (LinearLayout) rowView.findViewById(R.id.rowLay);
        Constants.setRobotoLightFont(context, holder.tv);
        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);
        if (position == 0) {
            holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebg));
        } else if (position == 1) {
            holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebgg));
        } else if (position == 2) {
            holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebgg));
        } else if (position == 3) {
            holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebg));
        } else if (position == 4) {
            holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebg));
        } else if (position == 5) {
            holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebgg));
        } else if (position == 6) {
            holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebgg));
        } else if (position == 7) {
            holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebg));
        } else if (position == 8) {
            holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebg));
        } else if (position == 9) {
            holder.rowLay.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.homebgg));
        }
        return rowView;
    }
}
