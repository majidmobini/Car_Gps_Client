package home.com.Materia;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import home.com.myapplication.R;


public class Custom_toast {
	

	static Toast toast;

	public static void showCustomAlertLong(Context context,int str)
	{
		try {

			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View toastRoot = inflater.inflate(R.layout.toast, null);

			toast = new Toast(context);
			toast.setView(toastRoot);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);

			((TextView) toastRoot.findViewById(R.id.toast_text)).setText(str);
			toast.show();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
