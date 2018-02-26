package Utilities.General;




        import android.annotation.TargetApi;
        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.drawable.ColorDrawable;
        import android.net.Uri;
        import android.os.Build;
        import android.preference.PreferenceManager;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.inputmethod.InputMethodManager;
        import android.webkit.WebResourceRequest;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.Toast;
        import org.json.JSONException;
        import org.json.JSONObject;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import com.ican.ilkercan.bumba.R;


public class UtilityFunctions {


    public static final String [] monthsEN = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    public static final String [] monthsTR = {"Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran", "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
    public static final String LangTR = "TR";
    public static final String LangEN = "EN";


    // Let this function return false during development.
    public static boolean IsDemo()
    {
        return true;
    }

    public static String CutText(String myVal, int max)
    {
        if(myVal != null)
        {
            if(myVal.length() > max)
                return myVal.substring(0, max - 1) + "...";
            else return myVal;
        }
        else {
            return "";
        }
    }

    public static void hideSoftKeyboard(AppCompatActivity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public File saveBitmapToFile(File file){
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE=75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }


    public static void DisplayUrl(String url, String title, AppCompatActivity activity)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        alert.setTitle(title);

        WebView wv = new WebView(activity);
        wv.loadUrl(url);
        wv.setWebViewClient(new WebViewClient() {

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                final Uri uri = request.getUrl();
                return handleUri(uri);
            }

            private boolean handleUri(final Uri uri) {

                final String host = uri.getHost();
                final String scheme = uri.getScheme();
                return false;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton(activity.getString(R.string.a_close_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        alert.show();
    }


    public static boolean IsUserLoggedIn(AppCompatActivity activity) {

        String value = GetSharedPreferenceValue(activity, Constants.SpKeyValueLoggedIn);

        if (value == null)
            return false;

        if (value.equalsIgnoreCase(Constants.SpKeyValueLoggedInPreferenceValue)) {
            return true;
        } else {
            return false;
        }


    }


    public static String ConvertPhpDateToAndroidDate(String inputDate)
    {

        String[] dateValues = inputDate.split(Constants.DATESEP_PHP);

        int year = Integer.valueOf(dateValues[0]);
        int month = Integer.valueOf(dateValues[1]);
        int day = Integer.valueOf(dateValues[2]);

        return GetDate(year, month, day, false);
    }

    public static String ConvertAndroidDateToPhpDate(String inputDate)
    {
        String[] dateValues = inputDate.split(Constants.DATESEP);

        int day = Integer.valueOf(dateValues[0]);
        int month = Integer.valueOf(dateValues[1]);
        int year = Integer.valueOf(dateValues[2]);

        return GetDate(year, month, day, true);
    }

    public static String GetDate(int year, int month, int day, boolean isPhp)
    {
        if(!isPhp)
            return String.valueOf(day) + Constants.DATESEP + String.valueOf(month) + Constants.DATESEP + String.valueOf(year);
        else
            return
                    String.valueOf(year) + Constants.DATESEP_PHP + String.valueOf(month) + Constants.DATESEP_PHP + String.valueOf(day);
    }

    public static String GetCurrentDate(boolean isPhp)
    {

        // NOTE : Calendar month returns 1 less than the selected month.
        // So add + 1 to get the correct month.


        final java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        int month = c.get(java.util.Calendar.MONTH) + 1;
        int day = c.get(java.util.Calendar.DAY_OF_MONTH);

        return GetDate(year, month, day, isPhp);
    }



    private static void SaveSharedPreferenceValue(SharedPreferences.Editor editor, String key, String value)
    {
        editor.putString(key,value);
        editor.apply();
    }

    public static String [] GetPhpErrorMessage(String iValue) throws JSONException {
        String[] retVal = null;
        JSONObject jsonObject = new JSONObject(iValue);
        if(jsonObject != null)
        {
            retVal = new String[2];
            retVal[0] = jsonObject.getString("status_code");
            retVal[1] = jsonObject.getString("message");
        }
        return retVal;
    }




    public static void clearAllSharedPreferences(Activity activity)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();
        String deviceId = preferences.getString(Constants.SpKeyValueDeviceId, "");
        String deviceToken = preferences.getString(Constants.SpKeyValueDeviceToken, "");
        editor.clear();
        editor.putString(Constants.SpKeyValueDeviceId, deviceId);
        editor.putString(Constants.SpKeyValueDeviceToken, deviceToken);
        editor.putBoolean(Constants.SpKeyIntroWasShown, true);
        editor.commit();

    }
    public String FormatDateForSignup(Date date)
    {
        String format = "yyyy-MM-dd";

        String outputDate = "";

        SimpleDateFormat df_output = new SimpleDateFormat(format, java.util.Locale.getDefault());


        try {

            outputDate = df_output.format(date);

        } catch (Exception e) {
            Log.e("formattedDateFromString", "Exception in formateDateFromstring(): " + e.getMessage());
        }
        return outputDate;
    }

    public static String GetPreference(String key, AppCompatActivity activity)
    {
        SharedPreferences shre = PreferenceManager.getDefaultSharedPreferences(activity);
        return shre.getString(key, "");
    }

    public static String GetDeviceId(AppCompatActivity activity)
    {
        return GetPreference(Constants.SpKeyValueDeviceId, activity);
    }

    public static String GetToken(AppCompatActivity activity)
    {
        return GetPreference(Constants.SpKeyValueToken, activity);
    }

    public static String GetMonthString(String date, String language)
    {
        String returnDate = "";


        String year = date.substring(0,4);
        int month = Integer.valueOf(date.substring(5,7));
        String monthStr = "";
        String day = date.substring(8,10);



        if(language == LangEN)
            monthStr = monthsEN[month - 1];
        else
            monthStr = monthsTR[month - 1];

        returnDate = day + " " + monthStr + " " + year;

        return returnDate;
    }

    public static void ShowMessage(AppCompatActivity activity, String message)
    {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }



    public static void SendEmail(AppCompatActivity activity, String subject, String to, String content)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        intent.putExtra(android.content.Intent.EXTRA_TEXT, content);
        Intent mailer = Intent.createChooser(intent, null);
        activity.startActivity(mailer);
    }

    public static String GetSharedPreferenceValue(AppCompatActivity activity, String keyName)
    {
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return app_preferences.getString(keyName, null);
    }

    public static boolean GetSharedPreferenceValue(AppCompatActivity activity, String keyName, boolean isboolean)
    {
        SharedPreferences app_preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return app_preferences.getBoolean(keyName, false);
    }

    public static void SetSharedPreferenceValue(AppCompatActivity activity, String keyName, String value)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(keyName,value);
        editor.apply();

        editor.commit();
    }

    public static boolean isNameSurnameValid(String namesurname) {

        return namesurname.length() >= Constants.MINIMUM_NAMESURNAME_LENGTH;

    }

    public static boolean isEmailValid(String email) {

        return email.contains("@");

    }

    public static boolean isNameValid(String name) {

        return name.length() >= Constants.MINIMUM_NAME_LENGTH;

    }

    public static boolean isGroupNameValid(String name) {

        return name.length() >= Constants.MINIMUM_GROUP_NAME_LENGTH;

    }


    public static boolean isSurNameValid(String surname) {

        return surname.length() >= Constants.MINIMUM_SURNAME_LENGTH;

    }

    public static boolean isReportTitleValid(String title) {

        return title.length() >= Constants.MINIMUM_REPORT_SUBJECT_LENGTH;

    }

    public static boolean isReportMessageValid(String message) {

        return message.length() >= Constants.MINIMUM_REPORT_MESSAGE_LENGTH;

    }

    public static boolean isPhoneValid(String phone) {

        return phone.length() >= Constants.MINIMUM_PHONE_LENGTH;

    }

    public static boolean isPasswordValid(String password) {

        return password.length() >= Constants.MINIMUM_PASSWORD_LENGTH;

    }


    public static ProgressDialog GetProgressDialog(AppCompatActivity activity)
    {
        ProgressDialog progressDialog = new ProgressDialog(activity, R.style.MyAlertDialogStyle);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        return progressDialog;
    }


    public static void ShowProgressDialog(ProgressDialog progressDialog, boolean show)
    {
        if(show) {
            if(!progressDialog.isShowing()) {
                try {
                    progressDialog.show();
                }catch (Exception e ) {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }


}
