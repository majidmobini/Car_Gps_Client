package home.com.Materia;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import home.com.myapplication.R;


public class Custom_progress {

	
	public static Dialog showCustomprogress( Context context)
	 {
		LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.loading_dialog,null);
	//	ImageView im=(ImageView) layout.findViewById(R.id.im_loading);
		//Animation rotate = AnimationUtils.loadAnimation(context,R.anim.rotate);
		//im.startAnimation(rotate);
		 GifView pGif = (GifView) layout.findViewById(R.id.progressBar);
		 pGif.setImageResource(R.drawable.gps);

		Dialog dialog =new Dialog(context, R.style.DialogSlideAnim);// new Dialog(context);
		Window window = dialog.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(layout);
		dialog.setCancelable(false);
		return dialog;
	         
	    }

}
