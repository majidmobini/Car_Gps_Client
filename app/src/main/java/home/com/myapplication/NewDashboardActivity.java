package home.com.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import home.com.Classes.Device;
import home.com.Classes.DeviceAdaptor;
import home.com.Classes.Locations;
import home.com.Classes.Position;
import home.com.Materia.Custom_progress;
import home.com.Materia.Custom_toast;
import home.com.Materia.Set_font;
import home.com.internet.Get_class;

import static home.com.Classes.Locations.locations;

public class NewDashboardActivity extends Activity implements View.OnClickListener ,TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{

    private MapView mapView;
    String location;
    private IMapController mMapController;
    private ImageView im_bat;
    private ImageView im_key;
    private ImageView im_door_open;
    private ImageView im_sat;
    private ImageView im_gprs;
    private ImageView im_speed;
    private TextView tv_speed;
    View llarrow ;
    TextView tvDevName;

    TextView tvStatus;
    ArrayList<Device> devices = new ArrayList<>();
    ArrayList<Position> positions = new ArrayList<>();
    Handler updateHdl = new Handler();
    int showingDevice = 0;

    private Runnable updateRun = new Runnable() {
        @Override
        public void run() {
            Log.d("Runable","Started");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    GetData();
                    updateHdl.postDelayed(updateRun,60000);
                }
            }).start();

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_dashboard);
        llarrow = findViewById(R.id.llArraows);
        im_bat = findViewById(R.id.im_bat);
        im_key = findViewById(R.id.im_key);
        im_door_open = findViewById(R.id.im_door);
        im_sat = findViewById(R.id.im_sat);
        im_gprs = findViewById(R.id.im_gprs);
        im_speed =  findViewById(R.id.im_speed);
        tv_speed = findViewById(R.id.tv_speed);
        tvStatus = findViewById(R.id.tvStatus);
        tvDevName = findViewById(R.id.tvDevName);
        findViewById(R.id.im_path).setOnClickListener(this);
        findViewById(R.id.im_lock).setOnClickListener(this);
        findViewById(R.id.im_unlock).setOnClickListener(this);
        findViewById(R.id.im_listen).setOnClickListener(this);
        findViewById(R.id.im_dev_setting).setOnClickListener(this);
        findViewById(R.id.im_dev_status).setOnClickListener(this);
        findViewById(R.id.im_location).setOnClickListener(this);
        findViewById(R.id.im_gps_set).setOnClickListener(this);

        findViewById(R.id.imRefresh).setOnClickListener(this);
        mapView= findViewById(R.id.mapview);
        //mapView.setTileSource(TileSourceFactory.MAPNIK);

        mapView.setTileSource(new CustomXYTileSource("Radyab",0,18,512,".png",new String[] {
                "http://mt0.google.com/vt/","http://mt1.google.com/vt/","http://mt2.google.com/vt/","http://mt3.google.com/vt/"},"GoldTrax"));

        mapView.setClickable(false);
        mapView.setBuiltInZoomControls(false);

        mapView.setMultiTouchControls(true);


        mMapController = mapView.getController();
        //mMapController.zoo
        // mMapController.setZoom(17); //set initial zoom-level, depends on your need
        mapView.setUseDataConnection(true);
        ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem/(1024*1024*1024);
        if (totalMemory < 3 )
        {
            Configuration.getInstance().setCacheMapTileCount((short)12);
            Configuration.getInstance().setCacheMapTileOvershoot((short)12);

        }
        Log.d("Runnable","Runned");
        updateHdl.post(updateRun);
        findViewById(R.id.imLeftArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showingDevice = showingDevice - 1;
                if(showingDevice < 0)
                    showingDevice = positions.size()-1;
                if(showingDevice < positions.size())
                     UpdateMapAndIcons(positions.get(showingDevice));
            }
        });
        findViewById(R.id.imRightArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showingDevice = showingDevice + 1;
                if(showingDevice > positions.size()-1)
                    showingDevice = 0;
                if(showingDevice < positions.size())
                     UpdateMapAndIcons(positions.get(showingDevice));
            }
        });
    }

    private void SetStatus(final String text)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvStatus.setText(text);
            }
        });
    }
    private void GetData()
    {
        SetStatus("در حال بروزرسانی...");
        final  Pref prf = new Pref(NewDashboardActivity.this);
        if (prf.PrefGetString(Pref.SITE_USERNAME).isEmpty() || prf.PrefGetString(Pref.SITE_PASSWORD).isEmpty())
        {
          //  updateHdl.postDelayed(updateRun,60000);
            SetStatus("لطفاً اطلاعات سامانه را تکمیل کنید.");
            return;
        }
       /* if(devices.size() > 0)
        {
            GetLastPointData();

            return;
        }*/
        String data = new Get_class(prf.PrefGetString(Pref.SITE_USERNAME),prf.PrefGetString(Pref.SITE_PASSWORD)).GetUrl("http://gps.jupin.ir/api/devices?");
        if (data.equals("Error") || data.length() < 2)
        {
            SetStatus("خطا در برقراری ارتباط با سرور.");
            return;
        }
        try
        {
            JSONArray jsa = new JSONArray(data);
            Gson gson = new GsonBuilder().create();
            devices.clear();
            for (int i = 0; i < jsa.length(); i++) {
                Device dev = gson.fromJson(jsa.getString(i), Device.class);
                if (dev.positionId != 0)
                     devices.add(dev);

            }
            if (devices.size() == 0)
            {
                SetStatus("دستگاهی یافت نشد.");
                return;
            }
            GetLastPointData();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void GetLastPointData()
    {
        final  Pref prf = new Pref(NewDashboardActivity.this);

        StringBuilder link = new StringBuilder();
        if(devices.size() > 0)
            link = new StringBuilder("http://gps.jupin.ir/api/positions?id=" + devices.get(0).positionId);
        for ( int i = 1 ; i < devices.size() && i<20;i++) {
            Device dev = devices.get(i);
            link.append("&id=").append(dev.positionId);
        }
        String data = new Get_class(prf.PrefGetString(Pref.SITE_USERNAME),prf.PrefGetString(Pref.SITE_PASSWORD)).GetUrl(link.toString());
        if (data.equals("Error") || data.length() < 2)
        {
            SetStatus("خطا در برقراری ارتباط با سرور.");
            return;
        }
        try
        {
            JSONArray jsa = new JSONArray(data);
            Gson gson = new GsonBuilder().create();
            if (jsa.length() > 0)
            {
                positions.clear();
                for (int i=0;i<jsa.length();i++) {
                    JSONObject jso = jsa.getJSONObject(i);
                    positions.add(gson.fromJson(jso.toString(), Position.class));
                }
                if (positions.size()>0)
                {
                    //if (showingDevice < positions.size())
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(positions.size() > 1)
                                    llarrow.setVisibility(View.VISIBLE);
                                else
                                    llarrow.setVisibility(View.GONE);
                                if (showingDevice >= positions.size()) {
                                    showingDevice = 0;
                                }
                                UpdateMapAndIcons(positions.get(showingDevice));
                                SetStatus("بروزرسانی انجام شد.");
                            }
                        });
                    }
                }
                else
                {
                    SetStatus("نقطه ای یافت نشد.");
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }


    }
    private void UpdateMapAndIcons(Position pos)
    {
        String name = "";
        for (Device dev : devices)
        {
            if (dev.id == pos.deviceId)
            {
                name = dev.name;
                break;
            }
        }
        tvDevName.setText(name);
        if (pos.attributes.batteryLevel < 25) {
            im_bat.setImageResource(R.drawable.bat_low);
        }
        else if (pos.attributes.batteryLevel<50) {
            im_bat.setImageResource(R.drawable.bat_med);
        }
        else if (pos.attributes.batteryLevel<75) {
            im_bat.setImageResource(R.drawable.bat_good);
        } else {
            im_bat.setImageResource(R.drawable.bat_full);
        }
        im_key.setVisibility(pos.attributes.Switch.equals("OFF")? View.GONE:View.VISIBLE);
        im_door_open.setVisibility(pos.attributes.Door.equals("CLOSE")? View.GONE:View.VISIBLE);
        if (pos.attributes.sat<5)
        {
            im_sat.setImageResource(R.drawable.gps_red);
        }
        else if(pos.attributes.sat<10)
        {
            im_sat.setImageResource(R.drawable.gps_yellow);
        }
        else{
            im_sat.setImageResource(R.drawable.gps_green);
        }

        if (pos.attributes.io5<15)
        {
            im_gprs.setImageResource(R.drawable.gprs_red);
        }
        else if(pos.attributes.io5<20)
        {
            im_gprs.setImageResource(R.drawable.gprs_yellow);
        }
        else{
            im_gprs.setImageResource(R.drawable.gprs_green);
        }
        DecimalFormat dcf = new DecimalFormat("#.#");
        tv_speed.setText( dcf.format(pos.speed * 1.852) +" Km/h");

        InfoWindow.closeAllInfoWindowsOn(mapView);
        GeoPoint gp = new GeoPoint(pos.latitude, pos.longitude);
       //

        Marker nodeMarker = new Marker(mapView);
        nodeMarker.setPosition(gp);
        nodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);

       Drawable nodeIcon2 = getResources().getDrawable(R.drawable.car_icon2);
        nodeMarker.setIcon(nodeIcon2);
     ////   nodeMarker.setRotation((float)(-pos.course + 90));

        StringBuilder stb = new StringBuilder();
        stb.append("نام: ");
        stb.append(name+"\n");
        stb.append("طول: ");
        stb.append(pos.longitude+"\n");
        stb.append("عرض: ");
        stb.append(pos.latitude+"\n");
        stb.append("سرعت: ");
        double spd = pos.speed * 1.852;
       // DecimalFormat dcf = new DecimalFormat("#.##");
        stb.append( dcf.format(spd) +"\n");
        stb.append("جهت: ");
        stb.append(pos.course+"\n");
        stb.append("زمان: ");
        //2020-01-14T09:53:12.000+0000
        String dtStart = pos.fixTime.split("\\.")[0]+"Z";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date date = format.parse(dtStart);
            PersianCalendar prc = new PersianCalendar(date.getTime());
            dtStart = prc.getPersianShortDateTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stb.append(dtStart);
        nodeMarker.setTitle(stb.toString());
        mMapController.animateTo(gp,14.0,1L);
        mapView.getOverlays().clear();
        mapView.getOverlays().add(nodeMarker);
        mapView.invalidate();

    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Runable","Paused");
        updateHdl.removeCallbacks(updateRun);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Runable","Resumed");
        updateHdl.removeCallbacks(updateRun);
        updateHdl.post(updateRun);
    }

    /*
              findViewById(R.id.im_path).setOnClickListener(this);
                findViewById(R.id.im_lock).setOnClickListener(this);
                findViewById(R.id.im_unlock).setOnClickListener(this);
                findViewById(R.id.im_listen).setOnClickListener(this);
                findViewById(R.id.im_dev_setting).setOnClickListener(this);
                findViewById(R.id.im_dev_status).setOnClickListener(this);
                findViewById(R.id.im_location).setOnClickListener(this);
                findViewById(R.id.im_gps_set).setOnClickListener(this);
             */
    @Override
    public void onClick(View v) {
        String sms = "";
        sms = new Pref(NewDashboardActivity.this).PrefGetString(Pref.USERNAME).toUpperCase() + " " +
                new Pref(NewDashboardActivity.this).PrefGetString(Pref.PASSWORD).toUpperCase() + " ";
        switch (v.getId())
        {
            case R.id.im_path:
                GetDevices();
                break;
            case R.id.im_lock:
                sms = sms + "SETPARAM 60000:1;";
                SendMsg(sms);
                break;
            case R.id.im_unlock:
                sms = sms + "SETPARAM 60000:0;";
                SendMsg(sms);
                break;
            case R.id.im_listen:
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:"+new Pref(NewDashboardActivity.this).PrefGetString(Pref.SIMNO)));
                if (ActivityCompat.checkSelfPermission(NewDashboardActivity.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    String permission = Manifest.permission.CALL_PHONE;
                    String[] permission_list = new String[1];
                    permission_list[0] = permission;
                    ActivityCompat.requestPermissions(this, permission_list, 1);
                    return;
                }
                else {
                    startActivity(phoneIntent);
                }
                break;
            case R.id.im_dev_setting:
                startActivity(new Intent(NewDashboardActivity.this,SettingsActivity.class));
                break;
            case R.id.im_dev_status:
                sms = sms + "GETSTATUS";
                SendMsg(sms);
                break;
            case R.id.im_location:
                sms = sms + "GETGPS";
                SendMsg(sms);
                break;
            case R.id.im_gps_set:
                startActivity(new Intent(NewDashboardActivity.this,devSettings.class));
                break;
            case R.id.imRefresh:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        GetData();
                    }
                }).start();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 )
        {
            if ( grantResults[0]  == PackageManager.PERMISSION_GRANTED)
            {
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:"+new Pref(NewDashboardActivity.this).PrefGetString(Pref.SIMNO)));
                startActivity(phoneIntent);

            }
        }
    }

    Device selectedDevice;

    private void ShowPathDate(int forday)
    {
        PersianCalendar persianCalendar = new PersianCalendar(System.currentTimeMillis());
        Calendar cld = Calendar.getInstance();

        persianCalendar.set(cld.get(Calendar.YEAR),cld.get(Calendar.MONTH),cld.get(Calendar.DATE),0,0);
       // persianCalendar.addPersianDate(Calendar.DAY_OF_MONTH,1);

       Log.d("Date1 ",persianCalendar.getTimeInMillis()+"");
        startCalender.setTimeInMillis(persianCalendar.getTimeInMillis() - (forday * 24*60*60*1000));
        persianCalendar.set(cld.get(Calendar.YEAR),cld.get(Calendar.MONTH),cld.get(Calendar.DATE),23,59);
        Log.d("Date2 ",persianCalendar.getTimeInMillis()+"");
        endCalender.setTimeInMillis(persianCalendar.getTimeInMillis() - (forday * 24*60*60*1000));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = startCalender.getTime();
        String dateTime = dateFormat.format(date);

        Date date2 = endCalender.getTime();
        String dateTime2 = dateFormat.format(date2);

        GetDateFromServer(dateTime,dateTime2);
    }
    private void SelecttimeDialog()
    {
        final Dialog dialog =new Dialog(NewDashboardActivity.this);// new Dialog(context);
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.select_path,null);

        Set_font.setkoodakFont(NewDashboardActivity.this,layout);

        layout.findViewById(R.id.btToday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPathDate(0);
                dialog.dismiss();

            }
        });
        layout.findViewById(R.id.btYesterday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPathDate(1);
                dialog.dismiss();

            }
        });
        layout.findViewById(R.id.btCustom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowTimeDialog();
                dialog.dismiss();
            }
        });

        layout.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(true);

        WindowManager.LayoutParams  lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);

    }

    private void ShowDeviceList()
    {
        final Dialog dialog =new Dialog(NewDashboardActivity.this);// new Dialog(context);
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.devices_dialog,null);
        RecyclerView recyclerView = layout.findViewById(R.id.recycler);
        DeviceAdaptor adapter;
        View.OnClickListener rowClicked = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (Integer)v.getTag();
                selectedDevice = devices.get(pos);
                //ShowTimePicker(1);
                SelecttimeDialog();
                dialog.dismiss();

            }
        };

        adapter = new DeviceAdaptor(NewDashboardActivity.this, devices,rowClicked);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(NewDashboardActivity.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new NewDashboardActivity.GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(true);

        WindowManager.LayoutParams  lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);


    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            outRect.top = spacing;
            outRect.left = spacing;
            outRect.right = spacing;
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    Calendar startCalender ;
    Calendar endCalender;
    private void GetDevices()
    {
        startCalender = Calendar.getInstance();
        endCalender = Calendar.getInstance();
        final  Pref prf = new Pref(NewDashboardActivity.this);
        if (prf.PrefGetString(Pref.SITE_USERNAME).isEmpty() ||prf.PrefGetString(Pref.SITE_PASSWORD).isEmpty())
        {
            Custom_toast.showCustomAlertLong(NewDashboardActivity.this,R.string.enter_siteUser);
            return;
        }
        final Dialog dl = Custom_progress.showCustomprogress(NewDashboardActivity.this);
        dl.setCancelable(false);
        dl.show();
        new Thread(new Runnable() {
            @Override
            public void run() {

                String data = new Get_class(prf.PrefGetString(Pref.SITE_USERNAME),prf.PrefGetString(Pref.SITE_PASSWORD)).GetUrl("http://gps.jupin.ir/api/devices?");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dl.dismiss();
                    }
                });
                if (data.equals("Error") || data.length() < 2)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Custom_toast.showCustomAlertLong(NewDashboardActivity.this,R.string.error_in_get_data);
                        }
                    });

                }
                else
                {
                    try
                    {
                        devices.clear();
                        JSONArray jsa = new JSONArray(data);
                        Gson gson = new GsonBuilder().create();
                        for (int i=0 ; i<jsa.length();i++)
                        {

                            devices.add(gson.fromJson(jsa.getString(i),Device.class));

                        }



                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ShowDeviceList();
                            }
                        });
                    }
                    catch (Exception ex)
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Custom_toast.showCustomAlertLong(NewDashboardActivity.this,R.string.error_in_get_data);
                            }
                        });

                    }
                }
            }
        }).start();

    }

    View timeDialogView;
    private void ShowTimeDialog()
    {
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        timeDialogView = inflater.inflate(R.layout.time_dialog,null);
        final Dialog dialog =new Dialog(NewDashboardActivity.this);// new Dialog(context);
        Set_font.setkoodakFont(NewDashboardActivity.this,timeDialogView);
        TextView tvStartDate = timeDialogView.findViewById(R.id.tvStartDate);
        TextView tvEndDate = timeDialogView.findViewById(R.id.textEndDate);

        timeDialogView.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        timeDialogView.findViewById(R.id.btOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                startCalender.setTimeZone(TimeZone.getTimeZone("UTC"));

                endCalender.setTimeZone(TimeZone.getTimeZone("UTC"));
                Log.d("timeStart",startCalender.toString());
                Log.d("timeEnd",endCalender.toString());

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
                try {
                    Date date = startCalender.getTime();
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                    String dateTime = dateFormat.format(date);
                    System.out.println("Current Date Time : " + dateTime);

                    Date date2 = endCalender.getTime();
                    String dateTime2 = dateFormat.format(date2);
                    System.out.println("Current Date Time : " + dateTime2);

                    GetDateFromServer(dateTime,dateTime2);


                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 1963-11-22T18:30:00Z

            }
        });

        PersianCalendar persianCalendar = new PersianCalendar(System.currentTimeMillis() - 24*60*60*1000);
        tvStartDate.setText(persianCalendar.getPersianShortDateTime());
        startCalender.setTimeInMillis(persianCalendar.getTimeInMillis());
        persianCalendar.addPersianDate(Calendar.DAY_OF_MONTH,1);
        tvEndDate.setText(persianCalendar.getPersianShortDateTime());
        endCalender.setTimeInMillis(persianCalendar.getTimeInMillis());
        tvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowTimePicker(1);
            }
        });

        tvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowTimePicker(2);
            }
        });




        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(timeDialogView);
        dialog.setCancelable(true);

        WindowManager.LayoutParams  lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void GetDateFromServer(final String time1, final String time2)
    {
        final Dialog dl = Custom_progress.showCustomprogress(NewDashboardActivity.this);
        dl.setCancelable(false);
        dl.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //http://gps.jupin.ir/api/devices   "positionId":775891
                //http://gps.jupin.ir/api/positions?id=775891
                Pref prf = new Pref(NewDashboardActivity.this);//admin ; hamed@1748
                //  String url = "http://gps.jupin.ir/api/attributes/computed?deviceId="+selectedDevice.id;
                String url = "http://gps.jupin.ir/api/reports/route?deviceId="+selectedDevice.id+"&from="+time1+"&to="+time2;
                final String data = new Get_class(prf.PrefGetString(Pref.SITE_USERNAME),prf.PrefGetString(Pref.SITE_PASSWORD)).GetUrl(url);
                if (!data.equals("Error"))
                {
                    try
                    {
                        locations.clear();
                        JSONArray jsa = new JSONArray(data);
                        Gson gson = new GsonBuilder().create();
                        if (jsa.length() > 0)
                        {
                            for (int i = 0; i < jsa.length(); i++) {
                                locations.add(gson.fromJson(jsa.getString(i), Locations.class));
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent myint = new Intent(NewDashboardActivity.this,Map_Activity.class);
                                    myint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    myint.putExtra("locations", "hastLocations");
                                    startActivity(myint);
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Custom_toast.showCustomAlertLong(NewDashboardActivity.this,R.string.no_data);
                                }
                            });
                        }

                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Custom_toast.showCustomAlertLong(NewDashboardActivity.this,R.string.error_in_get_data);
                            }
                        });
                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Custom_toast.showCustomAlertLong(NewDashboardActivity.this,R.string.error_in_get_data);
                        }
                    });

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dl.dismiss();
                    }
                });
            }
        }).start();



    }
    private void ShowTimePicker(int i)
    {

        PersianCalendar persianCalendar = new PersianCalendar(System.currentTimeMillis());
        final TimePickerDialog timePicker = TimePickerDialog.newInstance(NewDashboardActivity.this, persianCalendar.get(Calendar.HOUR_OF_DAY),persianCalendar.get(Calendar.MINUTE),true);

        timePicker.show(getFragmentManager(), "time"+i);

        timePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                NewDashboardActivity.this,
                persianCalendar.getPersianYear(),
                persianCalendar.getPersianMonth(),
                persianCalendar.getPersianDay()
        );

        datePickerDialog.show(getFragmentManager(), "date"+i);
        datePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                timePicker.dismiss();
            }
        });
    }

    boolean isStartTime = true;
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = "You picked the following date: "+dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        Log.d("date",date);
        PersianCalendar persianCalendar = new PersianCalendar();
        persianCalendar.setPersianDate(year,monthOfYear,dayOfMonth);
        long mili = persianCalendar.getTimeInMillis();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        calendar.setTimeInMillis(mili);
        Log.d("date",calendar.getTime()+"");
        if (view.getTag().equals("date1"))
        {
            isStartTime = true;
            startCalender = calendar;
        }
        else
        {
            isStartTime = false;
            endCalender = calendar;
        }

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        String time = "You picked the following time: "+hourOfDay+"h"+minute;
        Log.d("time",time);//1963-11-22T18:30:00Z
        if (isStartTime)
        {
            startCalender.set(startCalender.get(Calendar.YEAR),startCalender.get(Calendar.MONTH),startCalender.get(Calendar.DATE),hourOfDay,minute);
            TextView tvStartDate = timeDialogView.findViewById(R.id.tvStartDate);
            PersianCalendar prc = new PersianCalendar(startCalender.getTimeInMillis());
            tvStartDate.setText(prc.getPersianShortDateTime());

        }
        else
        {
            endCalender.set(endCalender.get(Calendar.YEAR),endCalender.get(Calendar.MONTH),endCalender.get(Calendar.DATE),hourOfDay,minute);
            TextView tvEndDate = timeDialogView.findViewById(R.id.textEndDate);
            PersianCalendar prc = new PersianCalendar(endCalender.getTimeInMillis());
            tvEndDate.setText(prc.getPersianShortDateTime());
        }
    }


    private void SendMsg(String msg)
    {
        Custom_toast.showCustomAlertLong(NewDashboardActivity.this,R.string.sending_sms);
        try {
            SmsManager sms = SmsManager.getDefault();
            PendingIntent sentPI;
            String SENT = "SMS_SENT";

            sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

            sms.sendTextMessage(new Pref(NewDashboardActivity.this).PrefGetString(Pref.SIMNO), null, msg, sentPI, null);
        }
        catch (Exception ex)
        {
            Uri uri = Uri.parse("smsto:"+new Pref(NewDashboardActivity.this).PrefGetString(Pref.SIMNO));
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", msg);
            startActivity(intent);
        }
    }
}
