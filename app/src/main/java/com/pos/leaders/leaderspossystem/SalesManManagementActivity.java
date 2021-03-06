package com.pos.leaders.leaderspossystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.pos.leaders.leaderspossystem.DataBaseAdapter.EmployeeDBAdapter;
import com.pos.leaders.leaderspossystem.Models.Employee;
import com.pos.leaders.leaderspossystem.Tools.EmployeeGridViewAdapter;
import com.pos.leaders.leaderspossystem.Tools.TitleBar;

import java.util.List;

public class SalesManManagementActivity extends AppCompatActivity {

    List<Employee> users;
    EmployeeDBAdapter userDBAdapter;
    GridView gvSalesMan;
    Button btAddSalesMan,btCancel;
    private static final int CHANGE_PASSWORD_DIALOG = 656;
    Employee user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sales_man_management);

        TitleBar.setTitleBar(this);

        gvSalesMan = (GridView) findViewById(R.id.reportManagement_GVSalesMan);
        btAddSalesMan = (Button) findViewById(R.id.salesManManagement_BTNewSalesMan);
        btCancel = (Button) findViewById(R.id.salesManManagement_BTCancel);

        //region Buttons

        btAddSalesMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SalesManManagementActivity.this, AddEmployeeActivity.class);
                startActivity(intent);
            }
        });

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onBackPressed();
            }
        });


        //endregion


        userDBAdapter= new EmployeeDBAdapter(this);
        userDBAdapter.open();
        users = userDBAdapter.getAllSalesMAn();
        user = null;

        final EmployeeGridViewAdapter adapter = new EmployeeGridViewAdapter(this, users);
        gvSalesMan.setAdapter(adapter);
        gvSalesMan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Intent intent;
                intent = new Intent(SalesManManagementActivity.this, SalesAssistantDetailesSalesMangmentActivity.class);
                intent.putExtra("employeeId", users.get(position).getEmployeeId());
                startActivity(intent);
             /**   final String[] items = {
                     //   getString(R.string.view)
                        //,
                        getString(R.string.view_sales),


                };

                AlertDialog.Builder builder = new AlertDialog.Builder(SalesManManagementActivity.this);
                builder.setTitle(getBaseContext().getString(R.string.make_your_selection));
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Intent intent;
                        switch (item) {

                            case 0:
                                EmployeeManagementActivity.User_Management_View=7;
                                intent = new Intent(SalesManManagementActivity.this, AddEmployeeActivity.class);
                                intent.putExtra("employeeId", users.get(position).getEmployeeId());
                                intent.putExtra(EmployeeManagementActivity.LEAD_POS_RESULT_INTENT_CODE_ADD_USER_ACTIVITY_BUTTON_ADD_USER_NAME, getString(R.string.view));
                                startActivity(intent);
                                break;
                            case 1:
                                intent = new Intent(SalesManManagementActivity.this, SalesAssistantDetailesSalesMangmentActivity.class);
                                intent.putExtra("employeeId", users.get(position).getEmployeeId());
                                startActivity(intent);
                                break;
                        }
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();**/
            }
        });
    }

}
