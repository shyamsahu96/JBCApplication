package citzen.jbc.myapplication.student;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import citzen.jbc.myapplication.R;
import citzen.jbc.myapplication.firebase.Feed;

/**
 * Created by shyam on 18-Jul-17.
 */

public class FeedBackDialog extends DialogFragment {

    static String LOG_TAG = "FEEDBACK DIALOG";
    View view;
    @BindView(R.id.metfeedtext)
    MaterialEditText feedText;
    FragmentActivity mActivity;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mFeedReference;
    FirebaseAuth mFirebaseAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feedback, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mFirebaseDatabase == null)
            mFirebaseDatabase = FirebaseDatabase.getInstance();
        if (mFirebaseAuth == null)
            mFirebaseAuth = FirebaseAuth.getInstance();
        if (mFeedReference == null)
            mFeedReference = mFirebaseDatabase.getReference(getString(R.string.feedKey));

    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseDatabase = null;
        mFirebaseAuth = null;
        mFeedReference = null;
    }

    @OnClick(R.id.btnFeedOk)
    void takeFeed() {
        if (!feedText.isCharactersCountValid())
            return;
        String feed = feedText.getText().toString().trim();
        Feed myFeed = new Feed(mFirebaseAuth.getCurrentUser().getDisplayName(), feed);
        mFeedReference.push().setValue(myFeed).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mActivity, "Thank you for your feedback", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    @OnClick(R.id.btnFeedCancel)
    void cancelFeed() {
        dismiss();
    }
}
