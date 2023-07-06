package home.com.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.recyclerview.widget.RecyclerView;
import home.com.myapplication.R;

public class DeviceAdaptor extends RecyclerView.Adapter<DeviceAdaptor.MyViewHolder> {
    private Context mContext;
    private List<Device> deviceList;
    private View.OnClickListener clk;
    public static int lastPosition = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvId;
        TextView tvName;
        TextView tvStatus;
        TextView tvLast;
        TextView tvPhone;
        View row;
        public MyViewHolder(View view) {
            super(view);
            tvId = view.findViewById(R.id.devId);
            tvName = view.findViewById(R.id.devName);
            tvStatus = view.findViewById(R.id.devStatus);
            tvLast = view.findViewById(R.id.devLastUpdate);
            tvPhone = view.findViewById(R.id.devPhone);
            row = view;

        }
    }

    public DeviceAdaptor(Context mContext, ArrayList<Device> albumList, View.OnClickListener clk) {
        this.mContext = mContext;
        this.deviceList = albumList;
        this.clk = clk;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_row, parent, false);

        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Device dev = deviceList.get(position);
        holder.tvId.setText(dev.id+"");
        if (dev.name != null)
           holder.tvName.setText(dev.name);
        if (dev.status != null)
          holder.tvStatus.setText(dev.status);
        if (dev.phone != null)
         holder.tvPhone.setText(dev.phone);
        if (dev.lastUpdate != null && dev.lastUpdate.length() > 2) {
            String dtStart = dev.lastUpdate.split("\\.")[0]+"Z";
           // String dtStart = "2010-10-15T09:27:37Z";
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                Date date = format.parse(dtStart);
                PersianCalendar prc = new PersianCalendar(date.getTime());
                dtStart = prc.getPersianShortDateTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvLast.setText(dtStart);
        }
        holder.row.setTag(position);
        holder.row.setOnClickListener(clk);

        setAnimation(holder.row,position);
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}

