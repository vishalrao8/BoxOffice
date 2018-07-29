package com.example.visha.boxoffice.activity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.visha.boxoffice.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;

public class ErrorScreen extends AppCompatActivity implements View.OnClickListener{

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.error_layout)
    ConstraintLayout parentLayout;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private NetworkRequest networkRequest;

    private void setUpNetworkCallback() {

        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkRequest = new NetworkRequest.Builder().build();
        networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {

                moveToPreviousActivity();

            }

        };
    }

    /* A method returning current network state */
    private boolean isConnected(){

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());

    }

    private void moveToPreviousActivity(){

        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.error_layout);
        ButterKnife.bind(this);

        Snackbar.make(parentLayout, getString(R.string.snack_bar_error_text), LENGTH_INDEFINITE)
                .setAction(R.string.snack_bar_action_text, this)
                .show();

        // Setting up network callback on creating activity
        setUpNetworkCallback();
    }

    @Override
    public void onClick(View view) {

        if (!isConnected())
            Snackbar.make(parentLayout, getString(R.string.snack_bar_error_text), LENGTH_INDEFINITE)
                    .setAction(R.string.snack_bar_action_text, ErrorScreen.this)
                    .show();
        else
            moveToPreviousActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectivityManager.unregisterNetworkCallback(networkCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);
    }
}
