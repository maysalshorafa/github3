package com.pos.leaders.leaderspossystem.DataBaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pos.leaders.leaderspossystem.DbHelper;
import com.pos.leaders.leaderspossystem.Models.Club;
import com.pos.leaders.leaderspossystem.Tools.SETTINGS;
import com.pos.leaders.leaderspossystem.Tools.Util;
import com.pos.leaders.leaderspossystem.syncposservice.Enums.MessageType;

import java.util.ArrayList;
import java.util.List;

import static com.pos.leaders.leaderspossystem.syncposservice.Util.BrokerHelper.sendToBroker;

/**
 * Created by Win8.1 on 6/21/2017.
 */

public class ClubAdapter {
    public static final String Group_TABLE_NAME = "club";
    // Column Names
    protected static final String Group_COLUMN_Name = "name";
    protected static final String Group_COLUMN__ID = "id";

    protected static final String Group_COLUMN__Descrption = "description";
    protected static final String Group_COLUMN_Type = "type";
    protected static final String Group_COLUMN_Parcent = "parcent";
    protected static final String Group_COLUMN_Amount = "amount";
    protected static final String Group_COLUMN_Point = "point";
    protected static final String Group_COLUMN_DISENABLED = "hide";
    protected static final String Group_COLUMN_BRANCH_ID = "branchId";
    protected static final String Group_COLUMN_Value_Of_Point = "valueOfPoint";



    public static final String DATABASE_CREATE= "CREATE TABLE IF NOT EXISTS club ( `id` INTEGER PRIMARY KEY AUTOINCREMENT,"+"`name` TEXT NOT NULL,"+"'description' Text ,"+"'type' INTEGER  DEFAULT 0,"+" 'parcent'  REAL DEFAULT 0 ,"+" 'amount' REAL DEFAULT 0,"+" 'point' REAL DEFAULT 0 ,"+"`hide` INTEGER DEFAULT 0,"+"`branchId` INTEGER DEFAULT 0,"+" 'valueOfPoint'  REAL DEFAULT 0 )";
    private SQLiteDatabase db;

    // Context of the application using the database.
    private final Context context;
    // Database open/upgrade helper
    private DbHelper dbHelper;

    public ClubAdapter(Context context) {
        this.context = context;
        this.dbHelper = new DbHelper(context);
    }


    public ClubAdapter open() throws SQLException {
        try {
            this.db = dbHelper.getWritableDatabase();
            return this;

        } catch (SQLException s) {
            new Exception("Error with DB Open");
            return this;

        }
    }

    public void close() {
        db.close();
    }

    public SQLiteDatabase getDatabaseInstance() {
        return db;
    }


    public int getRowCount(){
        Cursor cursor = db.rawQuery("select * from " + Group_TABLE_NAME , null);
        return cursor.getCount();
    }

    public long insertEntry( String name,String description, int type, float parcent, double amount, int point,int branchId,double valueOfPoint) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Club group = new Club(Util.idHealth(this.db, Group_TABLE_NAME, Group_COLUMN__ID), name, description, type, parcent, amount, point, false,branchId,valueOfPoint );
        Club boClub=group;
        boClub.setName(Util.getString(boClub.getName()));
        boClub.setDescription(Util.getString(boClub.getDescription()));
        sendToBroker(MessageType.ADD_CLUB, boClub, this.context);

