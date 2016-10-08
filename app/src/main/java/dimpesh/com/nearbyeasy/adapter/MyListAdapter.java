package dimpesh.com.nearbyeasy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dimpesh.com.nearbyeasy.R;


public class MyListAdapter extends BaseAdapter {

    ArrayList<String> name = new ArrayList<String>();
    ArrayList<String> image = new ArrayList<String>();
    ArrayList<String> vicinity = new ArrayList<String>();

    Context c;
    LayoutInflater inflater;

    public MyListAdapter(Context c, ArrayList<String> name, ArrayList<String> image, ArrayList<String> vicinity) {
        this.name = name;
        this.image = image;
        this.vicinity=vicinity;
        this.c = c;
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView tv_name,tv_vicinity;
        ImageView iv_icon;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView;
        itemView = inflater.inflate(R.layout.list_item, parent, false);

        tv_name = (TextView) itemView.findViewById(R.id.item_title);
        tv_vicinity = (TextView) itemView.findViewById(R.id.item_vicinity);
        iv_icon=(ImageView)itemView.findViewById(R.id.item_img);

        tv_name.setText(name.get(position).toString());
        tv_vicinity.setText(vicinity.get(position).toString());

        Picasso.with(c)
                .load(image.get(position)).placeholder(R.drawable.img_placeholder)
                .resize(50,50)
                .centerCrop()
                .into(iv_icon);
        itemView.setContentDescription(name.get(position));
        return itemView;
    }

}
