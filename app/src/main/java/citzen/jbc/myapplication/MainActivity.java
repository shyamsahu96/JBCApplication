package citzen.jbc.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int RC_SIGN = 123;
    static int RC_CANCELLED_USER = 111;

    FirebaseAuth mFirebaseAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    String mName, mEmail;
    TextView userName;
    TextView userEmail;
    CircleImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                    startActivityForResult(intent, RC_SIGN);
                }
            }
        };
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragReplace, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);

        user = mFirebaseAuth.getCurrentUser();
        View header=navigationView.getHeaderView(0);
        userName=(TextView)header.findViewById(R.id.navName);
        userEmail=(TextView)header.findViewById(R.id.navEmail);
        userImage=(CircleImageView)header.findViewById(R.id.profImageView);
        userName.setText(user.getDisplayName());
        userEmail.setText(user.getEmail());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN) {
            switch (resultCode) {
                case RESULT_OK:
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    mName = user.getDisplayName();
                    mEmail = user.getEmail();
                    break;
                case RESULT_CANCELED:
                    finish();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.log_out) {
            mFirebaseAuth.signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragReplace, new HomeFragment()).commit();

        } else if (id == R.id.nav_questions) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragReplace, new QuestionsFragment()).commit();

        } else if (id == R.id.nav_notices) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragReplace, new NoticeFragment()).commit();

        } else if (id == R.id.nav_feedback) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragReplace, new FeedBackFragment()).commit();

        } else if (id == R.id.nav_report) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragReplace, new ReportFragment()).commit();

        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthStateListener != null)
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }


}
