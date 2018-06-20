package com.pos.leaders.leaderspossystem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pos.leaders.leaderspossystem.DataBaseAdapter.ChecksDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.OrderDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.OrderDetailsDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.PaymentDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.ProductDBAdapter;
import com.pos.leaders.leaderspossystem.DataBaseAdapter.EmployeeDBAdapter;
import com.pos.leaders.leaderspossystem.Models.Check;
import com.pos.leaders.leaderspossystem.Models.Order;
import com.pos.leaders.leaderspossystem.Models.OrderDetails;
import com.pos.leaders.leaderspossystem.Models.Payment;
import com.pos.leaders.leaderspossystem.Models.Product;
import com.pos.leaders.leaderspossystem.Printer.InvoiceImg;
import com.pos.leaders.leaderspossystem.Printer.PrintTools;
import com.pos.leaders.leaderspossystem.Tools.CONSTANT;
import com.pos.leaders.leaderspossystem.Tools.DateConverter;
import com.pos.leaders.leaderspossystem.Tools.SESSION;
import com.pos.leaders.leaderspossystem.Tools.SETTINGS;
import com.pos.leaders.leaderspossystem.Tools.SaleManagementListViewAdapter;
import com.pos.leaders.leaderspossystem.Tools.TitleBar;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by KARAM on 26/10/2016.
 * Editing by KARAM on 10/04/2017.
 */
public class OrdersManagementActivity extends AppCompatActivity {

    TextView customer;
    ListView lvOrders;
    EditText etFrom, etTo;
    OrderDBAdapter orderDBAdapter;
    EmployeeDBAdapter userDBAdapter;
    PaymentDBAdapter paymentDBAdapter;
    private static final int DIALOG_FROM_DATE = 825;
    private static final int DIALOG_TO_DATE = 324;
    Date from, to;
    EditText etSearch;
    SaleManagementListViewAdapter adapter;
    View previousView = null;

    List<Order> _saleList;
    List<OrderDetails> orders;
    List<Check> checks;
    List<Order> All_orders;

