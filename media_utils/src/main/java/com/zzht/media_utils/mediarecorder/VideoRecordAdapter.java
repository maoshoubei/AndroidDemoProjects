package com.zzht.media_utils.mediarecorder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zzht.media_utils.R;

import java.util.List;

/**
 * @author clown
 * date 2017/11/17
 */
public class VideoRecordAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater inflater;
    private List<String> dataSource;

    /**
     * @param context
     */
    public VideoRecordAdapter(Context context, List<String> dataSource) {
        super();
        this.mContext = context;
        this.dataSource = dataSource;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.image_grid_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String model = dataSource.get(position);

        holder.image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.video_placeholder));
        holder.imgPlay.setVisibility(View.VISIBLE);

        return convertView;
    }

    class ViewHolder {
        private ImageView image;
        private ImageView imgPlay;

        public ViewHolder(View convertView) {
            super();
            image = (ImageView) convertView.findViewById(R.id.image_view);
            imgPlay = (ImageView) convertView.findViewById(R.id.img_play);
        }
    }


    public void setDataSource(List<String> dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

}
