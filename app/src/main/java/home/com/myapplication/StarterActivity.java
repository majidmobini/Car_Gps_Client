package home.com.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import home.com.logger.ExceptionHandler;

public class StarterActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statrt_logo);
        ExceptionHandler.register(getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestSmsPermission();
            }
        },3000);
    }

    private void StartApp()
    {
        if (new Pref(StarterActivity.this).PrefGetBool(Pref.ISCONFIGED))
        {
            startActivity(new Intent(StarterActivity.this,NewDashboardActivity.class));
            finish();
        }
        else
        {
            startActivity(new Intent(StarterActivity.this,SettingsActivity.class));
            finish();
        }
    }

    private void RequestSmsPermission() {
        String permission = Manifest.permission.RECEIVE_SMS;
        String permission2 = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String permission3 = Manifest.permission.INTERNET;
        String permission4 = Manifest.permission.READ_SMS;
        String permission5 = Manifest.permission.SEND_SMS;

        int grant = ContextCompat.checkSelfPermission(this, permission2);
        int grant2 = ContextCompat.checkSelfPermission(this, permission5);
        if ( grant != PackageManager.PERMISSION_GRANTED ||  grant2 != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[5];
            permission_list[0] = permission;
            permission_list[1] = permission2;
            permission_list[2] = permission3;
            permission_list[3] = permission4;
            permission_list[4] = permission5;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        }
        else
        {
            StartApp();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        StartApp();
    }
}
