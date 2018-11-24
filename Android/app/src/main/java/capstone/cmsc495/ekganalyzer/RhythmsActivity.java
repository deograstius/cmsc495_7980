package capstone.cmsc495.ekganalyzer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class RhythmsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Runnable timer;
    private Handler handler = new Handler();
    private LineGraphSeries<DataPoint> series;
    private DataPoint[] dataPoints;
    private int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rhythms);
        setTitle(R.string.sinus_rhythm);

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
        final RhythmsActivity thisActivity = this;

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.historyList:
                        Intent intent = new Intent(thisActivity, HistoryActivity.class);
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

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Create graph
        GraphView graph = findViewById(R.id.ekgGraphView);

        // Customize the series
        series = new LineGraphSeries<>();
        dataPoints = generateData();
        series.setColor(Color.WHITE);

        // Customize the stroke
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(8);
        series.setCustomPaint(paint);

        graph.addSeries(series);

        // Customize the viewPort
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(9);
        graph.getViewport().setMaxX(30);
        graph.getViewport().setMinX(0);

    }// End onCreate() Method

    /**
     * Gets the dataPoints for the series
     * @return an array of data points for the graph view
     */
    private DataPoint[] generateData() {

        int count = 1700;
        int x = 0;
        DataPoint[] dataPoints = new DataPoint[count];
        for(int i = 0; i < count;){
            dataPoints[i++] = new DataPoint(x, 4);
            dataPoints[i++] = new DataPoint(x + 2,4);
            dataPoints[i++] = new DataPoint(x + 2.9,6);
            dataPoints[i++] = new DataPoint(x + 3.1, 6);
            dataPoints[i++] = new DataPoint(x + 4,4);
            dataPoints[i++] = new DataPoint(x + 6,4);
            dataPoints[i++] = new DataPoint(x + 6.5,3);
            dataPoints[i++] = new DataPoint(x + 6.7,3);
            dataPoints[i++] = new DataPoint(x + 7.5,8);
            dataPoints[i++] = new DataPoint(x + 7.7,8);
            dataPoints[i++] = new DataPoint(x + 8.5,3);
            dataPoints[i++] = new DataPoint(x + 8.7,3);
            dataPoints[i++] = new DataPoint(x + 9.2,4);
            dataPoints[i++] = new DataPoint(x + 11,4);
            dataPoints[i++] = new DataPoint(x + 12,7);
            dataPoints[i++] = new DataPoint(x + 12.2,7);
            dataPoints[i++] = new DataPoint(x + 13,4);
            x += 13;
        }

        return dataPoints;
    }// End generateData() Method

    @Override
    protected void onResume() {
        super.onResume();

        timer = new Runnable() {
            @Override
            public void run() {
                series.appendData(dataPoints[currentIndex++], true, 1700);
                handler.postDelayed(this, 100);
            }
        };

        new Thread(timer).start();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(timer);
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rhythms_menu, menu);
        return true;
    }// End onCreateOptionsMenu() Method

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
            case R.id.live_ekg:
                intent = new Intent(this, LiveEKGActivity.class);
                startActivity(intent);
                return true;
            default:
                setTitle(item.getTitle());
                return super.onOptionsItemSelected(item);

        }// End switch statement
    }// End onOptionsItemsSelected() Method
}// End RhythmsActivity class
