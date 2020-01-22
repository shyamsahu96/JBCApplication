package citzen.jbc.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import citzen.jbc.myapplication.design.ThreeTwoPager;
import citzen.jbc.myapplication.firebase.Recents;
import id.zelory.compressor.Compressor;

import static citzen.jbc.myapplication.MainActivity.readPermission;
import static citzen.jbc.myapplication.MainActivity.writePermission;

/**
 * Created by shyam on 18-May-17.
 */

public class HomeFragment extends Fragment {
    public static String map_uri = "geo:20.2969922,85.8203299?z=16";
    View view;
    @BindView(R.id.vp_home)
    ThreeTwoPager mRecentPhotos;
    MyPagerAdapter mRecentAdapter;
    FragmentActivity mActivity;
    Vector<String> keys;
    int RC_CHOOSE = 100, RC_PERMISSIONS = 123;

    ChildEventListener mRecentEventListener;
    FirebaseDatabase mDatabase;
    DatabaseReference mRecentsReference;
    FirebaseStorage mStorage;
    StorageReference mRecentsPhotosReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mDatabase = FirebaseDatabase.getInstance();
        mRecentsReference = mDatabase.getReference(getString(R.string.recentevent));
        mStorage = FirebaseStorage.getInstance();
        mRecentsPhotosReference = mStorage.getReference(getString(R.string.recentevent));
        readPermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        writePermission = ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

    }

    @Override
    public void onStart() {
        super.onStart();
        keys = new Vector<>();
        mRecentAdapter = new MyPagerAdapter(mActivity.getSupportFragmentManager());


        if (mRecentEventListener == null) {
            mRecentEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Recents recents = dataSnapshot.getValue(Recents.class);
                    recents.setDataKey(dataSnapshot.getKey());
                    mRecentAdapter.addFragment(recents);
                    keys.add(dataSnapshot.getKey());
                    mRecentPhotos.setAdapter(mRecentAdapter);
                    Log.e("Onchild Added", recents.getPhotoLink());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    String changedKey = dataSnapshot.getKey();
                    int changedIndex = keys.indexOf(changedKey);
                    RecentEventsFragment fragment = (RecentEventsFragment) mRecentAdapter.getItem(changedIndex);
                    Recents recents = dataSnapshot.getValue(Recents.class);
                    fragment.setMessage(recents.getMessage());
                    fragment.setPhotoLink(recents.getPhotoLink());
                    mRecentPhotos.setAdapter(mRecentAdapter);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String removedKey = dataSnapshot.getKey();
                    int removedIndex = keys.indexOf(removedKey);
                    mRecentAdapter.removeFragment(removedIndex);
                    keys.remove(removedKey);
                    mRecentPhotos.setAdapter(mRecentAdapter);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Firebase Error", databaseError.getMessage());
                }
            };
        }
        mRecentsReference.addChildEventListener(mRecentEventListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        mRecentsReference.removeEventListener(mRecentEventListener);
        mRecentAdapter = null;
        keys.clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_CHOOSE) {
            if (resultCode == FragmentActivity.RESULT_OK) {
                Uri imageUri = data.getData();
                Log.e("PhotoUri", imageUri.toString());
                StorageReference photoRef = mRecentsPhotosReference.child(imageUri.getLastPathSegment());
                Compressor compressor = new Compressor(mActivity).setQuality(50).setCompressFormat(Bitmap.CompressFormat.JPEG);
                Cursor cursor;
                cursor = mActivity.getContentResolver().query(imageUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
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

                photoRef.putFile(Uri.fromFile(uploadFile)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        long size = taskSnapshot.getTotalByteCount();
                        long transfer = taskSnapshot.getBytesTransferred();
                        int per = (int) (transfer / size) * 100;
                        Log.e("Upload Percentage", String.valueOf(per));
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri uploadedUri = taskSnapshot.getDownloadUrl();
                        Recents recents = new Recents("Hi Recents", uploadedUri.toString());
                        mRecentsReference.push().setValue(recents);
                        Log.e("Uploading Status", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Uploading Exception", e.getMessage());
                        Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            } else if (resultCode == mActivity.RESULT_CANCELED) {
                Toast.makeText(mActivity, "Please select a image", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*@OnClick(R.id.addRecent)
    void addRecent() {
        if (!readPermission || !writePermission) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RC_PERMISSIONS);
        } else {
            Intent pickerIntent = new Intent(Intent.ACTION_PICK);
            pickerIntent.setType("image/*");
            Intent chooseIntent = Intent.createChooser(pickerIntent, "Select Image");
            chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickerIntent});
            startActivityForResult(chooseIntent, RC_CHOOSE);
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSIONS && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Allowed", "true");
                readPermission = true;
                writePermission = true;
            } else
                Log.e("Allowed", "false");
        }

    }

    @OnClick(R.id.imgcitzen)
    public void openMap() {
        Uri mapUri = Uri.parse(map_uri);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
        if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivity(mapIntent);
        else
            Toast.makeText(getActivity(), "Map functionality is not supported in your mobile.", Toast.LENGTH_SHORT).show();
    }

    class MyPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<RecentEventsFragment> fragments = new ArrayList<>();
        ArrayList<String> message, photoLink;

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("Adapter Position", String.valueOf(position));
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            Log.e("Adapter Size", String.valueOf(fragments.size()));
            return fragments.size();
        }

        void addFragment(Recents recents) {
            RecentEventsFragment fragment = new RecentEventsFragment();
            Bundle args = new Bundle();
            args.putString("message", recents.getMessage());
            args.putString("photoLink", recents.getPhotoLink());
            args.putString("dataKey", recents.getDataKey());
            fragment.setArguments(args);
            fragments.add(fragment);
            Log.e("Fragment Added", "true");
        }

        void removeFragment(int position) {
            fragments.remove(position);
        }
    }
}
