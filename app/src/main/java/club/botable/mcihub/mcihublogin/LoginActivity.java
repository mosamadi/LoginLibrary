package club.botable.mcihub.mcihublogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.graphics.Typeface.createFromAsset;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;


import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;


public class LoginActivity extends AppCompatActivity {

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    boolean doubleBackToExitPressedOnce = false;

    String[] permissions;



    String smsString = "";

    String IMEI = "";
    String mobileNO = "-1";
    Boolean SUB = false;
    String Device_ID_DB = "-1";
//    Boolean End_State = false;
    String camp_name;
    String sid;
    String welcome_str;
    String farsi_name_str;
    String head_number;
    String sub_url1;
    String sub_url2;
    String FirstActivity;
    String package_name;


    SmsVerifyCatcher smsVerifyCatcher;


    ProgressDialog dialog;

    EditText e_txt;



    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;

    }

    public String get_OTP(String S) {
        if (S.equals("no_text"))
            return "";
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(S);
        while (m.find()) {
            if (m.group().length() == 4)
                return m.group();
        }
        return "";
    }

    public void show_dialog(ProgressDialog _dialog) {
        _dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        _dialog.setIndeterminate(true);
        _dialog.setCancelable(true);
        _dialog.show();
        _dialog.setContentView(R.layout.my_progress);
    }

    public String GetPrefix(Button B) {
        if (B.getTag().equals("send_otp")) {
            return "09";
        }
        return "";
    }

    class state1 implements Runnable {
        CountDownTimer CntDwnTimer;
        TextView lbl1;
        EditText txt1;
        Button btn1;
        TextView lbl2;
        Button btn3;

        private state1(Button _btn1, EditText _txt1, TextView _lbl1, TextView _lbl2, Button _btn3, CountDownTimer _CntDwnTimer) {
            CntDwnTimer = _CntDwnTimer;
            lbl1 = _lbl1;
            txt1 = _txt1;
            btn1 = _btn1;
            lbl2 = _lbl2;
            btn3 = _btn3;
        }

        public void run() {
            btn1.setTag("get_otp");
            txt1.setText("");
            txt1.setHint("کد 4 رقمی را اینجا وارد نمایید");
            txt1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            lbl1.setVisibility(View.VISIBLE);
            lbl2.setVisibility(View.VISIBLE);
            CntDwnTimer.start();
            btn1.setText("تایید و ثبت نهایی");
            String wait = getString(R.string.wait).replace("*", System.getProperty("line.separator"));
            lbl2.setText(wait);
            btn3.setVisibility(View.VISIBLE);
        }
    }

    class state2 implements Runnable {
        CountDownTimer CntDwnTimer;
        TextView lbl1;
        EditText txt1;
        Button btn1;
        Button btn2;
        TextView lbl2;
        TextView lbl3;
        Button btn3;

        private state2(CountDownTimer _CntDwnTimer, TextView _lbl1, EditText _txt1, Button _btn1, Button _btn2, TextView _lbl2, Button _btn3, TextView _lbl3) {
            CntDwnTimer = _CntDwnTimer;
            lbl1 = _lbl1;
            txt1 = _txt1;
            btn1 = _btn1;
            btn2 = _btn2;
            lbl2 = _lbl2;
            btn3 = _btn3;
            lbl3 = _lbl3;
        }

        public void run() {

                start_Main_Activity();
        }
    }


    public void get_state(String imei , String sid, String camp_name){


        if (!(isOnline())) {
            makeText(getApplicationContext(), "لطفا اتصال اینترنت خود را چک نمایید",
                    LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = sub_url1 + "/login_sdk/get_state/"+imei+";"+sid + ";" + camp_name;
        Log.v("salam","url = " +url );

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String state = jsonObject.getString("State");
                            String deviceID = jsonObject.getString("Device_ID");
                            String phone = jsonObject.getString("Phone");
                            Device_ID_DB = deviceID;

                            SharedPreferences userDetails = getApplicationContext().getSharedPreferences("userdetails", MODE_PRIVATE);
                            SharedPreferences.Editor edit = userDetails.edit();
                            edit.putString("Device_ID_DB", deviceID);
                            edit.apply();

                            if(state.equals("unSUB")){
                                e_txt.setText(phone);


                            }else if (state.equals("SUB")){
                                start_Main_Activity();

                                return;
                            }




                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"لطفا چند لحظه ی دیگر مجدد تلاش کنید",LENGTH_SHORT).show();
                        }

                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),"لطفا اینترنت خود را چک کنید و مجدد تلاش کنید...",LENGTH_SHORT).show();
                Log.e("volley error",error.toString());

            }
        });


        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 120000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 70000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        queue.add(stringRequest);

    }


    public void insert_action(String deviceID, String action){

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = sub_url1 + "/login_sdk/insert_action/"+deviceID+";"+action;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });


        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 120000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 70000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        queue.add(stringRequest);


    }
    public void insert_phone(String deviceID , String phone_number){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = sub_url1 +"/login_sdk/insert_phone/"+deviceID+";"+phone_number;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });


        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 120000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 70000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        queue.add(stringRequest);

    }


    public void web_service(final String phone_number, final String token,  final String State, final Runnable func) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "";
        final SharedPreferences userDetails = getApplicationContext().getSharedPreferences("userdetails", MODE_PRIVATE);

        Device_ID_DB = userDetails.getString("Device_ID_DB", "");


        if (!(isOnline())) {
            makeText(getApplicationContext(), "لطفا اتصال اینترنت خود را چک نمایید",
                    LENGTH_SHORT).show();
            return;
        }
        switch (State) {

            case "send_sms":
                insert_action(Device_ID_DB,"send_sms");

                url = sub_url2 +"/pardis_hamavaran/subscription.php?Msisdn=" +
                        phone_number +
                        "&Sid="+sid+"&Action=subscribe";

                break;
            case "send_otp":

                insert_action(Device_ID_DB,"send_otp");

                url = sub_url2 +"/pardis_hamavaran/subscription.php?Msisdn=" +
                        phone_number +
                        "&Sid="+sid+"&Action=subscribe&Token=" +
                        token;

                break;


        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        switch (State) {

                            case "send_sms":
                                return;
                            case "send_otp":
                                if (response.equals("0")) {
                                    dialog.dismiss();
                                    func.run();

                                    SharedPreferences.Editor edit = userDetails.edit();
                                    edit.putString("phone_number", mobileNO);
                                    edit.apply();
                                    insert_phone(Device_ID_DB,mobileNO);
                                } else {
                                    dialog.dismiss();
                                    makeText(getApplicationContext(), "کد تایید وارد شده اشتباه می باشد", Toast.LENGTH_LONG).show();
                                }

                                break;


                        }
                    }
                }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                Log.e("volley error",error.toString());

            }
        });


        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 120000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 70000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        queue.add(stringRequest);
    }

    public void start_Main_Activity(){


//
//        Intent intent = new Intent(this,LoginActivity.class);
//        intent.putExtra("phone_number",mobileNO);
//
//        startActivity(intent);


        Class<?> theClass = null;
        try {
            theClass = Class.forName(FirstActivity);
            Intent intent = new Intent(getApplicationContext(), theClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("signup", "SUB");
            startActivity(intent);
            finish();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }







    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_login);
        e_txt = findViewById(R.id.editText);




        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        try {
            camp_name = getIntent().getExtras().getString("camp_name");
            sid = getIntent().getExtras().getString("sid");
            welcome_str = getIntent().getExtras().getString("welcome");
            farsi_name_str = getIntent().getExtras().getString("farsi_name_str");
            head_number = getIntent().getExtras().getString("head_number");

            sub_url1 = getIntent().getExtras().getString("sub_url1");
            sub_url2 = getIntent().getExtras().getString("sub_url2");

            FirstActivity =  getIntent().getExtras().getString("first_activity");
            package_name =  getIntent().getExtras().getString("package_name");
            FirstActivity = package_name + "." + FirstActivity;









        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Your Intent variable is not complete",LENGTH_SHORT).show();
            Log.v("LoginSDK",e.toString());

        }
//        FirebaseMessaging.getInstance().subscribeToTopic("potential");


        String imei = "";

        permissions =  new String[]{
                Manifest.permission.READ_SMS};


        if (checkPermissions()){
        //  permissions  granted.
        }

        final SharedPreferences userDetails = getApplicationContext().getSharedPreferences("userdetails", MODE_PRIVATE);
        final SharedPreferences.Editor edit = userDetails.edit();
        Device_ID_DB = userDetails.getString("Device_ID_DB", "");
        imei = userDetails.getString("imei", "");
        if (Device_ID_DB.equals("") && imei.equals("")){
            imei = FirebaseInstanceId.getInstance().getId();
            edit.putString("imei",imei);
            edit.apply();
        }
        else if (imei.equals("")){
            imei = FirebaseInstanceId.getInstance().getId();
            edit.putString("imei",imei);
            edit.apply();

            if (imei.equals("")) {
                final String my_code = "" + Build.BOARD.length() + Build.BRAND.length() +
                        Build.CPU_ABI.length() + Build.DEVICE.length() +
                        Build.DISPLAY.length() + Build.HOST.length() +
                        Build.ID.length() + Build.MANUFACTURER.length() +
                        Build.MODEL.length() + Build.PRODUCT.length() +
                        Build.TAGS.length() + Build.TYPE.length() +
                        Build.USER.length();

                imei = my_code;
            }
        }



        final String finalImei = imei;

        Typeface custom_font = createFromAsset(getAssets(), "fonts/B Bardiya.ttf");
        dialog = new ProgressDialog(LoginActivity.this);
        final Button btn1 = findViewById(R.id.button);
        btn1.setTag("send_otp");
        final Button btn2 = findViewById(R.id.button2);
        final Button btn3 = findViewById(R.id.button3);
        final EditText txt1 = findViewById(R.id.editText);
        final TextView lbl1 = findViewById(R.id.lbl3);
        lbl1.setText(farsi_name_str);

        final TextView lbl2 = findViewById(R.id.lbl2);
        final TextView lbl3 = findViewById(R.id.lbl1);

        lbl3.setText(farsi_name_str);
        String wellcome =welcome_str.replace("*", System.getProperty("line.separator"));
        lbl1.setText(wellcome);

        btn1.setTypeface(custom_font);
        btn2.setTypeface(custom_font);
        btn3.setTypeface(custom_font);
        txt1.setTypeface(custom_font);
        lbl1.setTypeface(custom_font);
        lbl2.setTypeface(custom_font);
        lbl3.setTypeface(custom_font);


        Log.v("FirebaseInstanceID",FirebaseInstanceId.getInstance().getId());



        final ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);





        txt1.setText(GetPrefix(btn1));
        Selection.setSelection(txt1.getText(), txt1.getText().length());

        txt1.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith(GetPrefix(btn1))) {
                    txt1.setText(GetPrefix(btn1));
                    Selection.setSelection(txt1.getText(), txt1.getText().length());

                }

            }
        });

        final CountDownTimer CntDwnTimer = new CountDownTimer(180000, 1000) {
            public void onTick(long millisUntilFinished) {
                @SuppressLint("DefaultLocale") String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                lbl1.setTextColor(Color.parseColor("#F09000"));
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) lbl1.getLayoutParams();
                lp.addRule(RelativeLayout.BELOW, lbl2.getId());
                lp.setMargins(0, 0, 0, 100);
                lbl1.setLayoutParams(lp);
                lbl1.setText(hms);
            }

            public void onFinish() {
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) lbl1.getLayoutParams();
                lp.addRule(RelativeLayout.BELOW, lbl3.getId());
                lp.setMargins(0, 0, 0, 100);
                lbl1.setLayoutParams(lp);
                lbl1.setTextColor(Color.parseColor("#FFFFFF"));
                lbl1.setText("در صورت عدم دریافت کد، از دکمه تلاش دوباره استفاده نمایید!!!");
                btn2.setVisibility(View.VISIBLE);
                lbl2.setVisibility(View.INVISIBLE);
            }
        };





        btn1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!isOnline()) {
                    makeText(getApplicationContext(), "لطفا اتصال اینترنت خود را بررسی نمایید.",
                            LENGTH_SHORT).show();
                    return;
                }
                switch (btn1.getTag().toString()) {

                    case "send_otp":
                        if (!(TextUtils.isDigitsOnly(txt1.getText()) && txt1.getText().length() == 11)) {
                            makeText(getApplicationContext(), "فرمت شماره همراه وارد شده صحیح نمی باشد",
                                    LENGTH_SHORT).show();
                            break;
                        }
//                        SmsReceiver.bindListener(new SmsListener() {
//                            @Override
//                            public void messageReceived(String messageText) {
//                                if (btn1.getTag().equals("get_otp")) {
//                                    if (!get_OTP(messageText).equals("")) {
//                                        txt1.setText(get_OTP(messageText));
//                                        activityManager.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_NO_USER_ACTION);
//                                    }
//                                }
//                            }
//                        });






                        mobileNO = "98" + txt1.getText().toString().substring(1);
                        get_state(finalImei,sid,camp_name);


                        web_service(mobileNO, "", "send_sms", null);

                        new state1(btn1, txt1, lbl1, lbl2, btn3, CntDwnTimer).run();
                        break;
                    case "get_otp":
                        if (!(TextUtils.isDigitsOnly(txt1.getText()) && txt1.getText().length() == 4)) {
                            makeText(getApplicationContext(), "کد تایید باید یک کد چهار رقمی باشد",
                                    LENGTH_SHORT).show();
                            break;
                        }
                        show_dialog(dialog);
                        web_service(mobileNO, txt1.getText().toString(),  "send_otp", new state2(CntDwnTimer, lbl1, txt1, btn1, btn2, lbl2, btn3, lbl3));
                        break;

                }

            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                insert_action(Device_ID_DB,"send_sms_again");

                btn1.setTag("get_otp");
                web_service(mobileNO, "",  "send_sms", null);
                lbl1.setVisibility(View.VISIBLE);
                lbl2.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.INVISIBLE);
                CntDwnTimer.cancel();
                CntDwnTimer.start();
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(View v) {
                insert_action(Device_ID_DB,"edit_phone_number");

                btn1.setTag("send_otp");
                btn1.setText("تایید و رفتن به مرحله بعد");
                lbl1.setTextColor(Color.parseColor("#FFFFFF"));
                txt1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                txt1.setText("0" + mobileNO.substring(2));
                txt1.setSelection(txt1.getText().length());

                lbl1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) lbl1.getLayoutParams();
                lp.addRule(RelativeLayout.BELOW, lbl3.getId());
                lp.setMargins(0, 100, 0, 0);
                lbl1.setLayoutParams(lp);
                String wellcome = welcome_str.replace("*", System.getProperty("line.separator"));
                lbl1.setText(wellcome);

                lbl2.setVisibility(View.INVISIBLE);
                btn2.setVisibility(View.INVISIBLE);
                btn3.setVisibility(View.INVISIBLE);
                CntDwnTimer.cancel();
            }
        });



        get_state(finalImei,sid,camp_name);



        smsVerifyCatcher = new SmsVerifyCatcher(LoginActivity.this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                smsString = smsString  + message;

                String code = parseCode(smsString);//Parse verification code


                txt1.setText(code);

                assert activityManager != null;
                activityManager.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_NO_USER_ACTION);
            }
        });
        smsVerifyCatcher.setPhoneNumberFilter(head_number);
        smsVerifyCatcher.onStart();







    }



    protected void onResume() {
        super.onResume();



    }






    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {
            result = ContextCompat.checkSelfPermission(this.getApplicationContext(),p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),MULTIPLE_PERMISSIONS );
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if (grantResults.length > 0) {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                            permissionsDenied += "\n" + per;

                        }

                    }
                    // Show permissionsDenied

                }
                return;
            }
        }
    }


    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
    private String parseCode(String message) {

        return get_OTP(message);
    }

    @Override
    protected void onStart() {
        super.onStart();
        smsVerifyCatcher.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
//        smsVerifyCatcher.onStop();

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {


            try {
                Class<?> theClass = Class.forName(FirstActivity);
                Intent intent = new Intent(getApplicationContext(), theClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("signup", "Exit");
                startActivity(intent);
                finish();


            } catch (ClassNotFoundException e) {

                e.printStackTrace();
            }






        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
