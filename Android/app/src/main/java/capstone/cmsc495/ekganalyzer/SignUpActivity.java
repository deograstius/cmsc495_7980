package capstone.cmsc495.ekganalyzer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateUserMutation;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import type.CreateUserInput;

public class SignUpActivity extends AppCompatActivity {

    AWSAppSyncClient appSyncClient;
    EditText emailEditText;
    EditText passwordEditText;
    EditText sensorIDEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Context context = getApplicationContext();
        AWSConfiguration awsConfiguration = new AWSConfiguration(context);


        appSyncClient = AWSAppSyncClient.builder()
                .context(context)
                .awsConfiguration(awsConfiguration)
                .build();

        emailEditText = findViewById(R.id.emailTextField);
        passwordEditText = findViewById(R.id.passwordTextField);
        sensorIDEditText = findViewById(R.id.sensorIDTextField);
    }// End onCreate Method

    private boolean textFieldsAreEmpty() {
        return emailEditText.getText().toString().equals("") ||
                passwordEditText.getText().toString().equals("") ||
                sensorIDEditText.getText().toString().equals("");
    }// End textFieldsAreEmpty() Method



    private GraphQLCall.Callback<CreateUserMutation.Data> mutationCallBack = new GraphQLCall.Callback<CreateUserMutation.Data>() {
        @Override
        public void onResponse(@Nonnull Response<CreateUserMutation.Data> response) {
            Log.v("SignUpActivity", response.toString());
            finish();
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.v("SignUpActivity", e.getCause().toString());
            e.printStackTrace();
        }
    };// End property

    public void signUp(View view) {

        if (!textFieldsAreEmpty()) {
            CreateUserInput createUserInput = CreateUserInput.builder()
                    .email(emailEditText.getText().toString())
                    .password(passwordEditText.getText().toString())
                    .sensorID(sensorIDEditText.getText().toString())
                    .build();

            appSyncClient.mutate(
                    CreateUserMutation.builder().input(createUserInput).build()
            ).enqueue(mutationCallBack);
        } else {
            Toast.makeText(this, "Please fill out all 3 fields", Toast.LENGTH_SHORT).show();
        }
    }
}// End SignUpActivity class
