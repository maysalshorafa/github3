package com.pos.leaders.leaderspossystem.DataBaseAdapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pos.leaders.leaderspossystem.DbHelper;
import com.pos.leaders.leaderspossystem.Models.City;
import com.pos.leaders.leaderspossystem.Models.Group;
import com.pos.leaders.leaderspossystem.Models.UserPermissions;
import com.pos.leaders.leaderspossystem.Tools.Util;
import com.pos.leaders.leaderspossystem.syncposservice.Enums.MessageType;

import static com.pos.leaders.leaderspossystem.syncposservice.Util.BrokerHelper.sendToBroker;

/**
 * Created by KARAM on 29/10/2016.
 */

public class UserPermissionsDBAdapter {
	// Table Name
	protected static final String USERPERMISSIONS_TABLE_NAME = "userPermissions";
	// Column Names
	protected static final String USERPERMISSIONS_COLUMN_USERID = "userId";
	protected static final String USERPERMISSIONS_COLUMN_PERMISSIONSID = "permissionId";

	protected static final String USERPERMISSIONS_COLUMN_ID = "id";

	public static final String DATABASE_CREATE = "CREATE TABLE `userPermissions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `userId` INTEGER , `permissionId` INTEGER,"+
			"FOREIGN KEY(`userId`) REFERENCES `users.id`,FOREIGN KEY(`permissionId`) REFERENCES `permissions.id`)";
	// Variable to hold the database instance
	private SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private DbHelper dbHelper;

	public UserPermissionsDBAdapter(Context context) {
		this.context = context;
		this.dbHelper = new DbHelper(context);
	}

	public UserPermissionsDBAdapter open() throws SQLException {
		this.db = dbHelper.getWritableDatabase();
		return this;
	}



		public long insertEntry( int permissionsId, long userId) {
			UserPermissions userPermissions = new UserPermissions(Util.idHealth(this.db, USERPERMISSIONS_TABLE_NAME, USERPERMISSIONS_COLUMN_ID),permissionsId,userId );
			sendToBroker(MessageType.ADD_USER_PERMISSION, userPermissions, this.context);

			try {
				long insertResult = insertEntry(userPermissions);
				return insertResult;
			} catch (SQLException ex) {
				Log.e("UserPermissions insertEntry", "inserting Entry at " + USERPERMISSIONS_TABLE_NAME + ": " + ex.getMessage());
				return 0;
			}
		}
	public long insertEntry(UserPermissions userPermissions){
		ContentValues val = new ContentValues();
		val.put(USERPERMISSIONS_COLUMN_ID,userPermissions.getId());
		//Assign values for each row.
		val.put(USERPERMISSIONS_COLUMN_USERID, userPermissions.getUserId());
		val.put(USERPERMISSIONS_COLUMN_PERMISSIONSID, userPermissions.getPermissionId());

		try {
			return db.insert(USERPERMISSIONS_TABLE_NAME, null, val);
		} catch (SQLException ex) {
			Log.e("UserPermion DB insert", "inserting Entry at " + USERPERMISSIONS_TABLE_NAME + ": " + ex.getMessage());
			return 0;
		}
	}
	public void close() {
		db.close();
	}

	public SQLiteDatabase getDatabaseInstance() {
		return db;
	}
}