        try {
            long insertResult = insertEntry(group);
            close();
            return insertResult;
        } catch (SQLException ex) {
            Log.e("Club insertEntry", "inserting Entry at " + Group_TABLE_NAME + ": " + ex.getMessage());
            return 0;
        }
    }

    public long insertEntry(Club group) {
        if(db.isOpen()){

        }else {

            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Log.d("testttt",group.toString());
        ContentValues val = new ContentValues();
        //Assign values for each row.
        val.put(Group_COLUMN__ID, group.getClubId());
        val.put(Group_COLUMN_Name, group.getName());
        val.put(Group_COLUMN__Descrption, group.getDescription());
        val.put(Group_COLUMN_Type, group.getType());
        val.put(Group_COLUMN_Parcent, group.getPercent());
        val.put(Group_COLUMN_Amount, group.getAmount());
        val.put(Group_COLUMN_Point, group.getPoint());
        val.put(Group_COLUMN_DISENABLED, group.isHide()?1:0);
        val.put(Group_COLUMN_BRANCH_ID,group.getBranchId());
        val.put(Group_COLUMN_Value_Of_Point,group.getValueOfPoint());

        try {
            return db.insert(Group_TABLE_NAME, null, val);
        } catch (SQLException ex) {
            Log.e("GroupDB insertEntry", "inserting Entry at " + Group_TABLE_NAME + ": " + ex.getMessage());
            return 0;
        }
    }

    public int updateEntry(Club club){
        if(db.isOpen()){

        }else {
                try {
                    open();
                }
                catch (SQLException ex) {
                    Log.d("Exception",ex.toString());
                }
            }

        ClubAdapter clubAdapter =new ClubAdapter(context);
        clubAdapter.open();
        ContentValues val = new ContentValues();
        //Assign values for each row.
        val.put(Group_COLUMN_Name,club.getName());
        val.put(Group_COLUMN__Descrption,club.getDescription());
        val.put(Group_COLUMN_Type,club.getType());
        val.put(Group_COLUMN_Parcent,club.getPercent());
        val.put(Group_COLUMN_Amount,club.getAmount());
        val.put(Group_COLUMN_Point,club.getPoint());
        val.put(Group_COLUMN_BRANCH_ID,club.getBranchId());
        val.put(Group_COLUMN_Value_Of_Point,club.getValueOfPoint());


        try {
            String where = Group_COLUMN__ID + " = ?";
            db.update(Group_TABLE_NAME, val, where, new String[]{club.getClubId() + ""});
            Club c=clubAdapter.getGroupByID(club.getClubId());
            Log.d("UpDate Object",c.toString());
            sendToBroker(MessageType.UPDATE_CLUB, c, this.context);
            clubAdapter.close();
            close();
            return 1;
        } catch (SQLException ex) {
            Log.e("Club insertEntry", "inserting Entry at " + Group_TABLE_NAME + ": " + ex.getMessage());
            return 0;
        }

    }
    public long updateEntryBo(Club club){
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        ClubAdapter clubAdapter =new ClubAdapter(context);
        clubAdapter.open();
        ContentValues val = new ContentValues();
        //Assign values for each row.
        val.put(Group_COLUMN_Name,club.getName());
        val.put(Group_COLUMN__Descrption,club.getDescription());
        val.put(Group_COLUMN_Type,club.getType());
        val.put(Group_COLUMN_Parcent,club.getPercent());
        val.put(Group_COLUMN_Amount,club.getAmount());
        val.put(Group_COLUMN_Point,club.getPoint());
        val.put(Group_COLUMN_BRANCH_ID,club.getBranchId());
        val.put(Group_COLUMN_Value_Of_Point,club.getValueOfPoint());


        try {
            String where = Group_COLUMN__ID + " = ?";
            db.update(Group_TABLE_NAME, val, where, new String[]{club.getClubId() + ""});
            Club c=clubAdapter.getGroupByID(club.getClubId());
            Log.d("UpDate Object",c.toString());
            clubAdapter.close();
            close();
            return 1;
        } catch (SQLException ex) {
            return 0;
        }

    }
    public boolean availableGrouprName(String groupName){
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Cursor cursor=db.query(Group_TABLE_NAME,null,Group_COLUMN_Name+"=?",new String[]{groupName},null,null,null);
        cursor.moveToFirst();
        if(cursor.getCount()>0){
            // group Name not available
            close();
            return false;
        }
        close();
        //group Name available
        return true;
    }

    /*
    public void read(Cursor cursor) {
        cursor.moveToFirst();
        Club.name= cursor.getString(cursor.getColumnIndex(Group_COLUMN_Name));
        Club.description= cursor.getString(cursor.getColumnIndex(Group_COLUMN__Descrption));
        Club.type= Integer.parseInt(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Type)));
        Club.amount= Integer.parseInt(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Amount)));
        Club.parcent=Integer.parseInt( cursor.getString(cursor.getColumnIndex(Group_COLUMN_Parcent)));


        cursor.close();
    }*/

    public Club getGroupInfo(long club_id){
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Club group = null;
        Cursor cursor = db.rawQuery("select * from " + Group_TABLE_NAME + " where id='" + club_id + "'", null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            close();
            return group;
        }
        cursor.moveToFirst();
        group = new Club(Long.parseLong(cursor.getString(cursor.getColumnIndex(Group_COLUMN__ID))),
                cursor.getString(cursor.getColumnIndex(Group_COLUMN_Name)),
                cursor.getString(cursor.getColumnIndex(Group_COLUMN__Descrption)),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Type))),
                (float) Double.parseDouble(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Parcent))),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Amount))),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Point))), Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(Group_COLUMN_DISENABLED))),Integer.parseInt(cursor.getString(cursor.getColumnIndex(Group_COLUMN_BRANCH_ID))),Double.parseDouble(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Value_Of_Point))));
        cursor.close();
       close();
        return group;

    }
    public Club getGroupByID(long id) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Club group = null;
        Cursor cursor = db.query(Group_TABLE_NAME, null, Group_COLUMN__ID + "=? ", new String[]{id + ""}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0)
        {
            group = createNewGroup(cursor);
            cursor.close();
            close();
            return group;
        }
        cursor.close();
        close();
        return group;
    }
    public List<Club> getAllGroup() {
        List<Club> groups = new ArrayList<Club>();
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
            cursor =  db.rawQuery( "select * from "+Group_TABLE_NAME+ " where " + Group_COLUMN_DISENABLED +" = 0 order by id desc", null );
        }else {
            cursor = db.rawQuery("select * from " + Group_TABLE_NAME + " where " + Group_COLUMN_BRANCH_ID + " = "+ SETTINGS.branchId+ " and " + Group_COLUMN_DISENABLED + "=0 order by id desc", null);

        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            groups.add(createNewGroup(cursor));
            cursor.moveToNext();
        }
        close();
        } catch (Exception e) {
            Log.d("exxx",e.toString());
        }
        return groups;
    }

    private Club createNewGroup(Cursor cursor){
        return  new Club(Long.parseLong(cursor.getString(cursor.getColumnIndex(Group_COLUMN__ID))),
                cursor.getString(cursor.getColumnIndex(Group_COLUMN_Name)),
                cursor.getString(cursor.getColumnIndex(Group_COLUMN__Descrption)),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Type))),
                (float) Double.parseDouble(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Parcent))),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Amount))),
                Integer.parseInt(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Point))),
                Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(Group_COLUMN_DISENABLED))),Integer.parseInt(cursor.getString(cursor.getColumnIndex(Group_COLUMN_BRANCH_ID))),Double.parseDouble(cursor.getString(cursor.getColumnIndex(Group_COLUMN_Value_Of_Point))));
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
        ClubAdapter clubAdapter=new ClubAdapter(context);
        clubAdapter.open();
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put(Group_COLUMN_DISENABLED, 1);

        String where = Group_COLUMN__ID + " = ?";
        try {
            db.update(Group_TABLE_NAME, updatedValues, where, new String[]{id + ""});
            Club club=clubAdapter.getClubById(id);
            sendToBroker(MessageType.DELETE_CLUB, club, this.context);
            close();
            return 1;
        } catch (SQLException ex) {
            Log.e("Club deleteEntry", "enable hide Entry at " + Group_TABLE_NAME + ": " + ex.getMessage());
            return 0;
        }
    }
    public long deleteEntryBo(Club club) {
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
        updatedValues.put(Group_COLUMN_DISENABLED, 1);

        String where = Group_COLUMN__ID + " = ?";
        try {
            db.update(Group_TABLE_NAME, updatedValues, where, new String[]{club.getClubId() + ""});
            close();
            return 1;
        } catch (SQLException ex) {
            Log.e("Club deleteEntry", "enable hide Entry at " + Group_TABLE_NAME + ": " + ex.getMessage());
            return 0;
        }
    }
    public Club getClubById(long id) {
        if(db.isOpen()){

        }else {
            try {
                open();
            }
            catch (SQLException ex) {
                Log.d("Exception",ex.toString());
            }
        }
        Club club = null;
        Cursor cursor = db.rawQuery("select * from " + Group_TABLE_NAME + " where id='" + id + "'", null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            close();
            return club;
        }
        cursor.moveToFirst();
        club = new Club(createNewGroup(cursor));
        cursor.close();
        close();
        return club;
    }
    public static String addColumnInteger(String columnName) {
        String dbc = "ALTER TABLE " + Group_TABLE_NAME
                + " add column " + columnName + " INTEGER  DEFAULT 0 ;";
        return dbc;
    }
}

