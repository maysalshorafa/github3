package com.pos.leaders.leaderspossystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.leaders.leaderspossystem.DataBaseAdapter.CustomerDBAdapter;
import com.pos.leaders.leaderspossystem.Models.Customer_M;
import com.pos.leaders.leaderspossystem.Tools.SESSION;
import com.pos.leaders.leaderspossystem.Tools.UtilityValidation;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddNewCoustmer extends AppCompatActivity  {
    Intent intent ;
    EditText etCoustmerName, dateOfbirtday, etCoustmerId, etGender, etJob, etEmail, etPhoneNo, etAddress;
    Button btAddcoustmer,btCancel;
    Spinner selectCitySpinner, selectClubSpinner;
    CustomerDBAdapter customerDBAdapter;
    int selectedCity;
    Customer_M custmer;
    android.support.v7.app.ActionBar actionBar;



    int selectedClub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_new_coustmer);


        final ViewGroup actionBarLayout = (ViewGroup) getLayoutInflater().inflate(
                R.layout.title_bar,
                null);

        // Set up your ActionBar
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(android.app.ActionBar.NAVIGATION_MODE_LIST);
        // TODO: Remove the redundant calls to getSupportActionBar()
        //       and use variable actionBar instead
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(actionBarLayout);
        Calendar ca = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        // You customization
        final int actionBarColor = getResources().getColor(R.color.primaryColor);
        actionBar.setBackgroundDrawable(new ColorDrawable(actionBarColor));

        final TextView actionBarTitle = (TextView) findViewById(R.id.editText8);
        actionBarTitle.setText(format.format(ca.getTime()));
        final TextView actionBarSent = (TextView) findViewById(R.id.editText9);
        actionBarSent.setText("POSID  "+ SESSION.POS_ID_NUMBER);


        final TextView actionBarStaff = (TextView) findViewById(R.id.editText10);
        actionBarStaff.setText(SESSION._USER.getFullName());

        final TextView actionBarLocations = (TextView) findViewById(R.id.editText11);
        actionBarLocations.setText(" "+SESSION._USER.getPermtionName());


        init();
        custmer = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
           long i = (long) bundle.get("id");
            Toast.makeText(getApplicationContext(), "Custmer id is"+ i, Toast.LENGTH_LONG).show();

            custmer = customerDBAdapter.getCustmerByID(i);
            etCoustmerName.setText(custmer.getName());
            dateOfbirtday.setText(custmer.getBirthday());
