package home.com.logger;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;

public class ExceptionHandler {
	public static String TAG = "com.nullwire.trace.ExceptionsHandler";

	private static String[] stackTraceFileList = null;

	/**
	 * Register handler for unhandled exceptions.
	 * 
	 * @param context
	 */

	static Context _context;

	private static void fillG(Context context) {
		PackageManager pm = context.getPackageManager();
		_context = context;
		try {
			PackageInfo pi;
			// Version
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			G.APP_VERSION = pi.versionName + "-" + pi.versionCode;
			// Package name
			G.APP_PACKAGE = pi.packageName;
			// Files dir for storing the stack traces
			G.FILES_PATH = context.getFilesDir().getAbsolutePath();
			// Device model
			G.PHONE_MODEL = android.os.Build.MODEL;
			// Android version
			G.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static boolean register(Context context) {
		Log.i(TAG, "Registering default exceptions handler");
		// Get information about the Package
		// PackageManager pm = context.getPackageManager();
		// _context = context;
		// try {
		// PackageInfo pi;
		// // Version
		// pi = pm.getPackageInfo(context.getPackageName(), 0);
		// G.APP_VERSION = pi.versionName + "-" + pi.versionCode;
		// // Package name
		// G.APP_PACKAGE = pi.packageName;
		// // Files dir for storing the stack traces
		// G.FILES_PATH = context.getFilesDir().getAbsolutePath();
		// // Device model
		// G.PHONE_MODEL = android.os.Build.MODEL;
		// // Android version
		// G.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
		// } catch (NameNotFoundException e) {
		// e.printStackTrace();
		// }
		fillG(context);
		Log.i(TAG, "TRACE_VERSION: " + G.TraceVersion);
		Log.d(TAG, "APP_VERSION: " + G.APP_VERSION);
		Log.d(TAG, "APP_PACKAGE: " + G.APP_PACKAGE);
		Log.d(TAG, "FILES_PATH: " + G.FILES_PATH);
		Log.d(TAG, "URL: " + G.URL);

		boolean stackTracesFound = false;
		// We'll return true if any stack traces were found
		if (searchForStackTraces().length > 0) {
			stackTracesFound = true;
		}

		new Thread() {
			@Override
			public void run() {
				// First of all transmit any stack traces that may be lying
				// around
				submitStackTraces();
				UncaughtExceptionHandler currentHandler = Thread
						.getDefaultUncaughtExceptionHandler();
				if (currentHandler != null) {
					Log.d(TAG, "current handler class="
							+ currentHandler.getClass().getName());
				}
				// don't register again if already registered
				if (!(currentHandler instanceof DefaultExceptionHandler)) {
					// Register default exceptions handler
					Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(
							currentHandler));
				}
			}
		}.start();

		return stackTracesFound;
	}

	/**
	 * Register handler for unhandled exceptions.
	 * 
	 * @param context
	 * @param Url
	 */
	public static void register(Context context, String url) {
		Log.i(TAG, "Registering default exceptions handler: " + url);
		// Use custom URL
		G.URL = url;
		// Call the default register method
		register(context);
	}

	/**
	 * Search for stack trace files.
	 * 
	 * @return
	 */
	private static String[] searchForStackTraces() {
		if (stackTraceFileList != null) {
			return stackTraceFileList;
		}
		File dir = new File(G.FILES_PATH + "/");
		// Try to create the files folder if it doesn't exist
		dir.mkdir();
		// Filter for ".stacktrace" files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".stacktrace");
			}
		};
		return (stackTraceFileList = dir.list(filter));
	}

	/**
	 * Look into the files folder to see if there are any "*.stacktrace" files.
	 * If any are present, submit them to the trace server.
	 */
	public static void submitStackTraces() {
		try {
			Log.d(TAG, "Looking for exceptions in: " + G.FILES_PATH);
			String[] list = searchForStackTraces();
			if (list != null && list.length > 0) {
				Log.d(TAG, "Found " + list.length + " stacktrace(s)");
				for (int i = 0; i < list.length; i++) {
					String filePath = G.FILES_PATH + "/" + list[i];
					// Extract the version from the filename:
					// "packagename-version-...."
					String version = list[i].split("-")[0];
					Log.d(TAG, "Stacktrace in file '" + filePath
							+ "' belongs to version " + version);
					// Read contents of stacktrace
					StringBuilder contents = new StringBuilder();
					BufferedReader input = new BufferedReader(new FileReader(
							filePath));
					String line = null;
					String androidVersion = null;
					String phoneModel = null;
					while ((line = input.readLine()) != null) {
						if (androidVersion == null) {
							androidVersion = line;
							continue;
						} else if (phoneModel == null) {
							phoneModel = line;
							continue;
						}
						contents.append(line);
						contents.append(System.getProperty("line.separator"));
					}
					input.close();
					String stacktrace;
					stacktrace = contents.toString();
					Log.d(TAG, "Transmitting stack trace: " + stacktrace);
					// Transmit stack trace with POST request
					/*
					 * DefaultHttpClient httpClient = new DefaultHttpClient();
					 * HttpPost httpPost = new HttpPost(G.URL); List
					 * <NameValuePair> nvps = new ArrayList <NameValuePair>();
					 * nvps.add(new BasicNameValuePair("package_name",
					 * G.APP_PACKAGE)); nvps.add(new
					 * BasicNameValuePair("package_version", version));
					 * nvps.add(new BasicNameValuePair("phone_model",
					 * phoneModel)); nvps.add(new
					 * BasicNameValuePair("android_version", androidVersion));
					 * nvps.add(new BasicNameValuePair("stacktrace",
					 * stacktrace));
					 * 
					 * 
					 * httpPost.setEntity(new UrlEncodedFormEntity(nvps,
					 * HTTP.UTF_8)); // We don't care about the response, so we
					 * just hope it went well and on with it
					 * httpClient.execute(httpPost);
					 */

					G.errorlog = "package_name : "
							+ G.APP_PACKAGE
							+ "\n"
							+ "package_version : "
							+ version
							+ " \n  "
							+ "android_version : "
							+ androidVersion
							+ "\n"
							+ "phoneModel : "
							+ phoneModel
							+ "\n"
							+ "------------------------------------------------\n"
							+ " Stack trace  : \n" + stacktrace;

				}
			} else {
				Log.d(TAG, "not Found any stacktrace ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				String[] list = searchForStackTraces();
				for (int i = 0; i < list.length; i++) {
					File file = new File(G.FILES_PATH + "/" + list[i]);
					file.delete();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
