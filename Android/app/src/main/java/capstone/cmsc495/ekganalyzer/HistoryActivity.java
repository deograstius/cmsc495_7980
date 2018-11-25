package capstone.cmsc495.ekganalyzer;
/**
 * @Purpose this activity holds the EKG history
 * @author: Deo & Jon Simmons
 * @since 11-18-2018
 * @version 1
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recyclerView = findViewById(R.id.historyList);

        List<String> input = new ArrayList<>();
        for (int i = 10; i < 29; i++) {
            input.add("11/24/2018 10:" + i + ":" + (i*2));
        }// define an adapter
        mAdapter = new HistoryAdapter(input);
        recyclerView.setAdapter(mAdapter);

        // Set up a custom tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get the action bar with an item
        ActionBar actionBar = getSupportActionBar();
        System.out.println(actionBar);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }// End if


        // Set the drawer layout A.K.A the navigation bar
        drawerLayout = findViewById(R.id.drawer_layout);
        final HistoryActivity thisActivity = this;

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Intent intent;

                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.rhythms:
                        intent = new Intent(thisActivity, RhythmsActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.live_ekg:
                        intent = new Intent(thisActivity, LiveEKGActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.log_out:
                        // ##### TO DO: Log user out




                        intent = new Intent(thisActivity, LoginActivity.class);
                        startActivity(intent);
                        return true;
                    default:
                        return true;

                }// End switch statement
            }// End onNavigationItemSelected
        });// End closure
    }// End onCreate() Method





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                return true;
            case R.id.live_ekg:
                intent = new Intent(this, LiveEKGActivity.class);
                startActivity(intent);
                return true;
            case R.id.rhythms:
                intent = new Intent(this, RhythmsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }// End switch statement

    }// End onOptionsItemSelected() Method

}// End History class
