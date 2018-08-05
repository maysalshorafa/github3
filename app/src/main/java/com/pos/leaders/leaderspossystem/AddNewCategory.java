package com.pos.leaders.leaderspossystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pos.leaders.leaderspossystem.DataBaseAdapter.CategoryDBAdapter;
import com.pos.leaders.leaderspossystem.Models.Category;
import com.pos.leaders.leaderspossystem.Tools.SESSION;
import com.pos.leaders.leaderspossystem.Tools.TitleBar;

public class AddNewCategory extends AppCompatActivity {
    Button addNewCategory, cancel;
    EditText categoryName;
    CategoryDBAdapter categoryDBAdapter;
    long categoryId;
    Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_new_category);
        TitleBar.setTitleBar(this);
        addNewCategory = (Button) findViewById(R.id.addNewDepartment);
        cancel = (Button) findViewById(R.id.cancelAddDepartment);
        categoryName = (EditText) findViewById(R.id.etdepartmentName);
        categoryDBAdapter = new CategoryDBAdapter(this);
        categoryDBAdapter.open();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            //update case
            categoryId = (long) bundle.get("categoryID");
            category = categoryDBAdapter.getDepartmentByID(categoryId);
            addNewCategory.setText(getString(R.string.update_category));
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addNewCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add new category region
                if (category == null) {
                    //if empty input
                    if (categoryName.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.please_insert_category_name), Toast.LENGTH_LONG).show();
                    } else {
                        //test if unique name or not
                        boolean exist = false;
                        exist = categoryDBAdapter.availableDepartmentName(categoryName.getText().toString());
                        //not unique name
                        if (!exist || categoryName.getText().toString().equalsIgnoreCase("all")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.please_insert_another_category_name), Toast.LENGTH_LONG).show();
                        }
                        //unique
                        else {
                            long check = categoryDBAdapter.insertEntry(categoryName.getText().toString(), SESSION._EMPLOYEE.getEmployeeId());
                            if (check > 0) {
                                Toast.makeText(getApplicationContext(), getString(R.string.success_to_add_new_category), Toast.LENGTH_LONG).show();
                                Log.i("success", "added category");
                            } else {
                                Toast.makeText(getApplicationContext(), getString(R.string.failed_to_add_new_category), Toast.LENGTH_LONG).show();
                                Log.e("error", " adding category");
                            }
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(AddNewCategory.this, CategoryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                            startActivity(intent);
                        }

                    }
                }
                //end

                //upDate region

                else {
                    //if empty input
                    if (categoryName.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.please_insert_category_name), Toast.LENGTH_LONG).show();
                    } else {
                        //test if unique name or not
                        boolean exist = false;
                        exist = categoryDBAdapter.availableDepartmentName(categoryName.getText().toString());
                        if (!exist) {
                            //not unique
                            Toast.makeText(getApplicationContext(), getString(R.string.please_insert_another_category_name), Toast.LENGTH_LONG).show();

                        } else {
                            //unique name
                            category.setName(categoryName.getText().toString());
                            categoryDBAdapter.updateEntry(category);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(AddNewCategory.this, CategoryActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    }
                }
                //end
            }


        });

    }
}
