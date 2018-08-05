package com.pos.leaders.leaderspossystem.DataBaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pos.leaders.leaderspossystem.DbHelper;
import com.pos.leaders.leaderspossystem.Models.Category;
import com.pos.leaders.leaderspossystem.Tools.Util;
import com.pos.leaders.leaderspossystem.syncposservice.Enums.MessageType;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.pos.leaders.leaderspossystem.syncposservice.Util.BrokerHelper.sendToBroker;

/**
 * Created by KARAM on 18/10/2016.
 */

public class CategoryDBAdapter {
    // Table Name
    public static final String CATEGORY_TABLE_NAME = "Category";
    // Column Names
    protected static final String CATEGORY_COLUMN_ID = "id";
    protected static final String CATEGORY_COLUMN_NAME = "name";
    protected static final String CATEGORY_COLUMN_CREATINGDATE = "creatingDate";
    protected static final String CATEGORY_COLUMN_BYUSER = "byEmployee";
    protected static final String CATEGORY_COLUMN_DISENABLED = "hide";

    public static final String DATABASE_CREATE = "CREATE TABLE Category ( `id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "`name` TEXT NOT NULL , `creatingDate` TIMESTAMP NOT NULL DEFAULT current_timestamp, " +
            "`byEmployee` INTEGER, `hide` INTEGER DEFAULT 0, FOREIGN KEY(`byEmployee`) REFERENCES `employees.id` )";
    public static final String DATABASE_UPDATE_FROM_V1_TO_V2 = "alter table departments rename to Category;";
    // Variable to hold the database instance
    private SQLiteDatabase db;
    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DbHelper dbHelper;


    public CategoryDBAdapter(Context context) {
        this.context = context;
        this.dbHelper = new DbHelper(context);
    }

    public CategoryDBAdapter open() throws SQLException {
        this.db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }


    public long insertEntry(String name, long byUser) {
        Category department = new Category(Util.idHealth(this.db, CATEGORY_TABLE_NAME, CATEGORY_COLUMN_ID), name, new Timestamp(System.currentTimeMillis()), byUser, false);
        Category boDepartment = department;
        boDepartment.setName(Util.getString(boDepartment.getName()));
        Log.d("test", boDepartment.getName());
        sendToBroker(MessageType.ADD_CATEGORY, boDepartment, this.context);

        try {
            return insertEntry(department);
        } catch (SQLException ex) {
            Log.e("DepartmentDB insert", "inserting Entry at " + CATEGORY_TABLE_NAME + ": " + ex.getMessage());
            return -1;
        }
    }

    public long insertEntry(Category department) {
        ContentValues val = new ContentValues();
        //Assign values for each row.

        val.put(CATEGORY_COLUMN_ID, department.getCategoryId());
        val.put(CATEGORY_COLUMN_NAME, department.getName());
        val.put(CATEGORY_COLUMN_BYUSER, department.getByUser());
        val.put(CATEGORY_COLUMN_DISENABLED, department.isHide() ? 1 : 0);
        val.put(CATEGORY_COLUMN_CREATINGDATE, String.valueOf(department.getCreatedAt()));

        try {

            return db.insert(CATEGORY_TABLE_NAME, null, val);
        } catch (SQLException ex) {
            Log.e("DepartmentDB insert", "inserting Entry at " + CATEGORY_TABLE_NAME + ": " + ex.getMessage());
            return -1;
        }

    }

