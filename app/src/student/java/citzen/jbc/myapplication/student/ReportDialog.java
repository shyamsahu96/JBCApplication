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
import citzen.jbc.myapplication.firebase.Report;

/**
 * Created by shyam on 18-Jul-17.
 */

public class ReportDialog extends DialogFragment {

    static String LOG_TAG = "REPORT DIALOG";
    View view;
    @BindView(R.id.metreporttext)
    MaterialEditText feedText;
    FragmentActivity mActivity;

    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mReportReference;
    FirebaseAuth mFirebaseAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_report, container, false);
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
        mReportReference = mFirebaseDatabase.getReference(getString(R.string.reportKey));

    }

    @Override
    public void onStop() {
        super.onStop();
        mReportReference = null;
        mFirebaseDatabase = null;
        mFirebaseAuth = null;
    }

    @OnClick(R.id.btnReportOk)
    void takeReport() {
        if (!feedText.isCharactersCountValid())
            return;
        String feed = feedText.getText().toString();
        Report report = new Report(mFirebaseAuth.getCurrentUser().getDisplayName(), feed);
        mReportReference.push().setValue(report).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mActivity, "Report Submitted Succesfully", Toast.LENGTH_SHORT).show();
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

    @OnClick(R.id.btnReportCancel)
    void cancelReport() {
        dismiss();
    }

}
