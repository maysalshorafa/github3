package com.pos.leaders.leaderspossystem.DataBaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pos.leaders.leaderspossystem.DbHelper;
import com.pos.leaders.leaderspossystem.Models.Employee;
import com.pos.leaders.leaderspossystem.Tools.SETTINGS;
import com.pos.leaders.leaderspossystem.Tools.Util;
import com.pos.leaders.leaderspossystem.syncposservice.Enums.MessageType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.pos.leaders.leaderspossystem.syncposservice.Util.BrokerHelper.sendToBroker;

/**
 * Created by Karam on 17/10/2016.
 */

public class EmployeeDBAdapter {
    //Table name
    public static final String EMPLOYEE_TABLE_NAME = "employees";
    //column names
    protected static final String EMPLOYEE_COLUMN_ID = "id";
    protected static final String EMPLOYEE_COLUMN_EMPLOYEE_NAME = "employeeName";
    protected static final String EMPLOYEE_COLUMN_PASSWORD = "pwd";
    protected static final String EMPLOYEE_COLUMN_FIRSTNAME = "firstName";
    protected static final String EMPLOYEE_COLUMN_LASTNAME = "lastName";
    protected static final String EMPLOYEE_COLUMN_CREATINGDATE = "visitDate";
    protected static final String EMPLOYEE_COLUMN_DISENABLED = "hide";
    protected static final String EMPLOYEE_COLUMN_PHONE_NUMBER = "phoneNumber";
    protected static final String EMPLOYEE_COLUMN_DISCOUNTINPERCENTAGE = "present";
    protected static final String EMPLOYEE_COLUMN_HOURLYWAGE = "hourlyWage";
    protected static final String EMPLOYEE_COLUMN_BRANCH_ID = "branchId";


    // TODO: Create public field for each column in your table.
    // SQL Statement to create a new database.
    public static final String DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS employees ( `id` INTEGER PRIMARY KEY AUTOINCREMENT,`employeeName` TEXT UNIQUE, `firstName` TEXT NOT NULL, `lastName` TEXT, `visitDate` TIMESTAMP NOT NULL DEFAULT current_timestamp,`pwd` TEXT , `hide` INTEGER DEFAULT 0, `phoneNumber` TEXT, `present` REAL NOT NULL DEFAULT 0, `hourlyWage` REAL DEFAULT 0.0, `branchId` INTEGER DEFAULT 0 )";
    // Variable to hold the database instance
    public SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DbHelper dbHelper;

    public EmployeeDBAdapter(Context context) {
        this.context = context;
        this.dbHelper = new DbHelper(context);
    }

    public EmployeeDBAdapter open() throws SQLException {
        try {

            this.db = dbHelper.getWritableDatabase();

        } catch (SQLException s) {
          Log.d("test","Error with DB Open");
        }
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }

    public long insertEntry(String employeeName, String password, String firstName, String lastName, String phoneNumber, Double persent, Double hourlyWag,int branchId) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Employee u = new Employee(Util.idHealth(this.db, EMPLOYEE_TABLE_NAME, EMPLOYEE_COLUMN_ID), employeeName, password, firstName, lastName, new Timestamp(System.currentTimeMillis()), false, phoneNumber, persent, hourlyWag,branchId);
        Employee boEmployee = u;
        boEmployee.setEmployeeName(Util.getString(boEmployee.getEmployeeName()));
        boEmployee.setPassword(Util.getString(boEmployee.getPassword()));
        boEmployee.setFirstName(Util.getString(boEmployee.getFirstName()));
        boEmployee.setLastName(Util.getString(boEmployee.getLastName()));
        boEmployee.setPhoneNumber(Util.getString(boEmployee.getPhoneNumber()));
        sendToBroker(MessageType.ADD_EMPLOYEE, boEmployee, this.context);

