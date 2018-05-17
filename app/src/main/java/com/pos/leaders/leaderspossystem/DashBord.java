package com.pos.leaders.leaderspossystem;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pos.leaders.leaderspossystem.CustomerAndClub.Customer;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.AReportDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.AReportDetailsDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.Currency.CurrencyDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.Currency.CurrencyTypeDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.PermissionsDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.ProductDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.SaleDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.ScheduleWorkersDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.UserDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.ZReportDBAdapter;
import com.pos.leaders.leaderspossystem.Models.AReport;
import com.pos.leaders.leaderspossystem.Models.Currency.Currency;
import com.pos.leaders.leaderspossystem.Models.Currency.CurrencyType;
import com.pos.leaders.leaderspossystem.Models.Permission.Permissions;
import com.pos.leaders.leaderspossystem.Models.Product;
import com.pos.leaders.leaderspossystem.Models.Sale;
import com.pos.leaders.leaderspossystem.Models.ScheduleWorkers;
import com.pos.leaders.leaderspossystem.Models.User;
import com.pos.leaders.leaderspossystem.Models.ZReport;
import com.pos.leaders.leaderspossystem.Printer.HPRT_TP805;
import com.pos.leaders.leaderspossystem.Printer.PrintTools;
import com.pos.leaders.leaderspossystem.Printer.SUNMI_T1.AidlUtil;
import com.pos.leaders.leaderspossystem.SettingsTab.SettingsTab;
import com.pos.leaders.leaderspossystem.Tools.InternetStatus;
import com.pos.leaders.leaderspossystem.Tools.SESSION;
import com.pos.leaders.leaderspossystem.Tools.SETTINGS;
import com.pos.leaders.leaderspossystem.Tools.TitleBar;
import com.pos.leaders.leaderspossystem.syncposservice.Enums.MessageKey;
import com.pos.leaders.leaderspossystem.syncposservice.Service.SyncMessage;
import com.sunmi.aidl.MSCardService;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import static com.pos.leaders.leaderspossystem.SetupNewPOSOnlineActivity.BO_CORE_ACCESS_AUTH;
import static com.pos.leaders.leaderspossystem.SetupNewPOSOnlineActivity.BO_CORE_ACCESS_TOKEN;

