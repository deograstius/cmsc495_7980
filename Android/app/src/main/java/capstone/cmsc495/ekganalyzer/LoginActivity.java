package capstone.cmsc495.ekganalyzer;
/**
 * @since 11-18-2018
 * @author: Deo Kalule & Jon Simmons
 * @purpose: To log into the EKG Analyzer app
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }// End onCreate() Method

    /**
     * Navigates to the SignUpActivity class
     * @param view the button that was tapped
     */
    public void goToSignUpActivity(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }// End goToSignUpActivity() class

    /**
     * Navigates to the SignUpActivity class
     * @param view the button that was tapped
     */
    public void goToHistoryActivity(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }// End goToHistoryActivity() class
}// End LoginActivity class
