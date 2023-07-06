package home.com.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import home.com.Materia.Custom_toast;
import home.com.Materia.Set_font;

public class devSettings extends Activity {


    @Override
    protected void onCreate( Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_settings);


        final String userPass = new Pref(devSettings.this).PrefGetString(Pref.USERNAME).toUpperCase() + " " +
                new Pref(devSettings.this).PrefGetString(Pref.PASSWORD).toUpperCase() + " ";
        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();
        Set_font.setkoodakFont(devSettings.this,godfatherView);

        findViewById(R.id.btdev_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String sms = userPass + "GETVER";
                SendMsg(sms);
            }
        });

        findViewById(R.id.btSetParam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowIdDialog(true);
            }
        });

        findViewById(R.id.btGetparam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowIdDialog(false);
            }
        });

        findViewById(R.id.btReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms = userPass + "CPURESET";
                SendMsg(sms);
            }
        });

        findViewById(R.id.btSimSet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(devSettings.this);
                builder.setTitle("تنظیم سیمکارت");

                String[] animals = {"ایرنسل", "همراه اول", "رایتل", "لغو"};
                builder.setItems(animals, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                String sms = userPass + "SETPARAM 60011:*141*1#;60012:*555*4*3*2#;60040:700;";
                                SendMsg(sms);
                                dialog.dismiss();
                                break;
                            case 1: // cow
                                String sms2 = userPass + "SETPARAM 60011:*140*11#;60012:*198*2#;60040:9990;";
                                SendMsg(sms2);
                                dialog.dismiss();
                                break;
                            case 2: // camel
                                String sms3 = userPass + "SETPARAM 60011:*141*1#;60012:*720*7*3*1#;60040:200;";
                                SendMsg(sms3);
                                dialog.dismiss();
                                break;
                            case 3: // sheep
                                dialog.dismiss();
                            case 4: // goat
                        }
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
            }
        });
    }


    private void ShowIdDialog(final boolean isSet)
    {
        Context contx = devSettings.this;
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
        final TextView tvHint = layout.findViewById(R.id.tvHint);

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
        final String[] dataArray = {getText(R.string.user).toString(), getText(R.string.pass).toString(),getText(R.string.doorInput).toString(),getText(R.string.micGain).toString(), getText(R.string.ledIndicator).toString(), getText(R.string.geofence).toString(),
                getText(R.string.geoRadius).toString(), getText(R.string.geoTime).toString(),
                getText(R.string.geoNumber).toString(),getText(R.string.geofenceAlarm).toString(),getText(R.string.turnOff).toString(),getText(R.string.onlineTime).toString()
        };



        final AppCompatSpinner spinner = layout.findViewById(R.id.spinnerID);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.custom_spinner , dataArray);


        final int[] id = {3003,3004,60005,60041,108,60000,60001,60002,60003,60004,600000,60031};
        layout.findViewById(R.id.btSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms = "";
                sms = new Pref(devSettings.this).PrefGetString(Pref.USERNAME).toUpperCase() + " " +
                        new Pref(devSettings.this).PrefGetString(Pref.PASSWORD).toUpperCase() + " ";
                if (isSet)
                {

                    int selected = spinner.getSelectedItemPosition();
                    if(selected == 10)
                    {
                        sms = sms + "SETDIGOUT "+(swSwitch.isChecked() ? "1" : "0");
                        sms = sms + ";";
                    }
                    else {
                        sms = sms + "SETPARAM ";
                        sms = sms + id[spinner.getSelectedItemPosition()];
                        if (selected == 4 || selected == 5) {
                            sms = sms + " :" + (swSwitch.isChecked() ? "1" : "0");

                        } else {
                            if (selected == 3)
                            {
                                try
                                {
                                    int power = Integer.parseInt(etInput.getText().toString());
                                    if (power>15 || power < 0)
                                    {
                                        Custom_toast.showCustomAlertLong(devSettings.this,R.string.mic_error);
                                        return;
                                    }
                                }
                                catch (Exception ex)
                                {
                                    Custom_toast.showCustomAlertLong(devSettings.this,R.string.mic_error);
                                    return;
                                }

                            }
                            sms = sms + " :" + etInput.getText().toString();
                        }
                        sms = sms + ";";
                    }
                }
                else
                {
                    int selected = spinner.getSelectedItemPosition();
                    if(selected == 10)
                    {
                        sms = sms + "GETIO";
                        sms = sms + ";";
                    }
                    else {

                        sms = sms + "GETPARAM ";
                        sms = sms + id[spinner.getSelectedItemPosition()];
                        sms = sms + ";";
                    }
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
                    if (position == 2 || position == 9 )
                    {
                        ex_dl.dismiss();
                        Show3ButtonDialog(position == 9);

                    }
                    dataView.setVisibility(View.VISIBLE);
                    if (position == 4 || position == 5 || position == 10)
                    {
                        viSwitch.setVisibility(View.VISIBLE);
                        etInput.setVisibility(View.GONE);
                        tvHint.setVisibility(View.GONE);
                        swSwitch.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        viSwitch.setVisibility(View.GONE);
                        swSwitch.setVisibility(View.GONE);
                        etInput.setVisibility(View.VISIBLE);
                        tvHint.setVisibility(View.GONE);
                        if(position == 3)
                        {
                            tvHint.setText(R.string.micPower);
                            tvHint.setVisibility(View.VISIBLE);
                        }

                        etInput.setHint(dataArray[position]);
                        if (position != 0 && position != 1)
                        {
                            etInput.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                        }

                    }
                    if (position == 0)
                    {
                        String user = new Pref(devSettings.this).PrefGetString(Pref.USERNAME);
                        if (user.length() > 0)
                            etInput.setText(user);
                        else
                            etInput.setText("ADM");
                        etInput.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                        etInput.setHint(R.string.user);
                    }
                    else  if (position == 1)
                    {
                        String pass = new Pref(devSettings.this).PrefGetString(Pref.PASSWORD);
                        if (pass.length() > 0)
                            etInput.setText(pass);
                        else
                            etInput.setText("ADM");
                        etInput.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                        etInput.setHint(R.string.pass);
                    }
                    else if (position == 3)
                    {
                        etInput.setText("14");
                        etInput.setHint(R.string.micPower);
                        etInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    }
                    else if (position == 6)
                    {
                        etInput.setText("100");
                        etInput.setHint(R.string.geoRadius);
                        etInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    }
                    else if (position == 7)
                    {
                        etInput.setHint(R.string.geoTime);
                        etInput.setText("7");
                        etInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

                    }
                    else if (position == 8)
                    {
                        etInput.setText("");
                        etInput.setHint("989xxxxxxxxx");
                        etInput.setInputType(InputType.TYPE_CLASS_PHONE);
                    }
                    else if (position == 11)
                    {
                        etInput.setText("");
                        etInput.setHint(R.string.onlineTime);
                        etInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        lp.copyFrom(ex_dl.getWindow().getAttributes());
        lp.width= ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height= ViewGroup.LayoutParams.MATCH_PARENT;


        ex_dl.show();
        ex_dl.getWindow().setAttributes(lp);

    }
    private void Show3ButtonDialog(final Boolean forGeoFenc)
    {
        Context contx = devSettings.this;
        final Dialog ex_dl=new Dialog(contx);
        ex_dl.setCancelable(true);
        ex_dl.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = ex_dl.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.three_botton_dialog,null);
        ex_dl.setContentView(layout);
        layout.findViewById(R.id.btCancell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ex_dl.dismiss();
            }
        });
        Button bt1 = layout.findViewById(R.id.bt1);
        Button bt2 = layout.findViewById(R.id.bt2);
        Button bt3 = layout.findViewById(R.id.bt3);
        if (forGeoFenc)
        {
            bt1.setText(R.string.onlySms);
            bt2.setText(R.string.onlyCall);
            bt3.setText(R.string.smsAndCall);
        }
        else
        {
            bt1.setText(R.string.doorNegative);
            bt2.setText(R.string.doorPositive);
            bt3.setText(R.string.doorSwitchOff);
        }

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms = "";
                sms = new Pref(devSettings.this).PrefGetString(Pref.USERNAME).toUpperCase() + " " +
                        new Pref(devSettings.this).PrefGetString(Pref.PASSWORD).toUpperCase() + " ";
                if (forGeoFenc)
                {
                    sms = sms + "SETPARAM 60004:0;"; //ADM ADM SETPARAM 60004:0,1,2;
                }
                else
                {
                    sms = sms + "SETPARAM 60005:0;";// ADM ADM SETPARAM 60005:0,1,2;
                }
                SendMsg(sms);
                ex_dl.dismiss();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms = "";
                sms = new Pref(devSettings.this).PrefGetString(Pref.USERNAME).toUpperCase() + " " +
                        new Pref(devSettings.this).PrefGetString(Pref.PASSWORD).toUpperCase() + " ";
                if (forGeoFenc)
                {
                    sms = sms + "SETPARAM 60004:1;";
                }
                else
                {
                    sms = sms + "SETPARAM 60005:1;";
                }
                SendMsg(sms);
                ex_dl.dismiss();
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sms = "";
                sms = new Pref(devSettings.this).PrefGetString(Pref.USERNAME).toUpperCase() + " " +
                        new Pref(devSettings.this).PrefGetString(Pref.PASSWORD).toUpperCase() + " ";
                if (forGeoFenc)
                {
                    sms = sms + "SETPARAM 60004:2;";
                }
                else
                {
                    sms = sms + "SETPARAM 60005:2;";
                }
                SendMsg(sms);
                ex_dl.dismiss();
            }
        });


        WindowManager.LayoutParams lp=new WindowManager.LayoutParams();
        lp.copyFrom(ex_dl.getWindow().getAttributes());
        lp.width= ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height= ViewGroup.LayoutParams.MATCH_PARENT;


        ex_dl.show();
        ex_dl.getWindow().setAttributes(lp);
    }

    private void SendMsg(String msg)
    {
        Custom_toast.showCustomAlertLong(devSettings.this,R.string.sending_sms);
        try {
            SmsManager sms = SmsManager.getDefault();
            PendingIntent sentPI;
            String SENT = "SMS_SENT";

            sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);

            sms.sendTextMessage(new Pref(devSettings.this).PrefGetString(Pref.SIMNO), null, msg, sentPI, null);
        }
        catch (Exception ex)
        {
            Uri uri = Uri.parse("smsto:"+new Pref(devSettings.this).PrefGetString(Pref.SIMNO));
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra("sms_body", msg);
            startActivity(intent);
        }






    }
}
