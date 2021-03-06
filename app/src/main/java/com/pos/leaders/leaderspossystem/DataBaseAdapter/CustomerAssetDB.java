package com.pos.leaders.leaderspossystem.DataBaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pos.leaders.leaderspossystem.DbHelper;
import com.pos.leaders.leaderspossystem.Models.CustomerAssistant;
import com.pos.leaders.leaderspossystem.Tools.Util;
import com.pos.leaders.leaderspossystem.syncposservice.Enums.MessageType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.pos.leaders.leaderspossystem.syncposservice.Util.BrokerHelper.sendToBroker;

/**
 * Created by Win8.1 on 8/27/2017.
 */

public class CustomerAssetDB {

    public static final String CustmerAsset_TabelName = "CustomerAssistant";
    // Column Names
    protected static final String CUSTMER_ASSEST_ID = "id";

    protected static final String ORDER_ID = "order_id";
    protected static final String CustmerAssest_COLUMN_ID = "customerAssistantID";
    protected static final String CUSTMER_ASSEST_COLUMN_AMOUNT = "amount";
    protected static final String CUSTMER_ASSEST_COLUMN_TYPE = "type";
    protected static final String CUSTMER_ASSEST_COLUMN_CASE = "salesCase";
    protected static final String CustmerAssest_COLUMN_CEATEDATE = "saleDate";


    public static final String DATABASE_CREATE = "CREATE TABLE `"+CustmerAsset_TabelName+"` ( `"+ CUSTMER_ASSEST_ID +"` INTEGER PRIMARY KEY AUTOINCREMENT, "+
            "`"+ ORDER_ID +"` INTEGER ,"+
            "`"+CustmerAssest_COLUMN_ID+"` INTEGER NOT NULL, `"+ CUSTMER_ASSEST_COLUMN_AMOUNT +"` REAL, `"+ CUSTMER_ASSEST_COLUMN_TYPE +"` INTEGER DEFAULT 0, `"+ CUSTMER_ASSEST_COLUMN_CASE +"` TEXT, `"+ CustmerAssest_COLUMN_CEATEDATE +"` TIMESTAMP DEFAULT current_timestamp );";

    private SQLiteDatabase db;
    private final Context context;
    // Database open/upgrade helper
    private DbHelper dbHelper;




    public CustomerAssetDB(Context context) {
        this.context = context;
        this.dbHelper = new DbHelper(context);
    }


    public CustomerAssetDB open() throws SQLException {
            this.db = dbHelper.getWritableDatabase();
            return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }



   
    public long insertEntry(long order_id,long user_id,double amount,int type,String salescase,Timestamp saleDate ) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        CustomerAssistant assest = new CustomerAssistant(Util.idHealth(this.db, CustmerAsset_TabelName, CUSTMER_ASSEST_ID), order_id, user_id, amount,type,salescase,saleDate);
        sendToBroker(MessageType.ADD_CUSTOMER_ASSISTANT, assest, this.context);

