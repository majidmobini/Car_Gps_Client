package home.com.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.Nullable;
import home.com.Classes.Locations;
import home.com.Materia.Set_font;

import static home.com.Classes.Locations.locations;

public class Map_Activity extends Activity {

    private MapView mapView;
    String location;
    private IMapController mMapController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);

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

        if (getIntent().hasExtra("locations"))
        {
            try {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ShowDataOnMap();
                    }
                },500);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        else {
            final String data = (getIntent().getStringExtra("body"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    InfoWindow.closeAllInfoWindowsOn(mapView);
                    String lat = "0";
                    String lng = "0";
                    if (data.split("Lat:").length > 0 && data.split("Long:").length >0) {
                        lat = data.split("Lat:")[1];
                        lat = lat.split(",")[0];

                        lng = data.split("Long:")[1];
                        lng = lng.split(",")[0];
                    }

                    SharedPreferences shared = getSharedPreferences("prefs", MODE_PRIVATE);
                    GeoPoint gp = new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lng));
                    // IGeoPoint igp = new GeoPoint(latlon[0],latlon[1]);
                    Drawable nodeIcon = getResources().getDrawable(R.drawable.pointer);
                    Marker nodeMarker = new Marker(
                            mapView);
                    nodeMarker.setPosition(gp);
                    nodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                   //// nodeMarker.setIcon(nodeIcon);
                    nodeMarker.setTitle(data);
                  //  mMapController.setCenter(gp);
                    mMapController.animateTo(gp,14.0,1L);
                   // mMapController.setZoom(16.0);
                    mapView.getOverlays().add(nodeMarker);
                    // GeoPoint gp=new GeoPoint(latlon[0],latlon[1]);
                    // waypoints.add(gp);

                    mapView.invalidate();

                    //  mMapController.setCenter(gp);
                    //mMapController.setZoom(12);

                }
            }, 500);
        }


    }

    private void ShowDataOnMap()
    {

        ActivityManager actManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        long totalMemory = memInfo.totalMem/(1024*1024*1024);
        int startFrom = 0;
        if (totalMemory < 2.7 )
        {
            if( locations.size() > 500) {
                startFrom = locations.size() - 500;
                ShowWarningDialog();
            }

        }
        if (totalMemory < 3)
        {
            Configuration.getInstance().setCacheMapTileCount((short)12);
            Configuration.getInstance().setCacheMapTileOvershoot((short)12);
        }


        Polyline polyline = new Polyline();
        DecimalFormat dcf = new DecimalFormat("#.##");
        for ( int i = startFrom ; i<locations.size();i++)
        {
            Locations loc = locations.get(i);
            GeoPoint gp = new GeoPoint(loc.latitude, loc.longitude);

            Drawable nodeIcon = getResources().getDrawable(R.drawable.direction0);
            if (loc.course < 10)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction0);
            }
            else if (loc.course < 20)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction10);
            }
            else if (loc.course < 30)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction20);
            }
            else if (loc.course < 40)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction30);
            }
            else if (loc.course < 50)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction40);
            }
            else if (loc.course < 60)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction50);
            }
            else if (loc.course < 70)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction60);
            }
            else if (loc.course < 80)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction70);
            }
            else if (loc.course < 90)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction80);
            }
            else if (loc.course < 100)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction90);
            }
            else if (loc.course < 110)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction100);
            }
            else if (loc.course < 120)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction110);
            }
            else if (loc.course < 130)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction120);
            }
            else if (loc.course < 140)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction130);
            }
            else if (loc.course < 150)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction140);
            }
            else if (loc.course < 160)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction150);
            }
            else if (loc.course < 170)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction160);
            }
            else if (loc.course < 180)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction170);
            }
            else if (loc.course < 190)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction180);
            }
            else if (loc.course < 200)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction190);
            }
            else if (loc.course < 210)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction200);
            }
            else if (loc.course < 220)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction210);
            }
            else if (loc.course < 230)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction220);
            }
            else if (loc.course < 240)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction230);
            }
            else if (loc.course < 250)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction240);
            }
            else if (loc.course < 260)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction250);
            }
            else if (loc.course < 270)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction260);
            }
            else if (loc.course < 280)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction270);
            }
            else if (loc.course < 290)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction280);
            }
            else if (loc.course < 300)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction290);
            }
            else if (loc.course < 310)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction300);
            }
            else if (loc.course < 320)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction310);
            }
            else if (loc.course < 330)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction320);
            }
            else if (loc.course < 340)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction330);
            }
            else if (loc.course < 350)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction340);
            }
            else if (loc.course < 360)
            {
                nodeIcon = getResources().getDrawable(R.drawable.direction350);
            }


           /// nodeIcon = getResources().getDrawable(R.drawable.direct);

            Marker nodeMarker = new Marker(mapView);

            polyline.addPoint(gp);
            polyline.setColor(Color.BLUE);
            polyline.setWidth(4);
            nodeMarker.setPosition(gp);
            nodeMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
            nodeMarker.setImage(nodeIcon);
            Drawable nodeIcon2 = getResources().getDrawable(R.drawable.direct_n);
            nodeMarker.setIcon(nodeIcon2);

            nodeMarker.setRotation(-loc.course + 180);
            StringBuilder stb = new StringBuilder();
            stb.append("طول: ");
            stb.append(loc.longitude+"\n");
            stb.append("عرض: ");
            stb.append(loc.latitude+"\n");
            stb.append("سرعت: ");
            double spd = loc.speed * 1.852;

            stb.append( dcf.format(spd) +"\n");
            stb.append("جهت: ");
            stb.append(loc.course+"\n");
            stb.append("زمان: ");
            //2020-01-14T09:53:12.000+0000
            String dtStart = loc.fixTime.split("\\.")[0]+"Z";
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
            Log.d("time",dtStart);
            nodeMarker.setTitle(stb.toString());
            mapView.getOverlays().add(nodeMarker);
        }
        int mid = locations.size()/2;

        Locations loc = locations.get(mid);
        GeoPoint gp = new GeoPoint(loc.latitude, loc.longitude);
       // mMapController.setCenter(gp);
        mMapController.animateTo(gp,14.0,1L);
      //  mMapController.setZoom(14.0);
        mapView.getOverlays().add(polyline);
        mapView.invalidate();
    }

    void ShowWarningDialog()
    {
        final Dialog dialog=new Dialog(Map_Activity.this );
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.msg_dialog,null);
        Set_font.setkoodakFont(Map_Activity.this, layout);

        TextView body=(TextView) layout.findViewById(R.id.tv_body);
        TextView from_tv=(TextView) layout.findViewById(R.id.tv_from);
        TextView serial=(TextView) layout.findViewById(R.id.tv_serial);
        TextView name=(TextView) layout.findViewById(R.id.tv_name);
        ///name.setText(new Pref(Dialog_activity.this).PrefGetString(Pref.USERNAME));
        ///serial.setText(getString(R.string.show_device_serial)+getIntent().getStringExtra("serial"));
        body.setText(R.string.warningMemory);
        name.setText("");
        dialog.setContentView(layout);
        dialog.setCancelable(false);
        Button bt= layout.findViewById(R.id.bt_ok);
        bt.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v) {
                dialog.dismiss();


            }
        });

        WindowManager.LayoutParams  lp=new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width= WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setGravity(Gravity.CENTER);
    }
}