public class DashBord extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private boolean enableBackButton = true;
    Button mainScreen, report, product, department, users, backUp, customerClub, logOut, offers, settings , schedule_workers;
    Button btZReport, btAReport;
    AReportDBAdapter aReportDBAdapter;
    User user = new User();
    UserDBAdapter userDBAdapter;
    ArrayList<Integer> permissions_name;
    ArrayList<Permissions> permissions = new ArrayList<Permissions>();
    ScheduleWorkersDBAdapter scheduleWorkersDBAdapter;
    Intent i;
    Sale lastSale;
    Currency fCurrency;
    Currency sCurrency;
    Currency tCurrency;
    Currency forthCurrency;
    double firstCurrencyInDefaultValue = 0, secondCurrencyInDefaultValue = 0, thirdCurrencyInDefaultValue = 0, forthCurrencyInDefaultValue = 0;
    double aReportTotalAmount = 0;
    private MSCardService sendservice;
    long aReportId;
    double totalZReportAmount =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_temp_dash_bord);
        if (SyncMessage.isConnected(this)) {
            SESSION.internetStatus = InternetStatus.CONNECTED;
        } else {
            SESSION.internetStatus = InternetStatus.ERROR;
        }

        TitleBar.setTitleBar(this);

        //run MSR Service
        Intent intent = new Intent();
        intent.setPackage("com.sunmi.mscardservice");
        intent.setAction("com.sunmi.mainservice.MainService");
        bindService(intent, new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                sendservice = MSCardService.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);


        //sendable.run();

        AccessToken accessToken = new AccessToken(this);
        accessToken.execute(this);
        //load pos id from shared file
        SharedPreferences sharedpreferences = getSharedPreferences(BO_CORE_ACCESS_AUTH, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(MessageKey.PosId)) {
            int posID = sharedpreferences.getInt(MessageKey.syncNumber, 1);
            SESSION.POS_ID_NUMBER = posID;
        }
        //load token from shared file
        sharedpreferences = getSharedPreferences(BO_CORE_ACCESS_TOKEN, Context.MODE_PRIVATE);
        if (sharedpreferences.contains(MessageKey.Token)) {
            String token = sharedpreferences.getString(MessageKey.Token, null);
            SESSION.token = token;
        }


        //end
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //   permissions_name = bundle.getString("permissions_name");
            permissions_name = getIntent().getIntegerArrayListExtra("permissions_name");
        }


        PermissionsDBAdapter permissionsDBAdapter = new PermissionsDBAdapter(this);
        permissionsDBAdapter.open();
        for (int p : permissions_name) {
            Permissions per = permissionsDBAdapter.getPermission(p);
            if (per != null) {
                permissions.add(per);
            }
        }
        permissionsDBAdapter.close();


        mainScreen = (Button) findViewById(R.id.mainScreen);
        btAReport = (Button) findViewById(R.id.dashboard_btAreport);
        btZReport = (Button) findViewById(R.id.dashboard_btZreport);
        report = (Button) findViewById(R.id.report);
        product = (Button) findViewById(R.id.product);
        department = (Button) findViewById(R.id.department);
        //offers = (Button) findViewById(R.id.offers);
        users = (Button) findViewById(R.id.users);
        schedule_workers = (Button) findViewById(R.id.schedule_workers);
        backUp = (Button) findViewById(R.id.backUp);
        logOut = (Button) findViewById(R.id.logOut);
        customerClub = (Button) findViewById(R.id.coustmerClub);
        settings = (Button) findViewById(R.id.settings);

        EnableButtons();
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*scheduleWorkersDBAdapter=new ScheduleWorkersDBAdapter(getApplicationContext());
                scheduleWorkersDBAdapter.open();
                ScheduleWorkers scheduleWorkers = scheduleWorkersDBAdapter.getLastScheduleWorkersByUserID(SESSION._USER.getId());
                scheduleWorkersDBAdapter.updateEntry(SESSION._USER.getId(),new Date());*/
                Intent intent = new Intent(DashBord.this, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
/**
                try {
                    scheduleWorkersDBAdapter.updateEntry(SESSION._SCHEDULEWORKERS.getId(), new Date());
                    SESSION._SCHEDULEWORKERS.setExitTime(new Date().getTime());
                    Log.i("Worker get out", SESSION._SCHEDULEWORKERS.toString());
                } catch (Exception ex) {
                }**/
                SESSION._LogOut();
                startActivity(intent);
            }
        });


        //region customerName report button
        btAReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AReport _aReport = new AReport();
                _aReport.setByUserID(SESSION._USER.getId());
                _aReport.setCreationDate(new Date().getTime());

                AReport aReport = getLastAReport();
                ZReport zReport = getLastZReport();

                if (aReport == null) {
                    _aReport.setLastZReportID(-1);
                    _aReport.setLastSaleID(-1);

                    ShowAReportDialog(_aReport);
                } else {
                    _aReport.setLastZReportID(zReport.getId());
                    _aReport.setLastSaleID(zReport.getEndSaleId());

                    ShowAReportDialog(_aReport);
                }

            }
        });
        //endregion customerName report button

        //region z report button
        btZReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DashBord.this)
                        .setTitle(getString(R.string.create_z_report))
                        .setMessage(getString(R.string.create_z_report_message))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ZReportDBAdapter zReportDBAdapter = new ZReportDBAdapter(DashBord.this);
                                zReportDBAdapter.open();
                                ZReport lastZReport = getLastZReport();

                                if (lastZReport == null) {
                                    lastZReport = new ZReport();
                                    lastZReport.setEndSaleId(0);
                                }
                                ZReport z = new ZReport(0, new Date().getTime(), SESSION._USER, lastZReport.getEndSaleId() + 1, lastSale);
                                z.setByUser(SESSION._USER.getId());
                                double amount = zReportDBAdapter.getZReportAmount(z.getStartSaleId(), z.getEndSaleId());
                                totalZReportAmount+=LogInActivity.LEADPOS_MAKE_Z_REPORT_TOTAL_AMOUNT+amount;
                                long zID = zReportDBAdapter.insertEntry(z.getCreationDate(), z.getByUser(), z.getStartSaleId(), z.getEndSaleId(),amount,totalZReportAmount);
                                z.setId(zID);
                                lastZReport = new ZReport(z);
                                zReportDBAdapter.close();
                                PrintTools pt = new PrintTools(DashBord.this);

                                //create and print z report
                                Bitmap bmap = pt.createZReport(lastZReport.getId(), lastZReport.getStartSaleId(), lastZReport.getEndSaleId(), false,totalZReportAmount);
                                if (bmap != null)
                                    pt.PrintReport(bmap);

                                Intent i = new Intent(DashBord.this, ReportZDetailsActivity.class);
                                i.putExtra(ZReportActivity.COM_LEADPOS_ZREPORT_ID, lastZReport.getId());
                                i.putExtra(ZReportActivity.COM_LEADPOS_ZREPORT_FORM, lastZReport.getStartSaleId());
                                i.putExtra(ZReportActivity.COM_LEADPOS_ZREPORT_TO, lastZReport.getEndSaleId());
                                i.putExtra(ZReportActivity.COM_LEADPOS_ZREPORT_TOTAL_AMOUNT,totalZReportAmount);

                                startActivity(i);
                                btZReport.setEnabled(false);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
        //endregion z report button

        mainScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), ReportsManagementActivity.class);
                startActivity(i);
            }
        });
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), ProductCatalogActivity.class);
                startActivity(i);
            }
        });
        department.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), DepartmentActivity.class);
                startActivity(i);
            }
        });
        backUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), BackupActivity.class);
             startActivity(i);
            }
        });
        users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), WorkerManagementActivity.class);
                i.putIntegerArrayListExtra("permissions_name", permissions_name);
                startActivity(i);
            }
        });
        schedule_workers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Product>lstPro= null;
                try {
                    long startTime = System.nanoTime();
                    lstPro = readProducts();
                    ProductDBAdapter productDBAdapter = new ProductDBAdapter(getBaseContext());
                    productDBAdapter.open();
                    Log.d("productsizelist ",lstPro.size()+"");
                    for(int i = 0; i<lstPro.size(); i++){
                        Product p=lstPro.get(i);
                    productDBAdapter.insertEntry(p.getName(), p.getBarCode(), "", p.getPrice(), p.getCostPrice(), true, false, 1, p.getByUser(), 1, 1);
                    }
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime); //divide by 1000000 to get milliseconds.
                   Log.d("executiontime:",duration+"");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //i = new Intent(getApplicationContext(), ScheduleWorkersActivity.class);
             //   startActivity(i);
            }
        });
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), SettingsTab.class);
                startActivity(i);
            }
        });
        customerClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), Customer.class);
                i.putIntegerArrayListExtra("permissions_name", permissions_name);
                startActivity(i);
            }
        });


        report.setEnabled(false);
        product.setEnabled(false);
        schedule_workers.setEnabled(false);
        users.setEnabled(false);
        backUp.setEnabled(false);
        department.setEnabled(false);
        department.setEnabled(false);
        customerClub.setEnabled(false);
        settings.setEnabled(false);
        btAReport.setEnabled(false);
        btZReport.setEnabled(false);
        mainScreen.setEnabled(false);
    }

    public void EnableButtons() {
        for (Permissions p : permissions) {
            switch (p.getName()) {
                case Permissions.PERMISSIONS_MAIN_SCREEN:
                    if (needAReport()) {
                        btAReport.setEnabled(true);
                        btZReport.setEnabled(false);
                        mainScreen.setEnabled(false);
                    } else {
                        if (lastSale == null) {
                            btZReport.setEnabled(false);
                        } else
                            btZReport.setEnabled(true);
                        btAReport.setEnabled(false);
                        mainScreen.setEnabled(true);
                    }
                    break;

                case Permissions.PERMISSIONS_REPORT:
                    report.setEnabled(true);
                    break;
                case Permissions.PERMISSIONS_PRODUCT:
                    product.setEnabled(true);
                    break;
                case Permissions.PERMISSIONS_DEPARTMENT:
                    department.setEnabled(true);
                    break;
                case Permissions.PERMISSIONS_USER:
                    users.setEnabled(true);
                    break;
                case Permissions.PERMISSIONS_SCHEDULE_WORKERS:
                    schedule_workers.setEnabled(true);
                    break;
                case Permissions.PERMISSIONS_BACK_UP:
                    backUp.setEnabled(true);
                    break;
                case Permissions.PERMISSIONS_SETTINGS:
                    settings.setEnabled(true);
                    break;
                case Permissions.PERMISSIONS_USER_CLUB:
                    customerClub.setEnabled(true);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SaleDBAdapter saleDBAdapter = new SaleDBAdapter(DashBord.this);
        saleDBAdapter.open();
        lastSale = saleDBAdapter.getLast();
        saleDBAdapter.close();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String str = extras.getString(LogInActivity.LEADPOS_MAKE_A_REPORT);
            if (str.equals(LogInActivity.LEADPOS_MAKE_A_REPORT)) {
                extras.clear();
            }
        }
        EnableButtons();


     switch (SETTINGS.printer) {
            case HPRT_TP805:
                HPRT_TP805.setConnected(false);
                if (HPRT_TP805.connect(this)) {
                    Toast.makeText(this, "Printer Connect Success!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Printer Connect Error!", Toast.LENGTH_LONG).show();
                }
                break;
            case BTP880:
                break;
            case SUNMI_T1:
                AidlUtil.getInstance().connectPrinterService(this);
                if (AidlUtil.getInstance().isConnect()) {
                    Toast.makeText(this, "Printer Connect Success!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Printer Connect Error!", Toast.LENGTH_LONG).show();
                }
                break;

        }

    }

    private boolean needAReport() {
        ZReport zReport = getLastZReport();
        AReport aReport = getLastAReport();


        if (aReport != null && zReport != null) {
            if (aReport.getLastZReportID() == zReport.getId()) {

            } else {
                return true;
            }

        } else if (aReport == null) {
            return true;
        }
        return false;
    }

    private ZReport getLastZReport() {
        ZReportDBAdapter zReportDBAdapter = new ZReportDBAdapter(this);

        zReportDBAdapter.open();
        ZReport zReport = null;
        try {
            zReport = zReportDBAdapter.getLastRow();
        } catch (Exception e) {
            e.printStackTrace();
        }
        zReportDBAdapter.close();
        return zReport;
    }

    private AReport getLastAReport() {
        AReportDBAdapter aReportDBAdapter = new AReportDBAdapter(this);
        aReportDBAdapter.open();
        AReport aReport = null;

        try {
            aReport = aReportDBAdapter.getLastRow();

        } catch (Exception e) {
            e.printStackTrace();
        }

        aReportDBAdapter.close();
        return aReport;
    }

    private void ShowAReportDialog(final AReport aReport) {

        //there is no customerName report after z report
        enableBackButton = false;
        // a_report Dialog WithOut Currency
        if (!SETTINGS.enableCurrencies) {
            final Dialog discountDialog = new Dialog(DashBord.this);
            discountDialog.setTitle(R.string.opening_report);
            discountDialog.setContentView(R.layout.cash_payment_dialog);
            discountDialog.setCancelable(false);

            final Button btOK = (Button) discountDialog.findViewById(R.id.cashPaymentDialog_BTOk);
            final Button btCancel = (Button) discountDialog.findViewById(R.id.cashPaymentDialog_BTCancel);
            btCancel.setVisibility(View.GONE);
            final EditText et = (EditText) discountDialog.findViewById(R.id.cashPaymentDialog_TECash);
            final Switch sw = (Switch) discountDialog.findViewById(R.id.cashPaymentDialog_SwitchProportion);
            sw.setVisibility(View.GONE);
            discountDialog.setCanceledOnTouchOutside(false);

            discountDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    DashBord.this.onResume();
                }
            });

            et.setHint(R.string.amount);
            et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        btOK.callOnClick();
                    }
                    return false;
                }
            });

            btOK.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v) {
                    String str = et.getText().toString();
                    if (!str.equals("")) {
                        aReport.setAmount(Double.parseDouble(str));
                        AReportDBAdapter aReportDBAdapter = new AReportDBAdapter(DashBord.this);
                        aReportDBAdapter.open();
                        aReportDBAdapter.insertEntry(aReport.getCreationDate(), aReport.getByUserID(), aReport.getAmount(), aReport.getLastSaleID(), aReport.getLastZReportID());
                        aReportDBAdapter.close();
                        discountDialog.cancel();
                    }
                }
            });
            discountDialog.show();
        }
        // With Currency Model
        else {

            final Dialog aReportDialog = new Dialog(DashBord.this);
            aReportDialog.setTitle(R.string.opening_report);
            aReportDialog.setContentView(R.layout.areport_details_dialog);
            aReportDialog.setCancelable(false);
            //Getting default currencies name and values
            List<CurrencyType> currencyTypesList = null;
            final List<Currency> currenciesList;
            List<String> currenciesNames;
            CurrencyTypeDBAdapter currencyTypeDBAdapter = new CurrencyTypeDBAdapter(this);
            currencyTypeDBAdapter.open();
            currencyTypesList = currencyTypeDBAdapter.getAllCurrencyType();
            currencyTypeDBAdapter.close();
            final CurrencyDBAdapter currencyDBAdapter = new CurrencyDBAdapter(DashBord.this);
            currencyDBAdapter.open();
            currenciesList = currencyDBAdapter.getAllCurrencyLastUpdate(currencyTypesList);
            currenciesNames = new ArrayList<String>();
            for (int i = 0; i < currencyTypesList.size(); i++) {
                currenciesNames.add(currencyTypesList.get(i).getType());
            }

            final AReportDetailsDBAdapter aReportDetailsDBAdapter = new AReportDetailsDBAdapter(DashBord.this);
            aReportDetailsDBAdapter.open();
            // Creating adapter for spinner
            final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currenciesNames);

            // Drop down layout style - list view with radio button
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // attaching data adapter to spinner
            Spinner sFirstCurrency = (Spinner) aReportDialog.findViewById(R.id.aReportDetailsDialog_SPForFirstCurrency);
            Spinner sSecondCurrency = (Spinner) aReportDialog.findViewById(R.id.aReportDetailsDialog_SPForSecondCurrency);
            Spinner sThirdCurrency = (Spinner) aReportDialog.findViewById(R.id.aReportDetailsDialog_SPForThirdCurrency);
            Spinner sForthCurrency = (Spinner) aReportDialog.findViewById(R.id.aReportDetailsDialog_SPForForthCurrency);
            sFirstCurrency.setAdapter(dataAdapter);
            sSecondCurrency.setAdapter(dataAdapter);
            sThirdCurrency.setAdapter(dataAdapter);
            sForthCurrency.setAdapter(dataAdapter);
            fCurrency = currenciesList.get(0);
            sCurrency = currenciesList.get(0);
            tCurrency = currenciesList.get(0);
            forthCurrency = currenciesList.get(0);
            // define editText , Button ,ImageView ,Layout
            final EditText ETFirstCurrencyAmount = (EditText) aReportDialog.findViewById(R.id.aReportDetailsDialog_ETFirstCurrencyAmount);
            final EditText ETSecondCurrencyAmount = (EditText) aReportDialog.findViewById(R.id.aReportDetailsDialog_ETSecondCurrencyAmount);
            final EditText ETThirdCurrencyAmount = (EditText) aReportDialog.findViewById(R.id.aReportDetailsDialog_ETThirdCurrencyAmount);
            final EditText ETForthCurrencyAmount = (EditText) aReportDialog.findViewById(R.id.aReportDetailsDialog_ETForthCurrencyAmount);
            final Button btOKForCurrency = (Button) aReportDialog.findViewById(R.id.aReportDetailsDialog_BTOK);
            ImageView addSecondCurrency = (ImageView) aReportDialog.findViewById(R.id.addSecondCurrency);
            ImageView addThirdCurrency = (ImageView) aReportDialog.findViewById(R.id.addThirdCurrency);
            ImageView addForthCurrency = (ImageView) aReportDialog.findViewById(R.id.addForthCurrency);
            final LinearLayout secondCurrencyLayout = (LinearLayout) aReportDialog.findViewById(R.id.secondCurrencyLayout);
            final LinearLayout thirdCurrencyLayout = (LinearLayout) aReportDialog.findViewById(R.id.thirdCurrencyLayout);
            final LinearLayout forthCurrencyLayout = (LinearLayout) aReportDialog.findViewById(R.id.forhCurrencyLayout);
            aReportDialog.setCanceledOnTouchOutside(false);

            aReportDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    DashBord.this.onResume();
                }
            });
            addSecondCurrency.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v) {
                    secondCurrencyLayout.setVisibility(View.VISIBLE);
                }
            });
            addThirdCurrency.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v) {
                    thirdCurrencyLayout.setVisibility(View.VISIBLE);
                }
            });
            addForthCurrency.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v) {
                    forthCurrencyLayout.setVisibility(View.VISIBLE);
                }
            });
            sFirstCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    fCurrency = currenciesList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            sSecondCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    sCurrency = currenciesList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            sThirdCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    tCurrency = currenciesList.get(position);
                }


                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
            sForthCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    forthCurrency = currenciesList.get(position);
                }


                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });


            ETFirstCurrencyAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        btOKForCurrency.callOnClick();
                    }
                    return false;
                }
            });
            ETFirstCurrencyAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!ETFirstCurrencyAmount.getText().toString().equals("")) {
                        firstCurrencyInDefaultValue = Double.parseDouble(ETFirstCurrencyAmount.getText().toString());

                    } else {
                        firstCurrencyInDefaultValue = 0;
                    }
                }
            });

            ETSecondCurrencyAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!ETSecondCurrencyAmount.getText().toString().equals("")) {
                        secondCurrencyInDefaultValue = Double.parseDouble(ETSecondCurrencyAmount.getText().toString());

                    } else {
                        secondCurrencyInDefaultValue = 0;
                    }
                }
            });

            ETThirdCurrencyAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!ETThirdCurrencyAmount.getText().toString().equals("")) {
                        thirdCurrencyInDefaultValue = Double.parseDouble(ETThirdCurrencyAmount.getText().toString());

                    } else {
                        thirdCurrencyInDefaultValue = 0;
                    }
                }
            });
            ETForthCurrencyAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!ETForthCurrencyAmount.getText().toString().equals("")) {
                        forthCurrencyInDefaultValue = Double.parseDouble(ETForthCurrencyAmount.getText().toString());

                    } else {
                        forthCurrencyInDefaultValue = 0;
                    }
                }
            });

            btOKForCurrency.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v) {
                    String str = ETFirstCurrencyAmount.getText().toString();
                    if (!str.equals("")) {
                        aReportTotalAmount = firstCurrencyInDefaultValue * fCurrency.getRate() + secondCurrencyInDefaultValue * sCurrency.getRate() + thirdCurrencyInDefaultValue * tCurrency.getRate() + forthCurrencyInDefaultValue * forthCurrency.getRate();
                        aReport.setAmount(aReportTotalAmount);
                        AReportDBAdapter aReportDBAdapter = new AReportDBAdapter(DashBord.this);
                        aReportDBAdapter.open();
                        aReportDBAdapter.insertEntry(aReport.getCreationDate(), aReport.getByUserID(), aReport.getAmount(), aReport.getLastSaleID(), aReport.getLastZReportID());
                        try {
                            aReportId = aReportDBAdapter.getLastRow().getId();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (firstCurrencyInDefaultValue > 0) {
                            aReportDetailsDBAdapter.insertEntry(aReportId, firstCurrencyInDefaultValue, fCurrency.getId(), firstCurrencyInDefaultValue * fCurrency.getRate());
                        }
                        if (secondCurrencyInDefaultValue > 0) {
                            aReportDetailsDBAdapter.insertEntry(aReportId, secondCurrencyInDefaultValue, sCurrency.getId(), secondCurrencyInDefaultValue * sCurrency.getRate());
                        }
                        if (thirdCurrencyInDefaultValue > 0) {
                            aReportDetailsDBAdapter.insertEntry(aReportId, thirdCurrencyInDefaultValue, tCurrency.getId(), thirdCurrencyInDefaultValue * tCurrency.getRate());
                        }
                        if (forthCurrencyInDefaultValue > 0) {
                            aReportDetailsDBAdapter.insertEntry(aReportId, firstCurrencyInDefaultValue, forthCurrency.getId(), forthCurrencyInDefaultValue * forthCurrency.getRate());
                        }

                        aReportDialog.cancel();
                        aReportDBAdapter.close();
                        aReportDetailsDBAdapter.close();

                    }
                }
            });

            aReportDialog.show();


        }


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    static int failImportItems=0;
    static String msg="";

    public List<Product> readProducts() throws IOException {
        List<Product> resultSet = new ArrayList<Product>();
        failImportItems=0;
        //File inputWorkbook = new File(inputFile);
       // if(inputWorkbook.exists()){
            Workbook w;
            try {
                int i=0;
                AssetManager am = getAssets();
                InputStream in = am.open("product2017.xls");
                w = Workbook.getWorkbook(in);
                Sheet sheet = w.getSheet(0);
                // Loop over column and lines
                Log.i("Row ","id \t name \t barcode \t price");
                for (int row = 1; row < sheet.getRows(); row++) {
                    try {
                        String name,barcode,id,price;
                        id=sheet.getCell(0, row).getContents().replaceAll(" ","");
                        name=sheet.getCell(4, row).getContents();
                        barcode=sheet.getCell(6, row).getContents();
                        price=new BigDecimal(sheet.getCell(1, row).getContents().replaceAll(" ","")).toString();
                        resultSet.add(new Product(i++,name,Double.parseDouble(price),barcode,1, SESSION._USER.getId()));
                    }
                    catch (Exception ex){
                        Log.e("",ex.getMessage());
                        failImportItems++;
                    }


                    /*for(int i=0; i<sheet.getColumns();i++){
                        Cell cell = sheet.getCell(i, row);
                    }

                    if(cell.getContents().equalsIgnoreCase(key)){
                        for (int i = 0; i < sheet.getColumns(); i++) {
                            Cell cel = sheet.getCell(i, row);
                            resultSet.add(cel.getContents());
                        }
                    }*/
                    continue;
                }
                msg = String.format("file have %d products, %d successfully to read, %d errors",sheet.getRows()-1,sheet.getRows()-1-failImportItems,failImportItems);
                //Toast.makeText(getBaseContext(),"fail to add "+failImportItems,Toast.LENGTH_LONG);

            } catch (BiffException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        //}
       // else
      //  {
            // resultSet.add("File not found..!");
      //  }
        if(resultSet.size()==0){
            // resultSet.add("Data not found..!");
        }
        return resultSet;
    }
}
