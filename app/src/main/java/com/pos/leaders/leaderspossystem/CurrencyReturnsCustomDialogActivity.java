package com.pos.leaders.leaderspossystem;

import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.pos.leaders.leaderspossystem.DataBaseAdapter.Currency.CurrencyReturnsDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.Currency.CurrencyTypeDBAdapter;

import com.pos.leaders.leaderspossystem.DataBaseAdapter.Currency.CurrencyDBAdapter;
import com.pos.leaders.leaderspossystem.Models.Currency.CurrencyType;
import com.pos.leaders.leaderspossystem.Models.Currency.Currency;

import com.pos.leaders.leaderspossystem.Tools.DateConverter;

import com.pos.leaders.leaderspossystem.Tools.SESSION;
import com.pos.leaders.leaderspossystem.Tools.TempCashActivty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CurrencyReturnsCustomDialogActivity extends Dialog implements  View.OnClickListener, AdapterView.OnItemSelectedListener {


    public Activity c;
    public Dialog d;
    public Button done, cancel;
    TextView tvExcess;
   String valueForReturnValue;
    Spinner returnSpener;
    double template=0;
    long returnSpenerId=0;
    private List<CurrencyType> currencyTypesList=null;
    CurrencyReturnsDBAdapter currencyReturnsDBAdapter=new CurrencyReturnsDBAdapter(getContext());
    CurrencyTypeDBAdapter currencyTypeDBAdapter = new CurrencyTypeDBAdapter(getContext());
    public CurrencyReturnsCustomDialogActivity(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        currencyReturnsDBAdapter.open();
        done = (Button) findViewById(R.id.btn_done);
        cancel = (Button) findViewById(R.id.btn_cancel);
        tvExcess = (TextView) findViewById(R.id.cashActivity_TVExcess);
        returnSpener = (Spinner) findViewById(R.id.spinnerForReturnValue);
        currencyTypeDBAdapter.open();
        done.setOnClickListener(this);
        cancel.setOnClickListener(this);
        tvExcess.setOnClickListener(this);
        returnSpener.setOnItemSelectedListener(this);

        currencyTypesList = currencyTypeDBAdapter.getAllCurrencyType();
        CurrencyDBAdapter currencyDBAdapter = new CurrencyDBAdapter(getContext());
        currencyDBAdapter.open();
        final List<Currency> currencyList = currencyDBAdapter.getAllCurrency(currencyTypesList);

        final List<String> currency = new ArrayList<String>();
        for (int i = 0; i < currencyTypesList.size(); i++) {
            currency.add(currencyTypesList.get(i).getType());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, currency);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        returnSpener.setAdapter(dataAdapter);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        template = preferences.getFloat(TempCashActivty.LEAD_POS_RESULT_INTENT_CODE_Temp_CASH_ACTIVITY_EXCESSVALUE, 0);
        tvExcess.setText(String.format(new Locale("en"), "%.2f", template));
        valueForReturnValue = String.valueOf(template);

        returnSpener.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                Currency currency=new Currency(getContext());
                for (int i=0 ;i<=currencyList.size()-1;i++){
                    if(currencyList.get(i).getName().equals(returnSpener.getSelectedItem().toString())){
                        currency = currencyList.get(i);
                    }
                }
                returnSpenerId = returnSpener.getSelectedItemId();
                template = (double) (Double.parseDouble(valueForReturnValue) / currency.getRate());
                tvExcess.setText(String.format(new Locale("en"), "%.2f", template));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done:
                currencyReturnsDBAdapter.insertEntry(SESSION._SALE.getId(),template , new Date(), returnSpenerId);
                currencyReturnsDBAdapter.close();
                c.finish();
                break;
            case R.id.btn_cancel:
                Intent i;
                i = new Intent(getContext(), TempCashActivty.class);
                c.startActivity(i);
                break;
            default:
                break;
        }
        dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
      //  if(keyCode==KeyEvent.KEYCODE_BACK)
         //   Toast.makeText(getContext(), "back press", Toast.LENGTH_LONG).show();

        return false;
        // Disable back button..............
    }
}
