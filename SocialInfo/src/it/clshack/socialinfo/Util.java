package it.clshack.socialinfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.view.inputmethod.InputMethodManager;

public class Util {
	public Util() {}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null
				&& cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
				.getWindowToken(), 0);
	}
	public static String getName(String text) {
		Pattern p = Pattern.compile("fsl fwb fcb\\\\\">(.*?)\\\\u003C\\\\");
		Matcher m = p.matcher(text);
		int cont = 0;
		while (m.find()) {
			if (cont > 0) {
				return (m.group(1).toString());
			}
			cont++;
		}
		return null;
	}
	public static String getUrlImg(String text) {
		Pattern p = Pattern.compile("lfloat img\" src=\"(.*?)\" alt=\"\"");
		Matcher m = p.matcher(text);
		if (m.find()) {
			return (m.group(1).toString());
		}
		return null;
	}
	public static String getHtml(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return total.toString();
	}
	
	public static InputStream postData(String email) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"https://www.facebook.com/ajax/login/help/identify.php?ctx=recover");
	    String str1 = "lsd=AVq1pCgt&email=" + email + "&your_name=&friend_name=&did_submit=Cerca&__user=0&__a=1&__req=1&fb_dtsg=AQAHmnjO";

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("lsd", "AVq1pCgt"));
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("your_name", ""));
			nameValuePairs.add(new BasicNameValuePair("friend_name", ""));
			nameValuePairs.add(new BasicNameValuePair("did_submit", "Cerca"));
			nameValuePairs.add(new BasicNameValuePair("__user", "0"));
			nameValuePairs.add(new BasicNameValuePair("__a", "1"));
			nameValuePairs.add(new BasicNameValuePair("__req", "1"));
			nameValuePairs.add(new BasicNameValuePair("fb_dtsg", "AQAHmnjO"));
			nameValuePairs.add(new BasicNameValuePair("phstamp", crackit(str1, "AQAHmnjO")));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			return httpclient.execute(httppost).getEntity().getContent();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	private static String crackit(String paramString1, String paramString2) {
		int i = paramString1.length();
		String str = new String();
		for (int j = 0;; j++) {
			if (j >= paramString2.length())
				return '1' + str + i;
			int k = Character.codePointAt(paramString2, j);
			str = str + k;
		}
	}

}