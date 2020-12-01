package com.ecity.mypermissioncheck;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @ProjectName: MyPermissionCheck
 * @Package: com.ecity.mypermissioncheck
 * @ClassName:
 * @Description:
 * @Author: Administrator
 * @CreateDate: 2020/7/21
 * @Version: 1.0
 */
public class MyActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState==null){
            MainFragment mainFragment = new MainFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment,mainFragment)
                    .commit();
        }
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()){
            case R.id.list:
                fragment = new MyListFragment();
                break;
            case R.id.grid:
                fragment = new MyGridFragment();
                break;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment)
                .addToBackStack(null)
                .commit();
    }
}