    private final static int DAY_MINUS_ONE_SECOND = 86399999;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);


        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sales_management);
        TitleBar.setTitleBar(this);
        lvOrders = (ListView) findViewById(R.id.saleManagement_LVSales);
        etFrom = (EditText) findViewById(R.id.saleManagement_ETFrom);
        etTo = (EditText) findViewById(R.id.saleManagement_ETTo);

        etFrom.setFocusable(false);
        etFrom.setText(DateConverter.getBeforeMonth().split(" ")[0]);
        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.setText("");
        etSearch.setHint("Search..");
        etSearch.setFocusable(true);
        etSearch.requestFocus();
        from = DateConverter.stringToDate(DateConverter.getBeforeMonth());

        etTo.setFocusable(false);
        etTo.setText(DateConverter.currentDateTime().split(" ")[0]);
        to = DateConverter.stringToDate(DateConverter.currentDateTime());

        etFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_FROM_DATE);
            }
        });

        etTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TO_DATE);
            }
        });

        orderDBAdapter = new OrderDBAdapter(this);

        userDBAdapter = new EmployeeDBAdapter(this);
        paymentDBAdapter = new PaymentDBAdapter(this);
        paymentDBAdapter.open();
        setDate();

    }

    private void setDate() {
        //List<ORDER> _saleList=new ArrayList<ORDER>();
        orderDBAdapter.open();
        _saleList = orderDBAdapter.getBetweenTwoDates(from.getTime()+ DAY_MINUS_ONE_SECOND, to.getTime());
       // Collections.sort(_saleList, new OutcomeDescComparator());

        All_orders = _saleList;
        orderDBAdapter.close();
        for (Order s : _saleList) {
            userDBAdapter.open();
            s.setUser(userDBAdapter.getEmployeeByID(s.getByUser()));
            userDBAdapter.close();

            paymentDBAdapter.open();
            try {
                s.setPayment(paymentDBAdapter.getPaymentBySaleID(s.getOrderId()).get(0));
            }
            catch (Exception ex){
                //_saleList.remove(s);
                ex.printStackTrace();
            }
            paymentDBAdapter.close();
        }
        Log.i("log", _saleList.toString());
        /*for (ORDER s : saleList) {
			if(DateConverter.dateBetweenTwoDates(from, to, s.getOrder_date())) {
				_saleList.add(s);
			}
		}*/
        adapter = new SaleManagementListViewAdapter(this, R.layout.list_adapter_row_sales_management, _saleList);

        lvOrders.setAdapter(adapter);


        lvOrders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final Order sale = _saleList.get(position);
                OrderDetailsDBAdapter orderDBAdapter = new OrderDetailsDBAdapter(OrdersManagementActivity.this);
                orderDBAdapter.open();
                orders = orderDBAdapter.getOrderBySaleID(sale.getOrderId());
                orderDBAdapter.close();
                ProductDBAdapter productDBAdapter = new ProductDBAdapter(OrdersManagementActivity.this);
                productDBAdapter.open();
                for (OrderDetails o : orders) {
                    if (o.getProductId() != -1) {
                        o.setProduct(productDBAdapter.getProductByID(o.getProductId()));
                    } else {
                        o.setProduct(new Product(-1, getApplicationContext().getResources().getString(R.string.general), o.getUnitPrice(), SESSION._EMPLOYEE.getEmployeeId()));
                    }
                }
                productDBAdapter.close();

                PaymentDBAdapter paymentDBAdapter = new PaymentDBAdapter(OrdersManagementActivity.this);
                paymentDBAdapter.open();
                final List<Payment> payments = paymentDBAdapter.getPaymentBySaleID(sale.getOrderId());

                sale.setPayment(payments.get(0));

                paymentDBAdapter.close();

                checks = new ArrayList<Check>();

                for (Payment p : payments) {
                    switch (p.getPaymentWay()) {
                        case CONSTANT.CHECKS:
                            ChecksDBAdapter checksDBAdapter = new ChecksDBAdapter(OrdersManagementActivity.this);
                            checksDBAdapter.open();
                            checks.addAll(checksDBAdapter.getPaymentBySaleID(sale.getOrderId()));
                            checksDBAdapter.close();
                            break;
                        case CONSTANT.CASH:
                            break;
                        case CONSTANT.CREDIT_CARD:
                            break;
                    }
                }
                FrameLayout fr = (FrameLayout) view.findViewById(R.id.listSaleManagement_FLMore);
                if (previousView == null) {
                    fr.setVisibility(View.VISIBLE);
                    previousView = view;
                } else {
                    fr.setVisibility(View.VISIBLE);
                    previousView.findViewById(R.id.listSaleManagement_FLMore).setVisibility(View.GONE);
                    previousView.setBackgroundColor(getResources().getColor(R.color.transparent));
                    previousView = view;
                }
                final InvoiceImg invoiceImg = new InvoiceImg(OrdersManagementActivity.this);


                sale.setOrders(orders);
                sale.setUser(SESSION._EMPLOYEE);
                //region Print Button
                final Button print = (Button) view.findViewById(R.id.listSaleManagement_BTView);
                print.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //send customerName copy from the in voice
                        new AlertDialog.Builder(OrdersManagementActivity.this)
                                .setTitle(getString(R.string.copyinvoice))
                                .setMessage(getString(R.string.print_copy_invoice))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (checks.size() > 0){
                                            Intent i = new Intent(OrdersManagementActivity.this, SalesHistoryCopySales.class);
                                            SETTINGS.copyInvoiceBitMap = invoiceImg.normalInvoice(sale.getOrderId(), orders, sale, true, SESSION._EMPLOYEE, checks);
                                            startActivity(i);
                                            // print(invoiceImg.normalInvoice(sale.getCashPaymentId(), orders, sale, true, SESSION._EMPLOYEE, checks));
                                        }
                                        else{
                                            Intent i = new Intent(OrdersManagementActivity.this, SalesHistoryCopySales.class);
                                            SETTINGS.copyInvoiceBitMap =invoiceImg.normalInvoice(sale.getOrderId(), orders, sale, true, SESSION._EMPLOYEE, null);
                                            startActivity(i);
                                         // print(invoiceImg.normalInvoice(sale.getCashPaymentId(), orders, sale, true, SESSION._EMPLOYEE, null));

                                        }
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
                //endregion Print Button

                //region Replacement Note Button

                Button btnRN = (Button) view.findViewById(R.id.listSaleManagement_BTReturn);
                btnRN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderDBAdapter saleDBAdapter = new OrderDBAdapter(OrdersManagementActivity.this);
                        saleDBAdapter.open();
                        sale.setReplacementNote(sale.getReplacementNote() + 1);
                        saleDBAdapter.updateEntry(sale);
                        saleDBAdapter.close();

                        print(invoiceImg.replacmentNote(sale, false));
                    }
                });
                //endregion Replacement Note Button

                //region Cancellation ORDER Button

                Button btnCan = (Button) view.findViewById(R.id.listSaleManagement_BTCancel);
                btnCan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderDBAdapter saleDBAdapter = new OrderDBAdapter(OrdersManagementActivity.this);
                        saleDBAdapter.open();
                        saleDBAdapter.deleteEntry(sale.getOrderId());
                        if (checks.size() > 0)
                            print(invoiceImg.cancelingInvoice(sale, false, checks));
                        else
                            print(invoiceImg.cancelingInvoice(sale, false, null));
                        sale.setPayment(new Payment(payments.get(0)));
                        long sID = saleDBAdapter.insertEntry(SESSION._EMPLOYEE.getEmployeeId(), new Timestamp(System.currentTimeMillis()), sale.getReplacementNote(), true, sale.getTotalPrice() * -1, sale.getTotalPaidAmount() * -1, sale.getCustomerId(), sale.getCustomer_name());

                        saleDBAdapter.close();
                        PaymentDBAdapter paymentDBAdapter1 = new PaymentDBAdapter(OrdersManagementActivity.this);
                        paymentDBAdapter1.open();
                        paymentDBAdapter1.insertEntry(sale.getPayment().getPaymentWay(), sale.getTotalPrice() * -1, sID);
                        paymentDBAdapter1.close();
                        //// TODO: 19/01/2017 cancel this sale and print return note and mony back by the payment way
                    }
                });
                //endregion Cancellation ORDER Button


                previousView.setBackgroundColor(getResources().getColor(R.color.list_background_color));
            }
        });
        etSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                etSearch.setFocusable(true);
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lvOrders.setTextFilterEnabled(true);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                _saleList = new ArrayList<Order>();
                String word = etSearch.getText().toString();

                if (!word.equals("")) {
                    for (Order c : All_orders) {

                        if (c.getUser().getEmployeeName().toLowerCase().contains(word.toLowerCase()) ||(c.getOrderId() + "").contains(word.toLowerCase())
                                || (c.getCreatedAt() + "").contains(word.toLowerCase())) {
                            _saleList.add(c);

                        }
                    }
                } else {
                    _saleList = All_orders;
                }
                adapter = new SaleManagementListViewAdapter(getApplicationContext(), R.layout.list_adapter_row_sales_management, _saleList);

                lvOrders.setAdapter(adapter);
            }
        });

    }


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_FROM_DATE) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, onFromDateSetListener, Integer.parseInt(from.toString().split(" ")[5]), from.getMonth(), Integer.parseInt(from.toString().split(" ")[2]));
            //datePickerDialog.getDatePicker().setMaxDate(to.getTime());
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            return datePickerDialog;
        } else if (id == DIALOG_TO_DATE) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, onToDateSetListener, Integer.parseInt(to.toString().split(" ")[5]), to.getMonth(), Integer.parseInt(to.toString().split(" ")[2]));
            //datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
            //datePickerDialog.getDatePicker().setMinDate(from.getTime());
            datePickerDialog.getDatePicker().setCalendarViewShown(false);
            return datePickerDialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener onFromDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            etFrom.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            from = DateConverter.stringToDate(year + "-" + (month + 1) + "-" + dayOfMonth + " 00:00:00");
            view.setMaxDate(to.getTime());
            setDate();
        }
    };

    private DatePickerDialog.OnDateSetListener onToDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            etTo.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
            to = DateConverter.stringToDate(year + "-" + (month + 1) + "-" + dayOfMonth + " 00:00:00");
            view.setMinDate(from.getTime());
            setDate();
        }
    };

    private void print(Bitmap bitmap) {
        PrintTools printTools = new PrintTools(this);
        printTools.PrintReport(bitmap);
        /*POSInterfaceAPI posInterfaceAPI = new POSUSBAPI(SalesManagementActivity.this);

        int i = posInterfaceAPI.OpenDevice();
        POSSDK pos = new POSSDK(posInterfaceAPI);

        pos.textSelectCharSetAndCodePage(POSSDK.CharacterSetUSA, 15);
        pos.systemSelectPrintMode(0);
        pos.systemFeedLine(1);
        pos.imageStandardModeRasterPrint(bitmap, CONSTANT.PRINTER_PAGE_WIDTH);
        pos.systemFeedLine(3);
        pos.systemCutPaper(66, 0);

        posInterfaceAPI.CloseDevice();*/
    }

}
/**class OutcomeDescComparator implements Comparator<ORDER>
{
    public int compare(ORDER fSale, ORDER lSale) {
        return lSale.getOrder_date().compareTo(fSale.getOrder_date());
    }
}*/