    public Category getDepartmentByID(long id) {
        Category department = null;
        Cursor cursor = db.rawQuery("select * from " + CATEGORY_TABLE_NAME + " where id='" + id + "'", null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return department;
        }
        cursor.moveToFirst();
        department = new Category(id, cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_NAME)),
                Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_CREATINGDATE))),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_BYUSER))), Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_DISENABLED))));
        cursor.close();

        return department;
    }

    public int deleteEntry(long id) {
        CategoryDBAdapter departmentDBAdapter=new CategoryDBAdapter(context);
        departmentDBAdapter.open();
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put(CATEGORY_COLUMN_DISENABLED, 1);

        String where = CATEGORY_COLUMN_ID + " = ?";
        try {
            db.update(CATEGORY_TABLE_NAME, updatedValues, where, new String[]{id + ""});
            Category department = departmentDBAdapter.getDepartmentByID(id);
            sendToBroker(MessageType.DELETE_CATEGORY, department, this.context);
            return 1;
        } catch (SQLException ex) {
            Log.e("Category DB delete", "enable hide Entry at " + CATEGORY_TABLE_NAME + ": " + ex.getMessage());
            return 0;
        }
    }
    public long deleteEntryBo(Category department) {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put(CATEGORY_COLUMN_DISENABLED, 1);

        String where = CATEGORY_COLUMN_ID + " = ?";
        try {
            db.update(CATEGORY_TABLE_NAME, updatedValues, where, new String[]{department.getCategoryId() + ""});
            return 1;
        } catch (SQLException ex) {
            Log.e("Category DB delete", "enable hide Entry at " + CATEGORY_TABLE_NAME + ": " + ex.getMessage());
            return 0;
        }
    }

    public long updateEntryBo(Category department) {
        CategoryDBAdapter departmentDBAdapter = new CategoryDBAdapter(context);
        departmentDBAdapter.open();
        ContentValues val = new ContentValues();
        //Assign values for each row.
        val.put(CATEGORY_COLUMN_NAME, department.getName());
        val.put(CATEGORY_COLUMN_BYUSER, department.getByUser());
        val.put(CATEGORY_COLUMN_DISENABLED, department.isHide());
        try {
            String where = CATEGORY_COLUMN_ID + " = ?";
            db.update(CATEGORY_TABLE_NAME, val, where, new String[]{department.getCategoryId() + ""});
            Category d=departmentDBAdapter.getDepartmentByID(department.getCategoryId());
            Log.d("Update object",d.toString());
            departmentDBAdapter.close();
            return 1;
        } catch (SQLException ex) {
            return 0;
        }
    }

    public void updateEntry(Category department) {
        CategoryDBAdapter departmentDBAdapter = new CategoryDBAdapter(context);
        departmentDBAdapter.open();
        ContentValues val = new ContentValues();
        //Assign values for each row.
        val.put(CATEGORY_COLUMN_NAME, department.getName());
        val.put(CATEGORY_COLUMN_BYUSER, department.getByUser());
        val.put(CATEGORY_COLUMN_DISENABLED, department.isHide());

        String where = CATEGORY_COLUMN_ID + " = ?";
        db.update(CATEGORY_TABLE_NAME, val, where, new String[]{department.getCategoryId() + ""});
        Category d=departmentDBAdapter.getDepartmentByID(department.getCategoryId());
        Log.d("Update object",d.toString());
        sendToBroker(MessageType.UPDATE_CATEGORY, d, this.context);
        departmentDBAdapter.close();
    }

    public List<Category> getAllDepartments() {
        List<Category> departmentList = new ArrayList<Category>();

        Cursor cursor = db.rawQuery("select * from " + CATEGORY_TABLE_NAME + " where " + CATEGORY_COLUMN_DISENABLED + "=0 order by id desc", null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            departmentList.add(new Category(Long.parseLong(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_NAME)),
                    Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_CREATINGDATE))),
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_BYUSER))),
                    Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_DISENABLED)))));
            cursor.moveToNext();
        }

        return departmentList;
    }

    public List<Category> getAllUserDepartments(long userId) {
        List<Category> userDepartmentList = new ArrayList<Category>();
        List<Category> departmentList = getAllDepartments();
        for (Category d : departmentList) {
            if (d.getByUser() == userId)
                userDepartmentList.add(d);
        }
        return userDepartmentList;
    }

    public List<Category> getAllDepartmentByHint(String hint) {
        List<Category> departmentList = new ArrayList<Category>();

        Cursor cursor = db.rawQuery("select * from " + CATEGORY_TABLE_NAME + " where " + CATEGORY_COLUMN_NAME + " like '%" +
                hint + "%' " + "and " + CATEGORY_COLUMN_DISENABLED + "=0 order by id desc  ", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            departmentList.add(makeDepartment(cursor));
            cursor.moveToNext();
        }

        return departmentList;
    }

    private Category makeDepartment(Cursor cursor) {
        try {
            Category d = new Category(new Category(Long.parseLong(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_NAME)),
                    Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_CREATINGDATE))),
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_BYUSER))),
                    Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_DISENABLED)))));

            return d;
        } catch (Exception ex) {
            Category d = new Category(new Category(Long.parseLong(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_NAME)),
                    Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_CREATINGDATE))),
                    Long.parseLong(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_BYUSER))),
                    Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN_DISENABLED)))));

            return d;
        }
    }
    public boolean availableDepartmentName(String departmentName) {
        Cursor cursor = db.query(CATEGORY_TABLE_NAME, null, CATEGORY_COLUMN_NAME + "=?", new String[]{departmentName}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            // Category Name not available
            return false;
        }
        // Category Name available
        return true;
    }
}