        try {
            close();
            return insertEntry(assest);
        } catch (SQLException ex) {
            Log.e("AssistantDB insert", "inserting Entry at " + CustmerAsset_TabelName + ": " + ex.getMessage());
            return -1;
        }
    }

    public long insertEntry(CustomerAssistant assest){
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        ContentValues val = new ContentValues();
        //Assign values for each row.

        val.put(CUSTMER_ASSEST_ID, assest.getCustAssistantId());
        val.put(ORDER_ID, assest.getOrderId());
        val.put(CustmerAssest_COLUMN_ID,assest.getCustomerAssistantID() );
        val.put(CUSTMER_ASSEST_COLUMN_AMOUNT, assest.getAmount());
        val.put(CUSTMER_ASSEST_COLUMN_TYPE, assest.getType());
        val.put(CUSTMER_ASSEST_COLUMN_CASE, assest.getSalesCase());
        try {
            return db.insert(CustmerAsset_TabelName, null, val);
        } catch (SQLException ex) {
            Log.e("CustomerAssistant DB insert", "inserting Entry at " + CustmerAsset_TabelName + ": " + ex.getMessage());
            return -1;
        }
    }


    public List<CustomerAssistant> getBetweenTwoDates(long employeeId,long from, long to){
        List<CustomerAssistant> customerAssistants = new ArrayList<CustomerAssistant>();
        List<CustomerAssistant> customerAssistantList = new ArrayList<CustomerAssistant>();
        try {
            if(db.isOpen()){

            }else {
                try {
                    open();
                }
                catch (SQLException ex) {
                    Log.d("Exception",ex.toString());
                }
            }
        Cursor cursor = db.rawQuery("select * from "+ CustmerAsset_TabelName +" where "+ CustmerAssest_COLUMN_ID +" = "+employeeId+" order by "+ CustmerAssest_COLUMN_CEATEDATE +" DESC",null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            customerAssistants.add(createNewAssistent(cursor));
            cursor.moveToNext();
        }
        customerAssistantList=getListBetweenTwoDates(from,to,customerAssistants);
       close();
        } catch (Exception e) {
            Log.d("exxx",e.toString());
        }
        return customerAssistantList;
    }
    public List<CustomerAssistant> getListBetweenTwoDates(long from, long to,List<CustomerAssistant>customerAssistantList){
        List<CustomerAssistant>customerAssistants=new ArrayList<CustomerAssistant>() ;
        for (int i=0;i<customerAssistantList.size();i++){
            if(customerAssistantList.get(i).getCreatedAt().after(new Timestamp(from))&&customerAssistantList.get(i).getCreatedAt().before(new Timestamp(to))){
                customerAssistants.add(customerAssistantList.get(i));
            }
        }
        return customerAssistants;
    }

    private CustomerAssistant createNewAssistent(Cursor cursor){
        try {
            return new CustomerAssistant(Long.parseLong(cursor.getString(cursor.getColumnIndex(CUSTMER_ASSEST_ID))),
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(ORDER_ID))),
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(CustmerAssest_COLUMN_ID))),
                    Double.parseDouble(cursor.getString(cursor.getColumnIndex(CUSTMER_ASSEST_COLUMN_AMOUNT))),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(CUSTMER_ASSEST_COLUMN_TYPE))),
                    cursor.getString(cursor.getColumnIndex(CUSTMER_ASSEST_COLUMN_CASE)),
                    Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(CustmerAssest_COLUMN_CEATEDATE))));
        }
        catch (Exception ex){
            return new CustomerAssistant(Long.parseLong(cursor.getString(cursor.getColumnIndex(CUSTMER_ASSEST_ID))),
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(ORDER_ID))),
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(CustmerAssest_COLUMN_ID))),
                    Double.parseDouble(cursor.getString(cursor.getColumnIndex(CUSTMER_ASSEST_COLUMN_AMOUNT))),
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(CUSTMER_ASSEST_COLUMN_TYPE))),
                    cursor.getString(cursor.getColumnIndex(CUSTMER_ASSEST_COLUMN_CASE)),
                   Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(CustmerAssest_COLUMN_CEATEDATE))));
        }

    }
    public List<CustomerAssistant> getAllSalesMAn() {

        List<CustomerAssistant> users = new ArrayList<CustomerAssistant>();
        Cursor cursor=null;

            cursor = db.rawQuery("select * from " + CustmerAsset_TabelName, null);
            if (cursor != null) {

                while (cursor.moveToNext()) {
                    users.add(createNewAssistent(cursor));
                }}

        cursor.close();

        return users;
    }
    public double getTotalAmountForAssistant( long id,long from , long to){
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        CustomerAssetDB customerAssetDB = new CustomerAssetDB(context);
        customerAssetDB.open();
        List<CustomerAssistant>customerAssistants=new ArrayList<CustomerAssistant>();
        double amount = 0;
        CustomerAssistant customerAssistant=null;
        customerAssistants=customerAssetDB.getBetweenTwoDates(id,from,to);
        for (int i=0;i<customerAssistants.size();i++){
            amount+=customerAssistants.get(i).getAmount();
        }
        close();
        return  amount;
    }
}
