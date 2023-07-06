package home.com.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import home.com.Materia.Set_font;

public class SettingsActivity extends Activity {
    TextInputEditText etSimNo;
    TextInputEditText etPass;
    TextInputEditText etUser;
    TextInputEditText etSiteUser;
    TextInputEditText etSitePass;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        ViewGroup godfatherView = (ViewGroup)this.getWindow().getDecorView();
        Set_font.setkoodakFont(SettingsActivity.this, godfatherView);
        etPass = findViewById(R.id.etPassword);
        etUser = findViewById(R.id.etUsername);
        etSimNo = findViewById(R.id.etSimNo);
        etSitePass = findViewById(R.id.etSitePassword);
        etSiteUser = findViewById(R.id.etSiteUser);

        etSitePass.setText(new Pref(SettingsActivity.this).PrefGetString(Pref.SITE_PASSWORD));
        etSiteUser.setText(new Pref(SettingsActivity.this).PrefGetString(Pref.SITE_USERNAME));

        etPass.setText(new Pref(SettingsActivity.this).PrefGetString(Pref.PASSWORD));
        etUser.setText(new Pref(SettingsActivity.this).PrefGetString(Pref.USERNAME));
        etSimNo.setText(new Pref(SettingsActivity.this).PrefGetString(Pref.SIMNO));
        findViewById(R.id.btSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                etSimNo.setError(null);
                etPass.setError(null);
                etUser.setError(null);
                if (etSimNo.getText().toString().isEmpty())
                {
                    etSimNo.setError(getText(R.string.fill_the_field));
                    return;
                }
                if (etPass.getText().toString().isEmpty())
                {
                    etPass.setError(getText(R.string.fill_the_field));
                    return;
                }
                if (etUser.getText().toString().isEmpty())
                {
                    etUser.setError(getText(R.string.fill_the_field));
                    return;
                }

                new Pref(SettingsActivity.this).PrefSetString(Pref.SITE_USERNAME,etSiteUser.getText().toString());
                new Pref(SettingsActivity.this).PrefSetString(Pref.SITE_PASSWORD,etSitePass.getText().toString());

                new Pref(SettingsActivity.this).PrefSetString(Pref.USERNAME,etUser.getText().toString());
                new Pref(SettingsActivity.this).PrefSetString(Pref.PASSWORD,etPass.getText().toString());
                new Pref(SettingsActivity.this).PrefSetString(Pref.SIMNO,etSimNo.getText().toString());
                new Pref(SettingsActivity.this).PrefSetBool(Pref.ISCONFIGED,true);
                startActivity(new Intent(SettingsActivity.this,NewDashboardActivity.class));
            }
        });
    }
}
