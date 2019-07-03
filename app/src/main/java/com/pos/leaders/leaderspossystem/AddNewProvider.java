package com.pos.leaders.leaderspossystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.leaders.leaderspossystem.CustomerAndClub.CustmerManagementActivity;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.CityDbAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.ClubAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.ProviderDbAdapter;
import com.pos.leaders.leaderspossystem.Models.City;
import com.pos.leaders.leaderspossystem.Models.Club;
import com.pos.leaders.leaderspossystem.Models.Provider;
import com.pos.leaders.leaderspossystem.Tools.SETTINGS;
import com.pos.leaders.leaderspossystem.Tools.TitleBar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AddNewProvider extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {

    String street="" , job="" , email="" , houseNo="" , postalCode="" , country="" , countryCode="",providerCode="",strProviderId="";
    int cityId=0,branchId=0;
    EditText etProviderFirstName, etProviderLastName, etStreet, etJob, etEmail, etPhoneNo, etHouseNumber, etPostalCode, etCountry, etCountryCode,etProviderCode,etProviderId;
    Button btAddProvider, btCancel;
    Spinner selectCitySpinner, selectClubSpinner , providerBranch;
    ProviderDbAdapter providerDbAdapter;
    Provider provider;
    LinearLayout secondProviderInformation ;
    private List<City> cityList = null;
    private List<Club> groupList = null;
    ArrayList<Integer> permissions_name;
    RadioButton maleRadioButton, femaleRadioButton;
    RadioGroup radioGender;
    String gender = "";
    long providerId;
    ImageView advanceFeature;
    TextView advance ,tvCustomerBalance;
    LinearLayout ProviderBalance ;
    public static Context context = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_new_coustmer);
        TitleBar.setTitleBar(this);
        init();
        context=this;
        provider = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            permissions_name = getIntent().getIntegerArrayListExtra("permissions_name");
        }
        if(CustmerManagementActivity.Customer_Management_Edit==10){
            btAddProvider.setText(R.string.edit);
            CustmerManagementActivity.Customer_Management_Edit=0;
        }
        if(CustmerManagementActivity.Customer_Management_View==9){
            btAddProvider.setVisibility(View.GONE);
            etProviderFirstName.setEnabled(false);
            etProviderLastName.setEnabled(false);
            etCountry.setEnabled(false);
            etStreet.setEnabled(false);
            etJob.setEnabled(false);
            etEmail.setEnabled(false);
            etPhoneNo.setEnabled(false);
            etHouseNumber.setEnabled(false);
            etPostalCode.setEnabled(false);
            etCountryCode.setEnabled(false);
            etProviderCode.setEnabled(false);
            etProviderId.setEnabled(false);
            ProviderBalance.setVisibility(View.VISIBLE);
            CustmerManagementActivity.Customer_Management_View=0;
        }
        if (bundle != null) {
            providerId = (long) bundle.get("id");
            provider = providerDbAdapter.getProviderByID(providerId);
            etProviderFirstName.setText(provider.getFirstName());
            etProviderLastName.setText(provider.getLastName());
            etJob.setText(provider.getJob());
            etEmail.setText(provider.getEmail());
            etPhoneNo.setText(provider.getPhoneNumber());
            etStreet.setText(provider.getStreet());
            etHouseNumber.setText(provider.getHouseNumber());
            etPostalCode.setText(provider.getPostalCode());
            etCountry.setText(provider.getCountry());
            etCountryCode.setText(provider.getCountryCode());
            etProviderCode.setText(provider.getProviderCode());
            etProviderId.setText(provider.getProviderIdentity());
            btAddProvider.setText(getResources().getText(R.string.edit));
            tvCustomerBalance.setText(provider.getBalance()+"");

            if(secondProviderInformation.getVisibility()== View.VISIBLE) {
                if (provider.getGender().equalsIgnoreCase(getString(R.string.male))) {
                    radioGender.check(R.id.male);
                } else if (provider.getGender().equalsIgnoreCase(getString(R.string.female))) {
                    radioGender.check(R.id.female);
                }

                for (int i = 0; i < cityList.size(); i++) {
                    City city = cityList.get(i);
                    if (city.getCityId() == provider.getCity()) {
                        selectCitySpinner.setSelection(i);

                    }
                }
            }
        }

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             onBackPressed();            }
        });
        advanceFeature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondProviderInformation.setVisibility(View.VISIBLE);
                advanceFeature.setVisibility(View.INVISIBLE);
                advance.setVisibility(View.INVISIBLE);
            }
        });

        btAddProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _providerName = etProviderFirstName.getText().toString();
                Intent intent;
                String customerTypeText = "";
                if (provider == null) {
                    if (!_providerName.equals("")) {
                        if (secondProviderInformation.getVisibility() == View.VISIBLE) {
                            if (providerBranch.getSelectedItem().toString().equals(getString(R.string.all))) {
                                branchId = 0;
                            } else {
                                branchId = SETTINGS.branchId;
                            }
                            street = etStreet.getText().toString();
                            job = etJob.getText().toString();
                            email = etEmail.getText().toString();
                            houseNo = etHouseNumber.getText().toString();
                            postalCode = etPostalCode.getText().toString();
                            country = etCountry.getText().toString();
                            countryCode = etCountryCode.getText().toString();
                            providerCode = etProviderCode.getText().toString();
                            strProviderId = etProviderId.getText().toString();
                            cityId = (int) selectCitySpinner.getSelectedItemId();

                        }

                        if (etProviderFirstName.getText().toString().equals("")) {
                            etProviderFirstName.setBackgroundResource(R.drawable.backtext);
                            Toast.makeText(getApplicationContext(), getString(R.string.please_insert_first_name), Toast.LENGTH_LONG).show();
                        } else if (etProviderLastName.getText().toString().equals("")) {
                            etProviderLastName.setBackgroundResource(R.drawable.backtext);
                            Toast.makeText(getApplicationContext(), getString(R.string.please_insert_last_name), Toast.LENGTH_LONG).show();
                        } else if (etPhoneNo.getText().toString().equals("")) {
                            etPhoneNo.setBackgroundResource(R.drawable.backtext);
                            Toast.makeText(getApplicationContext(), getString(R.string.please_insert_phone_no), Toast.LENGTH_LONG).show();
                        } else if (!providerDbAdapter.availableCustomerPhoneNo(etPhoneNo.getText().toString())) {
                            etPhoneNo.setBackgroundResource(R.drawable.backtext);
                            Toast.makeText(getApplicationContext(), getString(R.string.please_insert_phone_no), Toast.LENGTH_LONG).show();
                        } else {
                            long i = 0;
                            try {
                                i = providerDbAdapter.insertEntry(etProviderFirstName.getText().toString(),
                                        etProviderLastName.getText().toString(), gender, email, job, etPhoneNo.getText().toString(), street, cityId, houseNo, etPostalCode.getText().toString(),
                                        country, countryCode, 0, providerCode, strProviderId, branchId);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (i > 0) {
                                Toast.makeText(getApplicationContext(), getString(R.string.success_adding_new_provider), Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.can_not_add_provider_please_try_again), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    // Edit mode
                    if (_providerName != "") {
                        if (secondProviderInformation.getVisibility() == View.VISIBLE) {
                            if (providerBranch.getSelectedItem().toString().equals(getString(R.string.all))) {
                                branchId = 0;
                            } else {
                                branchId = SETTINGS.branchId;
                            }
                            street = etStreet.getText().toString();
                            job = etJob.getText().toString();
                            email = etEmail.getText().toString();
                            houseNo = etHouseNumber.getText().toString();
                            postalCode = etPostalCode.getText().toString();
                            country = etCountry.getText().toString();
                            countryCode = etCountryCode.getText().toString();
                            providerCode = etProviderCode.getText().toString();
                            strProviderId = etProviderId.getText().toString();
                            for (int i = 0; i < cityList.size(); i++) {
                                City city = cityList.get(i);
                                if (city.getName().equalsIgnoreCase(selectCitySpinner.getSelectedItem().toString())) {
                                    cityId = (int) city.getCityId();
                                }
                            }

                            if (etProviderFirstName.getText().toString().equals("")) {
                                etProviderFirstName.setBackgroundResource(R.drawable.backtext);
                                Toast.makeText(getApplicationContext(), getString(R.string.please_insert_first_name), Toast.LENGTH_LONG).show();
                            } else if (etProviderLastName.getText().toString().equals("")) {
                                etProviderLastName.setBackgroundResource(R.drawable.backtext);
                                Toast.makeText(getApplicationContext(), getString(R.string.please_insert_last_name), Toast.LENGTH_LONG).show();
                            } else if (etPhoneNo.getText().toString().equals("")) {
                                etPhoneNo.setBackgroundResource(R.drawable.backtext);
                                Toast.makeText(getApplicationContext(), getString(R.string.please_insert_phone_no), Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    provider.setFirstName(etProviderFirstName.getText().toString());
                                    provider.setLastName(etProviderLastName.getText().toString());
                                    provider.setPhoneNumber(etPhoneNo.getText().toString());
                                    if (secondProviderInformation.getVisibility() == View.VISIBLE) {
                                        provider.setBranchId(branchId);
                                        provider.setJob(job);
                                        provider.setEmail(email);
                                        provider.setStreet(street);
                                        provider.setHouseNumber(houseNo);
                                        provider.setPostalCode(postalCode);
                                        provider.setCountry(country);
                                        provider.setCountryCode(countryCode);
                                        provider.setProviderCode(providerCode);
                                        provider.setProviderIdentity(strProviderId);
                                        provider.setGender(gender);
                                        provider.setCity(cityId);
                                    }
                                    providerDbAdapter.updateEntry(provider);
                                    Toast.makeText(getApplicationContext(), getString(R.string.success_edit_provider), Toast.LENGTH_SHORT).show();

                                    Log.i("success Edit", provider.toString());
                                    finish();
                                } catch (Exception ex) {
                                    Log.d("exset", ex.toString());
                                    Toast.makeText(getApplicationContext(), getString(R.string.can_not_edit_provider_please_try_again), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                    }
                }
            }
        });

    }

    private void init() {

        CityDbAdapter cityDbAdapter = new CityDbAdapter(AddNewProvider.this);
        cityDbAdapter.open();
        ClubAdapter groupAdapter = new ClubAdapter(AddNewProvider.this);
        groupAdapter.open();
        secondProviderInformation = (LinearLayout) findViewById(R.id.secondCustomerInformation);
        etProviderFirstName = (EditText) findViewById(R.id.etCustomerFirstName);
        etProviderLastName = (EditText) findViewById(R.id.etCustomerLastName);
        etStreet = (EditText) findViewById(R.id.etCustomerStreet);
        radioGender = (RadioGroup) findViewById(R.id.customerGender);
        maleRadioButton = (RadioButton) findViewById(R.id.male);
        femaleRadioButton = (RadioButton) findViewById(R.id.female);
        etJob = (EditText) findViewById(R.id.etCustomerJob);
        etEmail = (EditText) findViewById(R.id.etCustomerEmail);
        etPhoneNo = (EditText) findViewById(R.id.etCustomerPhoneNumber);
        etCountry = (EditText) findViewById(R.id.etCustomerCountry);
        etCountryCode = (EditText) findViewById(R.id.etCustomerCountryCode);
        etHouseNumber = (EditText) findViewById(R.id.etHouseNumber);
        etPostalCode = (EditText) findViewById(R.id.etCustomerPostalCode);
        etProviderCode = (EditText) findViewById(R.id.etCustomerCode);
        etProviderId = (EditText) findViewById(R.id.etCustomerId);
        btAddProvider = (Button) findViewById(R.id.add_Custmer);
        btCancel = (Button) findViewById(R.id.addCustmer_BTCancel);
        advanceFeature = (ImageView) findViewById(R.id.advanceFeature);
        advance = (TextView) findViewById(R.id.advance);
        providerDbAdapter = new ProviderDbAdapter(this);
        providerDbAdapter.open();
        selectCitySpinner = (Spinner) findViewById(R.id.customerCitySpinner);
        selectClubSpinner = (Spinner) findViewById(R.id.customerClubSpinner);
        tvCustomerBalance = (TextView) findViewById(R.id.addNewCustomerBalanceValue);
        selectCitySpinner.setOnItemSelectedListener(this);
        final List<String> city = new ArrayList<String>();
        cityList = cityDbAdapter.getAllCity();
        for (int i = 0; i < cityList.size(); i++) {
            city.add(cityList.get(i).getName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, city);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        selectCitySpinner.setAdapter(dataAdapter);


        // Creating adapter for spinner


        // attaching data adapter to spinner
        providerBranch = (Spinner) findViewById(R.id.customerBranch);
        final List<String> customerBranchList = new ArrayList<String>();
        customerBranchList.add(getString(R.string.all));
        customerBranchList.add(getString(R.string.pos_branch));
        final ArrayAdapter<String> branchDataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, customerBranchList);
        branchDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        providerBranch.setAdapter(branchDataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}