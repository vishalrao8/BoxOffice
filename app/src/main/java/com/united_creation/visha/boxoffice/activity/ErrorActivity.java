package com.united_creation.visha.boxoffice.activity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.united_creation.visha.boxoffice.R;
import com.united_creation.visha.boxoffice.utils.UserPreferences;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;

public class ErrorActivity extends AppCompatActivity implements View.OnClickListener{

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

    private void moveToPreviousActivity(){

        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();

    }

    private void moveToMovieList () {

        UserPreferences.updateUserPreferences(getApplicationContext(), UserPreferences.FAVOURITE);
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
        ButterKnife.bind(this);

        Snackbar.make(parentLayout, getString(R.string.snack_bar_error_text), LENGTH_INDEFINITE)
                .setAction(R.string.show_favourites, this)
                .show();

        // Setting up network callback on creating activity
        setUpNetworkCallback();
    }

    @Override
    public void onClick(View view) {

        moveToMovieList();

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
