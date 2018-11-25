package capstone.cmsc495.ekganalyzer;

/**
 * @Purpose this activity holds the Live EKG
 * @author: Deo & Jon Simmons
 * @version 1
 * @since 11-21-2018
 */

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Iterator;
import java.util.Random;

public class LiveEKGActivity extends AppCompatActivity {

    private static final int valuesPerSecond = 200;  // Number of EKG data values per second (hertz)
    // ***** Heart rate setup variables to adjust sensitivity *****
    // Standard deviation of recent 6 values that signifies a beat
    //private static final double beatSensitivity = 10;
    // Counter (num values) delay before checking for beats again (to ignore duplicate counts from the same beat)
    //private static final int delayBetweenBeatCheck = 25;
    private static final Random RANDOM = new Random();
    private final LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    //double totalForMean;  // Total of all Y values for use in calculating mean

    private int numBeats;  // Number of heartbeats
    private int timeOfLastBeat = 0;
    private int[] timeBetweenLast6Beats = {800,800,800,800,800,800};
    private int millisecsPassed;  // Milliseconds of time passed so far
    private int heartRate = 0;  // Heart rate in bpm
    TextView textHeartRate;  // TextView for HeartRate output
    TextView textConditions;  // TextView for Conditions output
    private DrawerLayout drawerLayout;
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

        // Get needed TextView and GraphView objects
        textHeartRate = findViewById(R.id.textBPM);
        textConditions = findViewById(R.id.textConditions);
        GraphView ekgChart = findViewById(R.id.ekgGraphView);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Update Condition Info
            textConditions.setText("Condition Findings Here");
        }

        // Add mock TEST data to chart
        ekgChart.addSeries(series);

        // Customize viewport
        Viewport viewport = ekgChart.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-75);
        viewport.setMaxY(205);
        viewport.setScrollable(true);
        viewport.setXAxisBoundsManual(true);
        viewport.setMinX(0);
        viewport.setMaxX(1000);

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

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Portrait view navigation bar
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
        }
    } // onCreate() -----------------------------------------------------------


    @Override
    public void onResume() {
        super.onResume();
        millisecsPassed = 1;

        new Thread(new Runnable() {

            @Override
            public void run() {
                int millisDelay = 1000 / valuesPerSecond;  // Delay between each mock value created

                // Add 5000 new entries
                for (int i = 0; i < 5000; i++) {
                    int totalTime = 0; // Total time between last 6 beats (so for 5 beats to complete)

                    // Total time between last 6 beats
                    for (int time : timeBetweenLast6Beats) {
                        totalTime += time;
                    }
                    heartRate = 360000 / totalTime;

                    // final version of heartRate so it can be used in the UI thread
                    final int hRate = heartRate;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            addEntry();
                            textHeartRate.setText("" + hRate);
                        }
                    });

                    // sleep to slow down the add of entries. 5 millis delay = 200hz
                    try {
                        Thread.sleep(millisDelay);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }

                    millisecsPassed = millisecsPassed + 5;
                }
            }
        }).start();
    } // onResume() --------------------------------------------------


    // add random data to graph
    private void addEntry() {
        DataPoint dataPoint;  // New value

        // Create and display max 1000 values on the viewport and scroll to end
        // Display a fake beat every (170/1000) seconds
        int mod = lastX % 170;

        if (mod == 169) {
            numBeats++;

            // Set millisecond counter of the last beat and place into array of last 6 beat times
            timeBetweenLast6Beats[numBeats % 6] = (lastX * 5) - timeOfLastBeat;
            timeOfLastBeat = lastX * 5;

            dataPoint = new DataPoint(lastX++, RANDOM.nextDouble() * 7);
        } else if ((mod > 3) && (mod < 7)) {
            dataPoint = new DataPoint(lastX++, RANDOM.nextDouble() * 200);
        } else if ((mod > 0) && (mod < 9)) {
            dataPoint = new DataPoint(lastX++, RANDOM.nextDouble() * 80);
        } else if ((mod > 9) && (mod < 12)) {
            dataPoint = new DataPoint(lastX++, RANDOM.nextDouble() * -70);
        } else if ((mod > 11) && (mod < 14)) {
            dataPoint = new DataPoint(lastX++, RANDOM.nextDouble() * -20);
        } else {
            dataPoint = new DataPoint(lastX++, RANDOM.nextDouble() * 7);
        }

        // Add value to viewport and scroll to end
        synchronized (series) {
            series.appendData(dataPoint, true, 1000);
        }
    } // addEntry() ---------------------------------------------------


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch (item.getItemId()) {
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
