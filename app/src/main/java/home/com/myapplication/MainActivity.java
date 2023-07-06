package home.com.myapplication;

import android.Manifest;
import android.app.Activity;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.time.RadialPickerLayout;
import com.mohamadamin.persianmaterialdatetimepicker.time.TimePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.json.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import home.com.Classes.Device;
import home.com.Classes.DeviceAdaptor;
import home.com.Classes.Locations;
import home.com.Materia.Custom_progress;
import home.com.Materia.Custom_toast;
import home.com.Materia.Set_font;
import home.com.internet.Get_class;
import home.com.logger.G;

import static home.com.Classes.Locations.locations;

//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends Activity implements View.OnClickListener , TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    String TAG = "MainActivity";
    ArrayList<Device> devices = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setContentView(R);
        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();
        Set_font.setkoodakFont(MainActivity.this,godfatherView);
        findViewById(R.id.btBattery).setOnClickListener(this);
        findViewById(R.id.btGps).setOnClickListener(this);
        findViewById(R.id.btInfo).setOnClickListener(this);
        findViewById(R.id.btOnline).setOnClickListener(this);
        findViewById(R.id.btDevSetting).setOnClickListener(this);
        findViewById(R.id.btDeactivePark).setOnClickListener(this);
        findViewById(R.id.btActivePark).setOnClickListener(this);
        findViewById(R.id.btSetOutPut).setOnClickListener(this);
        findViewById(R.id.btSetting).setOnClickListener(this);
        findViewById(R.id.btStatus).setOnClickListener(this);
        findViewById(R.id.imBtSetting).setOnClickListener(this);
        findViewById(R.id.btGetPath).setOnClickListener(this);
        findViewById(R.id.btCall).setOnClickListener(this);
        writeLog();

    }

    private void writeLog()
    {
       File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File f = new File(directory,"Radyab.log");
        if (f.length()/(1024*1024) > 2)
        {
            f.delete();
            f = new File(directory,"Radyab.log");
        }
        try {
                FileWriter fr = new FileWriter(f, true);
                fr.write(G.errorlog + "\n"+G.ANDROID_VERSION + "\n" + G.PHONE_MODEL + "\n");
                fr.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        String sms = "";
        sms = new Pref(MainActivity.this).PrefGetString(Pref.USERNAME).toUpperCase() + " " +
                new Pref(MainActivity.this).PrefGetString(Pref.PASSWORD).toUpperCase() + " ";
        switch (v.getId())
        {
            case R.id.btGetPath:
               // ShowTimePicker();
                GetDevices();

                break;
            case R.id.btCall:
                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                phoneIntent.setData(Uri.parse("tel:"+new Pref(MainActivity.this).PrefGetString(Pref.SIMNO)));
                if (ActivityCompat.checkSelfPermission(MainActivity.this,
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
            case R.id.btBattery: //BATTERY
                sms = sms + "BATTERY";
                SendMsg(sms);
                break;
            case R.id.btGps://GETGPS
                sms = sms + "GETGPS";
                SendMsg(sms);
                break;
            case R.id.btInfo://GETVER
                sms = sms + "GETVER";
                SendMsg(sms);
                break;
            case R.id.btOnline:
                startActivity(new Intent(MainActivity.this,OnlineActivity.class));
                break;
            case R.id.btDevSetting: ///CPURESET
                startActivity(new Intent(MainActivity.this,devSettings.class));
                break;
/*
            کامند فعال کردن خاموش کننده
            ADM ADM SETDIGOUT 1
            کامند غیرفعال کردن خاموش کننده
            ADM ADM SETDIGOUT 0
                */
            case R.id.btActivePark:  //GETIO
                    sms = sms + "SETPARAM 60000:1;";
                SendMsg(sms);
               /// ShowIdDialog(false);
                break;

            case R.id.btDeactivePark: //SETPARAM
                sms = sms + "SETPARAM 60000:0;";
                SendMsg(sms);
              //  ShowIdDialog(true);
                break;
            case R.id.btSetOutPut: //SETDIGOUT
                ShowIdDialog(true);
                break;
            case R.id.btSetting: //GETPARAM
                ShowIdDialog(false);
                break;
            case R.id.btStatus://GETSTATUS
                sms = sms + "GETSTATUS";
                SendMsg(sms);
                break;
            case R.id.imBtSetting:
                startActivity(new Intent(MainActivity.this,SettingsActivity.class));
                break;
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
              phoneIntent.setData(Uri.parse("tel:"+new Pref(MainActivity.this).PrefGetString(Pref.SIMNO)));
              startActivity(phoneIntent);

          }
       }
    }

    Device selectedDevice;

    private void ShowPathDate(int forday)
    {
        long current = System.currentTimeMillis();
        startCalender.setTimeInMillis(current - (24*60*60*1000)*forday);
        endCalender.setTimeInMillis(current - (24*60*60*1000)*(forday-1));
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
        final Dialog dialog =new Dialog(MainActivity.this);// new Dialog(context);
        LayoutInflater inflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.select_path,null);

        layout.findViewById(R.id.btToday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPathDate(1);
                dialog.dismiss();

            }
        });
        layout.findViewById(R.id.btYesterday).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowPathDate(2);
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
        final Dialog dialog =new Dialog(MainActivity.this);// new Dialog(context);
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

        adapter = new DeviceAdaptor(MainActivity.this, devices,rowClicked);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
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
        final  Pref prf = new Pref(MainActivity.this);
        if (prf.PrefGetString(Pref.SITE_USERNAME).isEmpty() ||prf.PrefGetString(Pref.SITE_PASSWORD).isEmpty())
        {
            Custom_toast.showCustomAlertLong(MainActivity.this,R.string.enter_siteUser);
            return;
        }
        final Dialog dl = Custom_progress.showCustomprogress(MainActivity.this);
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
                            Custom_toast.showCustomAlertLong(MainActivity.this,R.string.error_in_get_data);
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
                                Custom_toast.showCustomAlertLong(MainActivity.this,R.string.error_in_get_data);
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
        final Dialog dialog =new Dialog(MainActivity.this);// new Dialog(context);
        Set_font.setkoodakFont(MainActivity.this,timeDialogView);
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
        final Dialog dl = Custom_progress.showCustomprogress(MainActivity.this);
        dl.setCancelable(false);
        dl.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //http://gps.jupin.ir/api/devices   "positionId":775891
                //http://gps.jupin.ir/api/positions?id=775891
                Pref prf = new Pref(MainActivity.this);//admin ; hamed@1748
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
                                    Intent myint = new Intent(MainActivity.this,Map_Activity.class);
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
                                    Custom_toast.showCustomAlertLong(MainActivity.this,R.string.no_data);
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
                                Custom_toast.showCustomAlertLong(MainActivity.this,R.string.error_in_get_data);
                            }
                        });
                    }
                }
                else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Custom_toast.showCustomAlertLong(MainActivity.this,R.string.error_in_get_data);
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
        final TimePickerDialog timePicker = TimePickerDialog.newInstance(MainActivity.this, persianCalendar.get(Calendar.HOUR_OF_DAY),persianCalendar.get(Calendar.MINUTE),true);

        timePicker.show(getFragmentManager(), "time"+i);

        timePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.d("TimePicker", "Dialog was cancelled");
            }
        });

        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                MainActivity.this,
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


    private void ShowIdDialog(final boolean isSet)
    {
        Context contx = MainActivity.this;
        final Dialog ex_dl=new Dialog(contx);
        ex_dl.setCancelable(true);
        ex_dl.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = ex_dl.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.param_selector,null);

        layout.findViewById(R.id.btCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ex_dl.dismiss();
            }
        });
        final View dataView = layout.findViewById(R.id.dataView);
        final TextInputEditText etInput = layout.findViewById(R.id.etInputText);
        final View viSwitch = layout.findViewById(R.id.viSwitch);
        final TextView tvSwitch = layout.findViewById(R.id.tvSwich);
        final Switch swSwitch = layout.findViewById(R.id.swSwitch);

        if (isSet)
            swSwitch.setVisibility(View.VISIBLE);
        if (swSwitch.isChecked())
        {
            tvSwitch.setText(R.string.on);
        }
        else
        {
            tvSwitch.setText(R.string.off);
        }

        swSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    tvSwitch.setText(R.string.on);
                }
                else
                {
                    tvSwitch.setText(R.string.off);
                }

            }
        });

     //   com.material.Set_font.setkoodakFont(contx, layout);
        ex_dl.setContentView(layout);
        final String[] dataArray = {getText(R.string.user).toString(), getText(R.string.pass).toString(),
                getText(R.string.autorized_number).toString(), getText(R.string.ledIndicator).toString(), getText(R.string.geofence).toString(),
                getText(R.string.geoRadius).toString(), getText(R.string.geoTime).toString(),
                getText(R.string.geoNumber).toString()
        };
        final AppCompatSpinner spinner = layout.findViewById(R.id.spinnerID);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item , dataArray);

        final int[] id = {3003,3004,4000,108,60000,60001,60002,60003};
        layout.findViewById(R.id.btSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms = "";
                sms = new Pref(MainActivity.this).PrefGetString(Pref.USERNAME).toUpperCase() + " " +
                        new Pref(MainActivity.this).PrefGetString(Pref.PASSWORD).toUpperCase() + " ";
                if (isSet)
                {
                    int selected = spinner.getSelectedItemPosition();
                    sms = sms + "SETPARAM ";
                    sms = sms + id[spinner.getSelectedItemPosition()];
                    if (selected == 3 || selected == 4)
                    {
                        sms = sms + " :" + (swSwitch.isChecked()?"1":"0");

                    }
                    else
                    {
                        sms = sms + " :" + etInput.getText().toString();
                    }
                    sms = sms + ";";
                }
                else
                {
                    int selected = spinner.getSelectedItemPosition();
                    sms = sms + "GETPARAM ";
                    sms = sms + id[spinner.getSelectedItemPosition()];
                }
                SendMsg(sms);
            }
        });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSet)
                {
                    dataView.setVisibility(View.VISIBLE);
                    if (position == 3 || position == 4)
                    {
                        viSwitch.setVisibility(View.VISIBLE);
                        etInput.setVisibility(View.GONE);
                    }
                    else
                    {
                        viSwitch.setVisibility(View.GONE);
                        etInput.setVisibility(View.VISIBLE);
                        etInput.setHint(dataArray[position]);
                        if (position != 0 && position != 1)
                        {
                            etInput.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        LayoutParams lp=new LayoutParams();
        lp.copyFrom(ex_dl.getWindow().getAttributes());
        lp.width= ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height= ViewGroup.LayoutParams.MATCH_PARENT;


        ex_dl.show();
        ex_dl.getWindow().setAttributes(lp);

    }
    private void SendMsg(String msg)
    {
        Custom_toast.showCustomAlertLong(MainActivity.this,R.string.sending_sms);
        try {
            SmsManager sms = SmsManager.getDefault();
            PendingIntent sentPI;
            String SENT = "SMS_SENT";

            sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

            sms.sendTextMessage(new Pref(MainActivity.this).PrefGetString(Pref.SIMNO), null, msg, sentPI, null);
        }
        catch (Exception ex)
        {
            Uri uri = Uri.parse("smsto:"+new Pref(MainActivity.this).PrefGetString(Pref.SIMNO));
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", msg);
            startActivity(intent);
        }






    }

   /* void GetFCMToken()
    {
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg =token;// getString(, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
        // [END retrieve_current_token]
    }*/



}
