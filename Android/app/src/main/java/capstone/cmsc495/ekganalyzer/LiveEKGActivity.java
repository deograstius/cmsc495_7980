package capstone.cmsc495.ekganalyzer;

/**
 * @Purpose this activity holds the Live EKG
 * @author: Deo & Jon Simmons
 * @since 11-21-2018
 * @version 1
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.Random;

public class LiveEKGActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    //private List<Double> ekgData;  // EKG data values
    private static final int valuesPerSecond = 200;  // Number of EKG data values per second (hertz)
    //private double heartRate;  // Heart rate in bpm

    private static final Random RANDOM = new Random();
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;

    /**
     * onCreate() = when activity is first initialized
     * @param savedInstanceState Saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate the view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_ekg);

        // Get ekg chart view
        GraphView ekgChart = findViewById(R.id.ekgGraphView);


        // TEST data
        //series = new LineGraphSeries<>(generateData());
        series = new LineGraphSeries<>();
        ekgChart.addSeries(series);

        // Customize viewport
        Viewport viewport = ekgChart.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-1);
        viewport.setMaxY(10);
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(50);

        // Initiate data/device connection




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
        final LiveEKGActivity thisActivity = this;

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
                    case R.id.historyList:
                        intent = new Intent(thisActivity, HistoryActivity.class);
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
    } // onCreate()



    @Override
    public void onResume() {
        super.onResume();

        new Thread(new Runnable() {

            @Override
            public void run() {
                // we add 100 new entries
                for (int i = 0; i < 100; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // sleep to slow down the add of entries
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    }

    // add random data to graph
    private void addEntry() {
        // here, we choose to display max 10 points on the viewport and we scroll to end
        series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), true, 50);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                return true;
            case R.id.historyList:
                intent = new Intent(this, HistoryActivity.class);
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

} // LiveEKGActivity class
