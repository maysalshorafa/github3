package com.pos.leaders.leaderspossystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pos.leaders.leaderspossystem.Backup.CryptoException;
import com.pos.leaders.leaderspossystem.Backup.CryptoUtils;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.SaleDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.ScheduleWorkersDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.UserDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.ZReportDBAdapter;
import com.pos.leaders.leaderspossystem.Models.Order;
import com.pos.leaders.leaderspossystem.Models.Sale;
import com.pos.leaders.leaderspossystem.Models.ScheduleWorkers;
import com.pos.leaders.leaderspossystem.Models.User;
import com.pos.leaders.leaderspossystem.Models.ZReport;
import com.pos.leaders.leaderspossystem.Tools.SESSION;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by KARAM on 19/11/2016.
 */

public class LogInActivity extends Activity implements View.OnClickListener {
    private Button btn_0,btn_1,btn_2,btn_3,btn_4,btn_5,btn_6,btn_7,btn_8,btn_9;
    private EditText et;
    private Button btn_login;
    private UserDBAdapter userDBAdapter;
    private ScheduleWorkersDBAdapter scheduleWorkersDBAdapter;
    private ZReport lastZReport;
    public static final String LEADPOS_MAKE_A_REPORT = "LEADPOS_make_a_report";

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

        SESSION._ORDERS = new ArrayList<Order>();

        scheduleWorkersDBAdapter = new ScheduleWorkersDBAdapter(this);
        userDBAdapter=new UserDBAdapter(this);

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
        btn_login = (Button) findViewById(R.id.loginActivity_btnLogin);
        btn_login.setOnClickListener(this);
        et = (EditText) findViewById(R.id.touchPad_et);
        et.setFocusable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userDBAdapter.open();
        scheduleWorkersDBAdapter.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        userDBAdapter.close();
        scheduleWorkersDBAdapter.close();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_ENTER && event.getAction() == event.ACTION_DOWN) {
            Log.i("pressed key", "Enter");
            login();
        }
        return true;
    }

    private void login(){
        String str=et.getText().toString();
        if(str.equals("")){
            Toast.makeText(this, getResources().getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
        }
        else{
            User user=userDBAdapter.logIn(et.getText().toString());
            if(user==null){
                Toast.makeText(this, getResources().getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
            }
            else{
                // success to log in
                SESSION._USER = new User(user);
                Toast.makeText(getApplicationContext(), "Hello " + user.getFullName() + " !!", Toast.LENGTH_SHORT).show();
                //open main screen
                //// TODO: 01/06/2017 open dashboard screen
                Intent intent = new Intent(getApplicationContext(), DashBoard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                int scheduleID = scheduleWorkersDBAdapter.insertEntry(user.getId());
                SESSION._SCHEDULEWORKERS = new ScheduleWorkers(scheduleID, user.getId(), new Date(), new Date());
                userDBAdapter.close();
                scheduleWorkersDBAdapter.close();

                //// TODO: 12/04/2017 check if AReport is valid
                SaleDBAdapter saleDBAdapter = new SaleDBAdapter(LogInActivity.this);
                saleDBAdapter.open();
                Sale lastSale = saleDBAdapter.getLast();
                saleDBAdapter.close();

                ZReportDBAdapter zReportDBAdapter = new ZReportDBAdapter(LogInActivity.this);
                zReportDBAdapter.open();

                try {
                    lastZReport = zReportDBAdapter.getLastRow();
                    if (lastZReport.getEndSaleId() == lastSale.getId()){
                        intent.putExtra(LEADPOS_MAKE_A_REPORT, LEADPOS_MAKE_A_REPORT);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
                et.setText(et.getText() + "0");
                break;
            case R.id.touchPad_bt1:
                et.setText(et.getText() + "1");
                break;
            case R.id.touchPad_bt2:
                et.setText(et.getText() + "2");
                break;
            case R.id.touchPad_bt3:
                et.setText(et.getText() + "3");
                break;
            case R.id.touchPad_bt4:
                et.setText(et.getText() + "4");
                break;
            case R.id.touchPad_bt5:
                et.setText(et.getText() + "5");
                break;
            case R.id.touchPad_bt6:
                et.setText(et.getText() + "6");
                break;
            case R.id.touchPad_bt7:
                et.setText(et.getText() + "7");
                break;
            case R.id.touchPad_bt8:
                et.setText(et.getText() + "8");
                break;
            case R.id.touchPad_bt9:
                et.setText(et.getText() + "9");
                break;
            case R.id.loginActivity_btnLogin:
                login();
                break;
        }
    }
}
