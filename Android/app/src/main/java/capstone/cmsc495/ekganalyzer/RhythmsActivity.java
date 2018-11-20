package capstone.cmsc495.ekganalyzer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class RhythmsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rhythms);

        // Set up a custom tool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }// End onCreate() Method


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
        // Menu selection options will go here
        return super.onOptionsItemSelected(item);
    }// End onOptionsItemsSelected() Method
}// End RhythmsActivity class
