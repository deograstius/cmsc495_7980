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
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import java.util.Iterator;
import java.util.Random;

public class LiveEKGActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private static final int valuesPerSecond = 200;  // Number of EKG data values per second (hertz)

    // ***** Heart rate setup variables to adjust sensitivity *****
    // Standard deviation of recent 6 values that signifies a beat
    private static final double beatSensitivity = 10;

    // Counter (num values) delay before checking for beats again (to ignore duplicate counts from the same beat)
    private static final int delayBetweenBeatCheck = 25;

    private static final Random RANDOM = new Random();
    private final LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
    private int lastX = 0;
    double totalForMean;  // Total of all Y values for use in calculating mean
    TextView textBPM; // TextView of bpm output

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

        // Get BPM TextView
         textBPM = findViewById(R.id.textBPM);

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
    } // onCreate() -----------------------------------------------------------


    @Override
    public void onResume() {
        super.onResume();

        new Thread(new Runnable() {

            @Override
            public void run() {
                Iterator<DataPoint> dataPointIterator;
                int millisDelay = 1000/valuesPerSecond;  // Delay between each mock value created
                int heartRate;  // Heart rate in bpm
                double[] values = new double[10]; // Last 10 ekg values
                int[] beatIndexes = new int[5];
                int index;
                int lastBeatIndex = 0;  // the index (i) of the last beat
                int numBeats = 0;
                double standardDev;

                // Add 5000 new entries
                for (int i = 0; i < 5000; i++) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            addEntry();
                        }
                    });

                    // ***** Determine heart beat *****
                    if (i > 4) {
                        // Retrieve last 6 data points
                        synchronized (series) {
                            // 4 data points + a value on each end added via getValues()
                            dataPointIterator = series.getValues((double) (i - 4), (double) (i));

                            index = 0;
                            while (dataPointIterator.hasNext()) {
                                values[index] = dataPointIterator.next().getY();
                                index++;
                            }
                        }

                        // Check standard deviation of last 6 values
                        // and make sure this potential beat
                        standardDev = standDeviation(values, (totalForMean/(i + 1)));

                        if ((standardDev > beatSensitivity)
                                    && (i > (lastBeatIndex + delayBetweenBeatCheck))) {
                            // This is a beat
                            numBeats++;

                            if (numBeats > 5) {
                                // Shift array 1 beat to hold the time of the last 5 beats
                                System.arraycopy(beatIndexes, 1, beatIndexes, 0, 4);
                                beatIndexes[4] = i;

                                // 6 beats recorded, so calculate heart rate
                                heartRate = 300/((beatIndexes[4] - beatIndexes[0]) * millisDelay/1000);

                                // final version of heartRate so it can be used in the UI thread
                                final int hRate = heartRate;

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // Output heart rate
                                        textBPM.setText("" + hRate);
                                    }
                                });

                            } else {
                                beatIndexes[numBeats - 1] = i;
                            }

                            lastBeatIndex = i;
                        }
                    }

                    // sleep to slow down the add of entries. 5 millis delay = 200hz
                    try {
                        Thread.sleep(millisDelay);
                    } catch (InterruptedException e) {
                        // manage error ...
                    }
                }
            }
        }).start();
    } // onResume() --------------------------------------------------


    /**
     * Calculates standard deviation
     * @param numArray Array of doubles
     * @return standard deviation of array values
     */
    public static double standDeviation(double dblValues[], double mean) {
        double sum = 0.0;
        double standardDeviation = 0.0;

        int length = dblValues.length;

        /*
        for(double num : dblValues) {
            sum += num;
        }

        double mean = sum/length;
        */

        for(double num: dblValues) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation/length);
    }


    // add random data to graph
    private void addEntry() {
        DataPoint dataPoint;  // New value

        // Create and display max 1000 values on the viewport and scroll to end
        // Display a fake beat every (170/200) seconds
        int mod = lastX % 170;

        if ((mod > 3 ) && (mod < 7)) {
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

        totalForMean += dataPoint.getY();

        // Add value to viewport and scroll to end
        synchronized (series) {
            series.appendData(dataPoint, true, 1000);
        }
    } // addEntry() ---------------------------------------------------


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
