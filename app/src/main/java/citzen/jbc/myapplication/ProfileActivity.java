package citzen.jbc.myapplication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import citzen.jbc.myapplication.firebase.FirebaseUserProfile;
import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {

    final String PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";
    @BindView(R.id.met_prof_name)
    MaterialEditText metName;
    @BindView(R.id.met_prof_clg)
    MaterialEditText metClg;
    @BindView(R.id.met_prof_pass)
    MaterialEditText metPass;
    @BindView(R.id.met_prof_email)
    MaterialEditText metEmail;
    @BindView(R.id.cbPass)
    CheckBox cbPass;
    @BindView(R.id.activity_prof_img)
    ImageView profImg;
    ProgressDialog dialog, uploadDialog;
    int RC_PHOTO = 111, RC_PERMISSION = 101;
    boolean readPermission;
    Uri imageUri;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPhotoReference;
    private FirebaseUser mFirebaseUser;
    private FirebaseUserProfile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.saveProfile) {

            //Upload user details to the firebase.
            if (!metName.isCharactersCountValid() || !metClg.isCharactersCountValid()) {
                Snackbar.make(findViewById(R.id.activity_profile), "Invalid Credentials", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            final String name, college, pass;
            name = metName.getText().toString();
            college = metClg.getText().toString();
            pass = metPass.getText().toString();

            if (!metPass.isCharactersCountValid() || !pass.matches(PASS_PATTERN)) {
                Snackbar.make(findViewById(R.id.activity_profile), "Invalid Password Format", Snackbar.LENGTH_SHORT).show();
                return false;
            }
            profile.setName(name);
            profile.setCollege(college);
            profile.setPassword(pass);
            final ProgressDialog dialog = ProgressDialog.show(this, null, "Updating Your Profile...", false, false);
            mReference.setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialog.dismiss();
                    UserProfileChangeRequest.Builder changeRequest = new UserProfileChangeRequest.Builder();
                    mFirebaseUser.updateProfile(changeRequest.setDisplayName(name).build()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(findViewById(R.id.activity_profile), "Profile Updated", Snackbar.LENGTH_SHORT).show();
                            mFirebaseUser.updatePassword(pass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Snackbar.make(findViewById(R.id.activity_profile), "Password Updated", Snackbar.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Password Failure", e.getMessage());
                                    Snackbar.make(findViewById(R.id.activity_profile), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                                    logoutandexit();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Update Failure", e.getMessage());
                            Snackbar.make(findViewById(R.id.activity_profile), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                            logoutandexit();
                        }
                    });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Firebase Error", e.getMessage());
                    Snackbar.make(findViewById(R.id.activity_profile), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    logoutandexit();
                }
            });

        } else if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            logoutandexit();
            return;
        }
        readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        cbPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    metPass.setEnabled(true);
                else
                    metPass.setEnabled(false);
            }
        });
        cbPass.setChecked(true);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFirebaseStorage == null || mPhotoReference == null) {
            mFirebaseStorage = FirebaseStorage.getInstance();
            mPhotoReference = mFirebaseStorage.getReference("profiles");
        }
        if (mFirebaseUser == null)
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mReference = mFirebaseDatabase.getReference(getString(R.string.userKey)).child(mFirebaseAuth.getCurrentUser().getUid());
        dialog = ProgressDialog.show(this, null, "Loading...", false, false);
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();
                profile = dataSnapshot.getValue(FirebaseUserProfile.class);
                Log.e("ValueEvent", dataSnapshot.getValue(FirebaseUserProfile.class).getCollege());
                metPass.setText(profile.getPassword());
                metClg.setText(profile.getCollege());
                metEmail.setText(profile.getEmail());
                metName.setText(profile.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dialog.dismiss();
                Log.e("ValueEventError", databaseError.getMessage());
                Toast.makeText(ProfileActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        if (mFirebaseUser.getPhotoUrl() != null)
            Picasso.with(ProfileActivity.this).load(mFirebaseUser.getPhotoUrl()).placeholder(R.drawable.placeholder).error(android.R.drawable.stat_notify_error).into(profImg);
        if (!readPermission)
            requestMyPermission();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @OnClick(R.id.fabPhoto)
    void selectPhoto() {
        if (!readPermission) {
            requestMyPermission();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK).setType("image/*");
        Intent chooserIntent = Intent.createChooser(intent, "Select a Photo:");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent});
        startActivityForResult(chooserIntent, RC_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO) {
            if (resultCode == RESULT_OK) {
                imageUri = data.getData();
                mPhotoReference = mPhotoReference.child(imageUri.getLastPathSegment());
                Compressor compressor = new Compressor(this).setQuality(50).setCompressFormat(Bitmap.CompressFormat.JPEG);
                Cursor cursor;
                cursor = getContentResolver().query(imageUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                cursor.moveToFirst();
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                cursor.close();
                Log.e("Cursor Path", path);

                File file = new File(path);
                if (file.exists()) {
                    Log.e("Exists", "true");
                }
                File uploadFile = null;
                try {
                    uploadFile = compressor.compressToFile(file);
                } catch (Exception e) {
                    Log.e("Compressor Exception", e.getMessage());
                    return;
                }
                uploadDialog = ProgressDialog.show(this, null, "Uploading", false, false);
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build();
                mFirebaseUser.updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadDialog.dismiss();
                        Picasso.with(ProfileActivity.this).load(mFirebaseUser.getPhotoUrl()).placeholder(R.drawable.placeholder).error(android.R.drawable.stat_notify_error).into(profImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                readPermission = true;
        }
    }

    void requestMyPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION);
    }

    private void logoutandexit() {
        mFirebaseAuth.signOut();
        Toast.makeText(this, "Please Login Again Your Session has been Expired", Toast.LENGTH_SHORT).show();
        finish();
    }
}