//           etCoustmerId.setText((CharSequence) customerDBAdapter.getCustmerByID(i));
            etGender.setText(custmer.getGender());
            etJob.setText(custmer.getJob());
            etEmail.setText(custmer.getEmail());
            etPhoneNo.setText(custmer.getPhoneNumber());
            etAddress.setText(custmer.getAddress());
            btAddcoustmer.setText(getResources().getText(R.string.edit));

            //The key argument here must match that used in the other activity
        }

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 22/10/2016 cancel and return to previous activity
                Intent intent = new Intent(AddNewCoustmer.this, DashBoard.class);
                //	intent.putExtra("permissions_name",user.getPermtionName());

                //userDBAdapter.close();
                startActivity(intent);
            }
        });

        btAddcoustmer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _custmerName = etCoustmerName.getText().toString();
                Intent intent;
                if (custmer == null) {
                    if (_custmerName != "") {
                        if (customerDBAdapter.availableCustmerName(_custmerName)) {
                            if (etCoustmerName.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert  name", Toast.LENGTH_LONG).show();
                            } else if (dateOfbirtday.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert date of birthday", Toast.LENGTH_LONG).show();
                            } else if (etGender.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert gender", Toast.LENGTH_LONG).show();
                            }
                            else if (etEmail.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert job", Toast.LENGTH_LONG).show();
                            }else if (etJob.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert gender", Toast.LENGTH_LONG).show();
                            }else if (etPhoneNo.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert phoneno", Toast.LENGTH_LONG).show();
                            }else if (etAddress.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert adress", Toast.LENGTH_LONG).show();
                            }else if (selectClubSpinner.getSelectedItem().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please Selesct Club", Toast.LENGTH_LONG).show();
                            }else if (selectCitySpinner.getSelectedItem().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please Select City", Toast.LENGTH_LONG).show();
                            }

                            else {
                                long i = customerDBAdapter.insertEntry(Long.parseLong(etCoustmerId.getText().toString()) ,etCoustmerName.getText().toString(), dateOfbirtday.getText().toString(),
                                        etGender.getText().toString(),  etEmail.getText().toString(),
                                        etJob.getText().toString(),etPhoneNo.getText().toString(),etAddress.getText().toString() ,selectedCity,selectedClub);
                                if (i > 0) {
                                    Log.i("success", "adding new custmer");
                                    intent = new Intent(AddNewCoustmer.this, CustmerMangmentActivity.class);
                                    startActivity(intent);
                                    //// TODO: 17/10/2016 sucess to add entity
                                } else {
                                    Log.e("error", "can`t add custmer");
                                    Toast.makeText(getApplicationContext(), "Can`t add custmer please try again", Toast.LENGTH_LONG).show();
                                    //// TODO: 17/10/2016 error with adding entity
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Custmer name is not available, try to use another Custmer name", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    // Edit mode
                    if (_custmerName != "") {
                        if ((customerDBAdapter.availableCustmerName(_custmerName)) || _custmerName == custmer.getCustmerName()) {
                            if (etCoustmerName.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert  name", Toast.LENGTH_LONG).show();
                            } else if (dateOfbirtday.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert date of birthday", Toast.LENGTH_LONG).show();
                            } else if (etGender.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert gender", Toast.LENGTH_LONG).show();
                            }
                            else if (etEmail.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert job", Toast.LENGTH_LONG).show();
                            }else if (etJob.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert gender", Toast.LENGTH_LONG).show();
                            }else if (etPhoneNo.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert phoneno", Toast.LENGTH_LONG).show();
                            }else if (etAddress.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please insert adress", Toast.LENGTH_LONG).show();
                            }else if (selectClubSpinner.getSelectedItem().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please Selesct Club", Toast.LENGTH_LONG).show();
                            }else if (selectCitySpinner.getSelectedItem().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Please Select City", Toast.LENGTH_LONG).show();
                            } else {
                                try {
                                    custmer.setName(etCoustmerName.getText().toString());
                                    custmer.setAddress(etAddress.getText().toString());
                                    custmer.setBirthday(dateOfbirtday.getText().toString());
                                    custmer.setEmail(etGender.getText().toString());
                                    custmer.setPhoneNumber(etPhoneNo.getText().toString());
                                    custmer.setEmail(etEmail.getText().toString());
                                    custmer.setGender(etGender.getText().toString());
                                    custmer.setJob(etJob.getText().toString());

                                    customerDBAdapter.updateEntry(custmer);
                                    Log.i("success Edit", custmer.toString());
                                    intent = new Intent(AddNewCoustmer.this, CustmerMangmentActivity.class);
                                    startActivity(intent);
                                } catch (Exception ex) {
                                    Log.e("error can`t edit custmer", ex.getMessage().toString());
                                    Toast.makeText(getApplicationContext(), "Can`t edit custmer please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Custmer name is not available, try to use another custmer name", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });






    }





    private void init(){

        etCoustmerName = (EditText) findViewById(R.id.add_coustmer_name);
        dateOfbirtday = (EditText) findViewById(R.id.addCoustmer_Birthday);
        etCoustmerId = (EditText) findViewById(R.id.addCoustmer_Id);
        etGender = (EditText) findViewById(R.id.addCoustmer_gender);


        etJob = (EditText) findViewById(R.id.addCoustmer_job);
        etEmail = (EditText) findViewById(R.id.addCoustmer_email);
        etPhoneNo = (EditText) findViewById(R.id.addCoustmer_ETPhoneNumber);
        etAddress = (EditText) findViewById(R.id.addCoustmer_Address);
        btAddcoustmer=(Button)findViewById(R.id.add_Custmer);
        btCancel=(Button)findViewById(R.id.addCustmer_BTCancel);

//        dateFormatter = new SimpleDateFormat(UtilityDateFormater.Format1);
        customerDBAdapter=new CustomerDBAdapter(this);
        customerDBAdapter.open();


        selectCitySpinner = (Spinner) findViewById(R.id.customer_spinner);
        selectClubSpinner = (Spinner) findViewById(R.id.SelectClubSpinner);


        ArrayList<Integer> citiesArray = new ArrayList<Integer>();

        // here we add the values to the Spiiner array
        citiesArray.add(1);
        citiesArray.add(2);
        citiesArray.add(3);


        //Spinner My_spinner = (Spinner) findViewById(R.id.customer_spinner);
        ArrayAdapter city_adapter = new ArrayAdapter(this, R.layout.spinner_row_layout,R.id.spinnerTxt,
                citiesArray);

        ArrayAdapter club_adapter = new ArrayAdapter(this, R.layout.spinner_club_row,R.id.spinnerClubRowTxt,
                citiesArray);
        selectCitySpinner.setAdapter(city_adapter);
        selectClubSpinner.setAdapter(club_adapter);

        selectCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2,
                                       long arg3) {
                // TODO Auto-generated method stub

                selectedCity = (int) parent.getItemAtPosition(arg2);
                // ((TextView) parent.getChildAt(0)).setTextColor(0x00000000);

                Log.e("Test Spinner", "" +   selectedCity);


            }



            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });

        selectClubSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            public void onItemSelected(AdapterView<?> parent, View arg1, int arg2,
                                       long arg3) {
                // TODO Auto-generated method stub

                selectedClub = (int) parent.getItemAtPosition(arg2);
                // ((TextView) parent.getChildAt(0)).setTextColor(0x00000000);

                Log.e("Test Spinner", "" +   selectedCity);


            }



            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });


    }



    protected boolean ValidationCreationMethod(boolean notvalid){

        //notvalid = false;
        String name = etCoustmerName.getText().toString();
        String email = etEmail.getText().toString();
        String gender = etGender.getText().toString();
        String job  = etJob.getText().toString();
        String phoneNumber = etPhoneNo.getText().toString();
        String address  = etAddress.getText().toString();
        String birthdate = dateOfbirtday.getText().toString();
        String customerID = etCoustmerId.getText().toString();


        // FIRSTLY: CHECK THE VALIDATION OF THE INPUTS.


        if (!UtilityValidation.isValidName(name)) {
            etCoustmerName.setError("Invalid name");
            notvalid = true;

        }
        if(!UtilityValidation.isValidMobile(phoneNumber)){ // CHECK THE VALIDATION OF THE PHONE NUMBER
            etPhoneNo.setError("Invalid Mobile");
            notvalid = true;

        }
        if (!UtilityValidation.isValidEmail(email)) {
            etEmail.setError("Invalid Email");
            notvalid = true;
        }


        if(!UtilityValidation.isValidJob(job)){
            etJob.setError("Invalid Address");
            notvalid = true;
        }

        if(!UtilityValidation.isValidGender(gender)){
            etGender.setError("Invalid Address");
            notvalid = true;
        }

        if(!UtilityValidation.isValidAddress(address)){
            etAddress.setError("Invalid Address");
            notvalid = true;
        }

        if(!UtilityValidation.isValidID(customerID)){
            etCoustmerId.setError("Invalid Address");
            notvalid = true;
        }

        if(UtilityValidation.isValidBirthdate(birthdate)){
            dateOfbirtday.setError(("Invalid Birthdate"));
            notvalid = true;
        }
        if(!notvalid) {


            return notvalid;
        }

        return notvalid;
    }




}
