package home.com.logger;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Error_log {
	
	public  static void InsertLog(String message, StackTraceElement[] elements,
			String tag) {

		try {

			String StackTrace = "";

			if (elements != null) {
				for (int i = 0; i < elements.length; i++) {
					StackTrace += elements[i].getFileName() + " , "
							+ elements[i].getClassName() + " , "
							+ elements[i].getMethodName() + " , line "
							+ elements[i].getLineNumber();
					StackTrace += "\n";
				}

			} else
				StackTrace = " StackTrace  elementArray is null ";



		} catch (Exception e) {
			e.printStackTrace();

		}

	}

	public static void InsertLog(Exception e, StackTraceElement[] elements, String tag) {

		try {

			String StackTrace = "";

			if (elements != null) {
				for (int i = 0; i < elements.length; i++) {
					StackTrace += elements[i].getFileName() + " , "
							+ elements[i].getClassName() + " , "
							+ elements[i].getMethodName() + " , line "
							+ elements[i].getLineNumber();
					StackTrace += "\n";
				}

			} else
				
				StackTrace = " StackTrace  elementArray is null ";



			

			 
		} catch (Exception t) {
			t.printStackTrace();

		}

	}

	private static String getmessage(String msg) {
		if (msg == null)
			msg = " message is null ";
		if (TextUtils.isEmpty(msg))
			msg = " message is empty ";
		msg = msg.replace("'", "");

		return msg;
	}

	private static String gettag(String tag) {
		if (tag == null)
			tag = " tag is null ";

		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.US);
		String date= formatter.format(new Date());

		if (date == null)
			date = "";

		tag = date + "---- tag: " + tag;
		tag = tag.replace("'", "");

		return tag;
	}


}
