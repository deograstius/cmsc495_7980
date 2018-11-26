package capstone.cmsc495.ekganalyzer;
/**
 * @since 11-18-2018
 * @author: Deo Kalule & Jon Simmons
 * @purpose: To log into the EKG Analyzer app
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.GetUserQuery;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

public class LoginActivity extends AppCompatActivity {

    AWSAppSyncClient appSyncClient;
    EditText passwordText;
    final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        passwordText = findViewById(R.id.passwordTextField);

        appSyncClient = AWSAppSyncClient.builder()
                .context(getApplicationContext())
                .awsConfiguration(new AWSConfiguration(getApplicationContext()))
                .build();

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

        Intent intent = new Intent(LoginActivity.this, HistoryActivity.class);
        startActivity(intent);

//        appSyncClient.query(GetUserQuery.builder().id(passwordText.getText().toString()).build())
//                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
//                .enqueue(new GraphQLCall.Callback<GetUserQuery.Data>() {
//                    @Override
//                    public void onResponse(@Nonnull Response<GetUserQuery.Data> response) {
//                        if(response.data() != null){
//                            Log.v(TAG, "Not NIl");
//                            Log.v(TAG, response.data().getUser().password());
////
//                        } else {
//                            Log.v(TAG, "response.data() is Null");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(@Nonnull ApolloException e) {
//                        Toast.makeText(LoginActivity.this, e.getCause().toString(), Toast.LENGTH_SHORT).show();
//                    }
//                });


    }// End goToHistoryActivity() class
}// End LoginActivity class
