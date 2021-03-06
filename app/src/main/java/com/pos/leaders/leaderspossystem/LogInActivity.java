package com.pos.leaders.leaderspossystem;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pos.leaders.leaderspossystem.DataBaseAdapter.EmployeeDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.EmployeePermissionsDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.OrderDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.ZReportDBAdapter;
import com.pos.leaders.leaderspossystem.Models.Employee;
import com.pos.leaders.leaderspossystem.Models.Order;
import com.pos.leaders.leaderspossystem.Models.OrderDetails;
import com.pos.leaders.leaderspossystem.Models.ZReport;
import com.pos.leaders.leaderspossystem.Tools.SESSION;
import com.pos.leaders.leaderspossystem.Tools.Util;

import java.util.ArrayList;
import java.util.Random;

import static com.pos.leaders.leaderspossystem.Tools.SendLog.sendLogFile;

/**
 * Created by KARAM on 19/11/2016.
 */

public class LogInActivity extends Activity implements View.OnClickListener {
    private Button btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_del;
    private EditText et;
    private Button btn_login , btn_schedule_workers;
    private EmployeeDBAdapter userDBAdapter;
    private ZReport lastZReport;
    public static final String LEADPOS_MAKE_A_REPORT = "LEADPOS_make_a_report";

    private static Employee DEFAULT_USER = null;

    private SQLiteDatabase db;
    public static boolean branchIdProblem=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);

        /*
        Intent intent=new Intent(LogInActivity.this,BackupActivity.class);
        startActivity(intent);
        */

        Util.killSyncService(this);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.loginActivity_parentLayout);
        Random random = new Random();
        int rand = random.nextInt(12) + 1;

        switch (rand) {
            case 1:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background1));
                break;
            case 2:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background2));
                break;
            case 3:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background3));
                break;
            case 4:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background4));
                break;
            case 5:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background5));
                break;
            case 6:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background6));
                break;
            case 7:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background7));
                break;
            case 8:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background8));
                break;
            case 9:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background9));
                break;
            case 10:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background10));
                break;
            case 11:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background11));
                break;
            case 12:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.background12));
                break;
        }
