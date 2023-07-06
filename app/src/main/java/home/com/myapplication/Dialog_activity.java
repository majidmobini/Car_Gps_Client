package home.com.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import home.com.Materia.Set_font;

public class Dialog_activity extends Activity {
	MediaPlayer player;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		final Dialog dialog=new Dialog(Dialog_activity.this );
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.msg_dialog,null);
		Set_font.setkoodakFont(Dialog_activity.this, layout);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		TextView body=(TextView) layout.findViewById(R.id.tv_body);
		TextView from_tv=(TextView) layout.findViewById(R.id.tv_from);
		TextView serial=(TextView) layout.findViewById(R.id.tv_serial);
		TextView name=(TextView) layout.findViewById(R.id.tv_name);
		///name.setText(new Pref(Dialog_activity.this).PrefGetString(Pref.USERNAME));
		///serial.setText(getString(R.string.show_device_serial)+getIntent().getStringExtra("serial"));
		body.setText(getIntent().getStringExtra("body"));
		name.setText(getIntent().getStringExtra("from"));
		dialog.setContentView(layout);
		dialog.setCancelable(false);
		Button bt= layout.findViewById(R.id.bt_ok);
		bt.setOnClickListener(new OnClickListener() 
		{

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				onBackPressed();

			}
		});

		WindowManager.LayoutParams  lp=new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width= WindowManager.LayoutParams.MATCH_PARENT;
		lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
		dialog.show();
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setGravity(Gravity.CENTER);
		/*
		String msgBody = body.getText().toString();
		if(msgBody.contains("Khodro az park fence kharej shod") || msgBody.contains("Charger ghat shod")
				|| msgBody.contains("Switch roshan shod") ||msgBody.contains("Dar baz shod") )
		{
			try {
				try {
					AssetFileDescriptor afd = getAssets().openFd("Sound.mp3");
					player = new MediaPlayer();
					player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
					player.prepare();
					player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
						@Override
						public void onPrepared(MediaPlayer mediaPlayer) {
							player.start();
						}
					});

				}
				catch (Exception ex) {
					ex.printStackTrace();
				}

			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}

		}*/


	}

}