        try {
            long insertResult = insertEntry(u);
            close();
            return insertResult;
        } catch (SQLException ex) {
            Log.e("UserDB insertEntry", "inserting Entry at " + EMPLOYEE_TABLE_NAME + ": " + ex.getMessage());
            return 0;
        }
    }

    public long insertEntry(Employee employee) throws  SQLException {
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
        val.put(EMPLOYEE_COLUMN_ID, employee.getEmployeeId());
        val.put(EMPLOYEE_COLUMN_EMPLOYEE_NAME, employee.getEmployeeName());
        val.put(EMPLOYEE_COLUMN_PASSWORD, employee.getPassword());
        val.put(EMPLOYEE_COLUMN_FIRSTNAME, employee.getFirstName());
        val.put(EMPLOYEE_COLUMN_LASTNAME, employee.getLastName());
        val.put(EMPLOYEE_COLUMN_DISENABLED, employee.isHide() ? 1 : 0);
        val.put(EMPLOYEE_COLUMN_PHONE_NUMBER, employee.getPhoneNumber());
        val.put(EMPLOYEE_COLUMN_DISCOUNTINPERCENTAGE, employee.getPresent());
        val.put(EMPLOYEE_COLUMN_HOURLYWAGE, employee.getHourlyWage());
        val.put(EMPLOYEE_COLUMN_BRANCH_ID,employee.getBranchId());

        long id = db.insertOrThrow(EMPLOYEE_TABLE_NAME, null, val);
close();
        return id;
    }

    public Employee getEmployeeByID(long id) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Employee employee = null;
        Cursor cursor = db.query(EMPLOYEE_TABLE_NAME, null, EMPLOYEE_COLUMN_ID + "=? ", new String[]{id + ""}, null, null, null);
        //Cursor cursor = db.rawQuery("select * from " + EMPLOYEE_TABLE_NAME + " where id='" + id + "'", null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) // UserName Exist
        {
            employee = createNewEmployee(cursor);
            cursor.close();
            return employee;
        }
        cursor.close();
        close();
        return employee;
    }

    public Employee logIn(String employeeName, String Passowrd) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Cursor cursor = db.query(EMPLOYEE_TABLE_NAME, null, EMPLOYEE_COLUMN_EMPLOYEE_NAME + "=? and " + EMPLOYEE_COLUMN_PASSWORD + "=?", new String[]{employeeName, Passowrd}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) // UserName Not Exist
        {
            String ID = cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_ID));

            Employee u = createNewEmployee(cursor);

            cursor.close();
            Log.i("Log in", u.toString());
            close();
            return u;
        }
        cursor.close();
        close();
        return null;
    }

    public Employee logIn(String Passowrd) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        long userID = 0;
        Cursor cursor = db.query(EMPLOYEE_TABLE_NAME, null, EMPLOYEE_COLUMN_PASSWORD + "=?", new String[]{Passowrd}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) // UserName Not Exist
        {
            String ID = cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_ID));

            Employee u = createNewEmployee(cursor);

            cursor.close();
            Log.i("Log in", u.toString());
            close();
            return u;
        }
        cursor.close();
        close();
        return null;
    }

    public int deleteEntry(long id) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        EmployeeDBAdapter employeeDBAdapter=new EmployeeDBAdapter(context);
        employeeDBAdapter.open();
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put(EMPLOYEE_COLUMN_DISENABLED, 1);

        String where = EMPLOYEE_COLUMN_ID + " = ?";
        try {
            db.update(EMPLOYEE_TABLE_NAME, updatedValues, where, new String[]{id + ""});
            Employee user = employeeDBAdapter.getEmployeeByID(id);
            sendToBroker(MessageType.DELETE_EMPLOYEE, user, this.context);
            close();
            return 1;
        } catch (SQLException ex) {
            Log.e("deleteEntry", "enable hide Entry at " + EMPLOYEE_TABLE_NAME + ": " + ex.getMessage());
            return 0;
        }
    }
    public long deleteEntryBo(Employee employee) {
        if(db.isOpen()){

        }else {
                try {
                    open();
                }
                catch (SQLException ex) {
                    Log.d("Exception",ex.toString());
                }
        }
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put(EMPLOYEE_COLUMN_DISENABLED, 1);

        String where = EMPLOYEE_COLUMN_ID + " = ?";
        try {
            db.update(EMPLOYEE_TABLE_NAME, updatedValues, where, new String[]{employee.getEmployeeId() + ""});
            close();
            return 1;
        } catch (SQLException ex) {
            Log.e("employeeDB deleteEntry", "enable hide Entry at " + EMPLOYEE_TABLE_NAME + ": " + ex.getMessage());
            return 0;
        }
    }

    public void updateEntry(Employee employee) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        EmployeeDBAdapter userDBAdapter = new EmployeeDBAdapter(context);
        userDBAdapter.open();
        ContentValues val = new ContentValues();
        //Assign values for each row.
        val.put(EMPLOYEE_COLUMN_EMPLOYEE_NAME, employee.getEmployeeName());
        val.put(EMPLOYEE_COLUMN_PASSWORD, employee.getPassword());
        val.put(EMPLOYEE_COLUMN_FIRSTNAME, employee.getFirstName());
        val.put(EMPLOYEE_COLUMN_LASTNAME, employee.getLastName());
        val.put(EMPLOYEE_COLUMN_PHONE_NUMBER, employee.getPhoneNumber());
        val.put(EMPLOYEE_COLUMN_DISCOUNTINPERCENTAGE, employee.getPresent());
        val.put(EMPLOYEE_COLUMN_HOURLYWAGE, employee.getHourlyWage());
        val.put(EMPLOYEE_COLUMN_BRANCH_ID,employee.getBranchId());

        String where = EMPLOYEE_COLUMN_ID + " = ?";
        db.update(EMPLOYEE_TABLE_NAME, val, where, new String[]{employee.getEmployeeId() + ""});
        Employee u=userDBAdapter.getEmployeeByID(employee.getEmployeeId());
        Log.d("Update Object",u.toString());
        sendToBroker(MessageType.UPDATE_EMPLOYEE, u, this.context);
        userDBAdapter.close();
        close();
    }
    public long updateEntryBo(Employee employee) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        EmployeeDBAdapter userDBAdapter = new EmployeeDBAdapter(context);
        userDBAdapter.open();
        ContentValues val = new ContentValues();
        //Assign values for each row.
        val.put(EMPLOYEE_COLUMN_EMPLOYEE_NAME, employee.getEmployeeName());
        val.put(EMPLOYEE_COLUMN_PASSWORD, employee.getPassword());
        val.put(EMPLOYEE_COLUMN_FIRSTNAME, employee.getFirstName());
        val.put(EMPLOYEE_COLUMN_LASTNAME, employee.getLastName());
        val.put(EMPLOYEE_COLUMN_PHONE_NUMBER, employee.getPhoneNumber());
        val.put(EMPLOYEE_COLUMN_DISCOUNTINPERCENTAGE, employee.getPresent());
        val.put(EMPLOYEE_COLUMN_HOURLYWAGE, employee.getHourlyWage());
        val.put(EMPLOYEE_COLUMN_BRANCH_ID,employee.getBranchId());

        try {
            String where = EMPLOYEE_COLUMN_ID + " = ?";
            db.update(EMPLOYEE_TABLE_NAME, val, where, new String[]{employee.getEmployeeId() + ""});
            Employee u=userDBAdapter.getEmployeeByID(employee.getEmployeeId());
            Log.d("Update Object",u.toString());
            userDBAdapter.close();
            close();
            return 1;
        } catch (SQLException ex) {
            return 0;
        }

    }

    public boolean availableEmployeeName(String employeeName) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Cursor cursor = db.query(EMPLOYEE_TABLE_NAME, null, EMPLOYEE_COLUMN_EMPLOYEE_NAME + "=?", new String[]{employeeName}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            // Employee Name not available
            close();
            return false;
        }
        close();
        // Employee Name available
        return true;
    }
    public boolean availablePassWord(String password) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Cursor cursor = db.query(EMPLOYEE_TABLE_NAME, null, EMPLOYEE_COLUMN_PASSWORD + "=?", new String[]{password}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            // Employee Name not available
            close();
            return false;
        }
        // Employee Name available
        close();
        return true;
    }
    public List<Employee> getAllEmployee() {
        List<Employee> employee = new ArrayList<Employee>();
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
            Cursor cursor=null;
        if(SETTINGS.enableAllBranch) {
            cursor =  db.rawQuery( "select * from "+EMPLOYEE_TABLE_NAME+" where "+  EMPLOYEE_COLUMN_DISENABLED +" = 0 order by id desc", null );
        }else {
            cursor = db.rawQuery("select * from " + EMPLOYEE_TABLE_NAME +" where " + EMPLOYEE_COLUMN_BRANCH_ID + " = "+ SETTINGS.branchId+ " and " + EMPLOYEE_COLUMN_DISENABLED + "=0 order by id desc", null);

        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            employee.add(createNewEmployee(cursor));
            cursor.moveToNext();
        }
       close();
        } catch (Exception e) {
            Log.d("exxx",e.toString());
        }
        return employee;
    }

    public List<Employee> getAllSalesMAn() {
        List<Long> salesManId = new ArrayList<Long>();
        List<Employee> employees = new ArrayList<Employee>();
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
            EmployeePermissionsDBAdapter employeePermissionsDBAdapter = new EmployeePermissionsDBAdapter(context);
        employeePermissionsDBAdapter.open();
        salesManId = employeePermissionsDBAdapter.getSalesManId();

        Cursor cursor = null;
        for (int i = 0; i < salesManId.size(); i++) {
            if(SETTINGS.enableAllBranch) {
                cursor =  db.rawQuery( "select * from "+EMPLOYEE_TABLE_NAME+ " where " + EMPLOYEE_COLUMN_ID + " = " + salesManId.get(i)+" and "+  EMPLOYEE_COLUMN_DISENABLED +" = 0 order by id desc", null );
            }else {
                cursor = db.rawQuery("select * from " + EMPLOYEE_TABLE_NAME + " where " + EMPLOYEE_COLUMN_ID + " = " + salesManId.get(i)+" and " + EMPLOYEE_COLUMN_BRANCH_ID + " = "+ SETTINGS.branchId+ " and " + EMPLOYEE_COLUMN_DISENABLED + "=0 order by id desc", null);

            }
            if (cursor != null) {

                while (cursor.moveToNext()) {
                    employees.add(createNewEmployee(cursor));
                }
            }
        }
        cursor.close();
        close();
        } catch (Exception e) {
            Log.d("exxx",e.toString());
        }
        return employees;
    }

    private Employee createNewEmployee(Cursor cursor) {
        return new Employee(Long.parseLong(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_ID)))
                , cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_EMPLOYEE_NAME)), cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_PASSWORD))
                , cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_FIRSTNAME)), cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_LASTNAME))
                , Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_CREATINGDATE)))
                , Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_DISENABLED)))
                , cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_PHONE_NUMBER))
                , Double.parseDouble(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_DISCOUNTINPERCENTAGE)))
                , Double.parseDouble(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_HOURLYWAGE))),Integer.parseInt(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_BRANCH_ID)))
        );
    }

    public String getEmployeesName(long id) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        String employeesName = "";
        Employee employee = null;
        Cursor cursor = db.rawQuery("select * from " + EMPLOYEE_TABLE_NAME + " where " + EMPLOYEE_COLUMN_ID + " = " + id, null);
        cursor.moveToFirst();
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return employeesName;
        }
        cursor.moveToFirst();
        employee = new Employee(Long.parseLong(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_ID)))
                , cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_EMPLOYEE_NAME)), cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_PASSWORD))
                , cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_FIRSTNAME)), cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_LASTNAME))
                , Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_CREATINGDATE)))
                , Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_DISENABLED)))
                , cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_PHONE_NUMBER))
                , Double.parseDouble(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_DISCOUNTINPERCENTAGE)))
                , Double.parseDouble(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_HOURLYWAGE)))
                ,Integer.parseInt(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_BRANCH_ID)))
        );
        cursor.close();
        close();
        return employee.getFullName();
    }
    public Boolean isValidPassword(String password) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Cursor cursor = db.query(EMPLOYEE_TABLE_NAME, null, EMPLOYEE_COLUMN_PASSWORD + "=?", new String[]{password}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
close();
            return true;
        }
        close();
        return false;
    }
    public Employee getEmployeesByPassword(String password) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Employee employee = null;
        Cursor cursor = db.query(EMPLOYEE_TABLE_NAME, null, EMPLOYEE_COLUMN_PASSWORD + "=?", new String[]{password}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            close();
            return employee;
        }
        cursor.moveToFirst();
        employee = new Employee(Long.parseLong(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_ID)))
                , cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_EMPLOYEE_NAME)), cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_PASSWORD))
                , cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_FIRSTNAME)), cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_LASTNAME))
                , Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_CREATINGDATE)))
                , Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_DISENABLED)))
                , cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_PHONE_NUMBER))
                , Double.parseDouble(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_DISCOUNTINPERCENTAGE)))
                , Double.parseDouble(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_HOURLYWAGE)))
                ,Integer.parseInt(cursor.getString(cursor.getColumnIndex(EMPLOYEE_COLUMN_BRANCH_ID)))
        );
        cursor.close();
        close();
        return employee;
    }
    public static String addColumnInteger(String columnName) {
        String dbc = "ALTER TABLE " + EMPLOYEE_TABLE_NAME
                + " add column " + columnName + " INTEGER  DEFAULT 0 ;";
        return dbc;
    }
}