package citzen.jbc.myapplication.admin;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import citzen.jbc.myapplication.LogInActivity;
import citzen.jbc.myapplication.NoticeFragment;
import citzen.jbc.myapplication.ProfileActivity;
import citzen.jbc.myapplication.QuestionsFragment;
import citzen.jbc.myapplication.R;
import citzen.jbc.myapplication.exam.ActivityMain;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int RC_PERMISSIONS = 123;
    static int RC_CANCELLED_USER = 111;
    static boolean readPermission, writePermission;
    int RC_SIGN = 123;
    FirebaseAuth mFirebaseAuth;
    FirebaseUser mFirebaseUser;
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        userName = (TextView) header.findViewById(R.id.navName);
        userEmail = (TextView) header.findViewById(R.id.navEmail);
        userImage = (CircleImageView) header.findViewById(R.id.profImageView);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, userImage, getString(R.string.imageTrans));
                startActivity(new Intent(MainActivity.this, ProfileActivity.class), compat.toBundle());
            }
        });
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                    startActivityForResult(intent, RC_SIGN);
                } else {
                    if (!user.isEmailVerified()) {
                        mFirebaseAuth.signOut();
                        Toast.makeText(MainActivity.this, "E-mail verification required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mFirebaseAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            Log.e("Logged In Again", "true");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            mFirebaseAuth.signOut();
                        }
                    });
                    userName.setText(user.getDisplayName());
                    userEmail.setText(user.getEmail());
                }
            }
        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragReplace, new HomeFragment()).commit();
        navigationView.setCheckedItem(R.id.nav_home);


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
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            }).setNegativeButton("No", null).setCancelable(true).setTitle("Exit").setMessage("Are you sure you want to exit?");
            dialog.show();
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragReplace, new HomeFragment()).commit();

        } else if (id == R.id.nav_questions) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragReplace, new QuestionsFragment()).commit();

        } else if (id == R.id.nav_notices) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragReplace, new NoticeFragment()).commit();

        } else if (id == R.id.nav_feedback) {
            underdev();

        } else if (id == R.id.nav_report) {
            underdev();

        } else if (id == R.id.nav_logout) {
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            mFirebaseAuth.signOut();
        } else if (id == R.id.nav_take_test)
            startActivity(new Intent(this, ActivityMain.class));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthStateListener != null)
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        if (mFirebaseUser == null)
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null && mFirebaseUser.getPhotoUrl() != null)
            Picasso.with(this).load(mFirebaseUser.getPhotoUrl()).placeholder(R.drawable.ic_account_circle).into(userImage);
        else
            Picasso.with(this).load(R.drawable.ic_account_circle).into(userImage);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Allowed", "true");
                readPermission = true;
                writePermission = true;
            } else
                Log.e("Allowed", "false");
        }
    }

    private void underdev() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setCancelable(true).setPositiveButton("Ok", null);
        builder.setMessage("Under Development").show();
    }
}