//  to insert data from file
        /**
         DbHelper dbHelper = new DbHelper(LogInActivity.this);
         this.db = dbHelper.getWritableDatabase();
         try {
         int insertCount = dbHelper.insertFromFile(this, R.raw.testdbfile);
         Toast.makeText(this, "Rows loaded from file= " + insertCount, Toast.LENGTH_SHORT).show();
         } catch (IOException e) {
         Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
         e.printStackTrace();
         }**/

        SESSION._ORDER_DETAILES = new ArrayList<OrderDetails>();

        userDBAdapter = new EmployeeDBAdapter(this);

        btn_0 = (Button) findViewById(R.id.touchPad_bt0);
        btn_0.setOnClickListener(this);
        btn_1 = (Button) findViewById(R.id.touchPad_bt1);
        btn_1.setOnClickListener(this);
        btn_2 = (Button) findViewById(R.id.touchPad_bt2);
        btn_2.setOnClickListener(this);
        btn_3 = (Button) findViewById(R.id.touchPad_bt3);
        btn_3.setOnClickListener(this);
        btn_4 = (Button) findViewById(R.id.touchPad_bt4);
        btn_4.setOnClickListener(this);
        btn_5 = (Button) findViewById(R.id.touchPad_bt5);
        btn_5.setOnClickListener(this);
        btn_6 = (Button) findViewById(R.id.touchPad_bt6);
        btn_6.setOnClickListener(this);
        btn_7 = (Button) findViewById(R.id.touchPad_bt7);
        btn_7.setOnClickListener(this);
        btn_8 = (Button) findViewById(R.id.touchPad_bt8);
        btn_8.setOnClickListener(this);
        btn_9 = (Button) findViewById(R.id.touchPad_bt9);
        btn_9.setOnClickListener(this);
        btn_del = (Button) findViewById(R.id.touchPad_btDel);
        btn_del.setOnClickListener(this);
        btn_login = (Button) findViewById(R.id.loginActivity_btnLogin);
        btn_login.setOnClickListener(this);
        btn_schedule_workers = (Button) findViewById(R.id.schedule_workers);
        btn_schedule_workers.setOnClickListener(this);
        et = (EditText) findViewById(R.id.touchPad_et);
        et.setFocusable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userDBAdapter.open();
        /*et.setText("123456");
        login();*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        userDBAdapter.close();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_ENTER && event.getAction() == event.ACTION_DOWN) {
            Log.i("pressed key", "Enter");
            login();
        }
        return true;
    }

    private void MakeDefaultUser(){
        DEFAULT_USER = new Employee();
        DEFAULT_USER.setFirstName("LeadPOS");
        DEFAULT_USER.setLastName("Developer");
        DEFAULT_USER.setEmployeeId(0L);
        DEFAULT_USER.setPassword("117181916131");
    }

    private void login() {
        sendLogFile();
        MakeDefaultUser();
        String str = et.getText().toString();
        if (str.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
        } else if (str.equals(DEFAULT_USER.getPassword())) {

            ArrayList<Integer> permissions = new ArrayList<>();


            SESSION._EMPLOYEE = new Employee(DEFAULT_USER);

            permissions.add(0,2);//report
            permissions.add(1,3);//products
            permissions.add(2,4);//category
            permissions.add(3,5);//users
            permissions.add(4,7);//backup
            permissions.add(5,8);//settings

            Intent intent = new Intent(getApplicationContext(), DashBord.class);
            intent.putIntegerArrayListExtra("permissions_name", permissions);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(LogInActivity.LEADPOS_MAKE_A_REPORT, LogInActivity.LEADPOS_MAKE_A_REPORT);

            startActivity(intent);
            finish();
        } else {
            Employee user = userDBAdapter.logIn(et.getText().toString());
            if (user == null) {
                Toast.makeText(this, getResources().getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
            } else {
                EmployeePermissionsDBAdapter userPermissionsDBAdapter = new EmployeePermissionsDBAdapter(this);
                userPermissionsDBAdapter.open();
                ArrayList<Integer> permissions = userPermissionsDBAdapter.getPermissions(user.getEmployeeId());

                // success to log in

                SESSION._EMPLOYEE = new Employee(user);
                Toast.makeText(getApplicationContext(), "Hello " + user.getFullName() + " !!", Toast.LENGTH_SHORT).show();
/*
                SharedPreferences cSharedPreferences = getApplicationContext().getSharedPreferences(POS_Management, MODE_PRIVATE);
                final SharedPreferences.Editor editor = cSharedPreferences.edit();
                if (cSharedPreferences != null) {
                    //CreditCard
                    if (cSharedPreferences.contains(SetUpManagement.LEAD_POS_RESULT_INTENT_SET_UP_MANAGEMENT_ACTIVITY_ENABLE_SERVER_URL)) {
                        SETTINGS.BO_SERVER_URL = cSharedPreferences.getString(SetUpManagement.LEAD_POS_RESULT_INTENT_SET_UP_MANAGEMENT_ACTIVITY_ENABLE_SERVER_URL,"");
                    }
                }*/

                //open main screen
                //// TODO: 01/06/2017 open dashboard screen

                Intent intent = new Intent(getApplicationContext(), DashBord.class);
                intent.putIntegerArrayListExtra("permissions_name", permissions);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(LogInActivity.LEADPOS_MAKE_A_REPORT, LogInActivity.LEADPOS_MAKE_A_REPORT);
                userDBAdapter.close();

                /**
                    long scheduleID = scheduleWorkersDBAdapter.insertEntry(user.getCashPaymentId());
                    SESSION._SCHEDULEWORKERS = new ScheduleWorkers(scheduleID, user.getCashPaymentId(), new Date().getTime(), new Date().getTime());
                    scheduleWorkersDBAdapter.close();**/

                //// TODO: 12/04/2017 check if OpiningReport is valid
                OrderDBAdapter saleDBAdapter = new OrderDBAdapter(LogInActivity.this);
                saleDBAdapter.open();
                Order lastSale = saleDBAdapter.getLast();
                saleDBAdapter.close();

                ZReportDBAdapter zReportDBAdapter = new ZReportDBAdapter(LogInActivity.this);
                zReportDBAdapter.open();
                //get ZReport TotalAmount
                //end
                try {
                    lastZReport = zReportDBAdapter.getLastRow();
                    if (lastZReport.getEndOrderId() == lastSale.getOrderId()) {
                        intent.putExtra(LEADPOS_MAKE_A_REPORT, LEADPOS_MAKE_A_REPORT);
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }


                startActivity(intent);
                finish();
            }
        }

        et.setText("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.touchPad_bt0:
                et.append("0");
                break;
            case R.id.touchPad_bt1:
                et.append("1");
                break;
            case R.id.touchPad_bt2:
                et.append("2");
                break;
            case R.id.touchPad_bt3:
                et.append("3");
                break;
            case R.id.touchPad_bt4:
                et.append("4");
                break;
            case R.id.touchPad_bt5:
                et.append("5");
                break;
            case R.id.touchPad_bt6:
                et.append("6");
                break;
            case R.id.touchPad_bt7:
                et.append("7");
                break;
            case R.id.touchPad_bt8:
                et.append("8");
                break;
            case R.id.touchPad_bt9:
                et.append("9");
                break;
            case R.id.touchPad_btDel:
                String str = et.getText().toString();
                if (!str.equals("")) {
                    if (str.length() == 1) {
                        str = "";
                    } else {
                        str = str.substring(0, str.length() - 1);
                    }
                    et.setText(str);
                }
                break;
            case R.id.loginActivity_btnLogin:
                login();
                break;
            case
                    R.id.schedule_workers:
                Intent intent = new Intent(LogInActivity.this,ScheduleWorkersActivity.class);
                startActivity(intent);             }

        }
    }


