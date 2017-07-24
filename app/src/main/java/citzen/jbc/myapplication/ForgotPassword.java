package citzen.jbc.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shyam on 21-Jul-17.
 */

public class ForgotPassword extends DialogFragment {

    final public static String LOG_TAG = "FORGOTPASS";
    View view;
    @BindView(R.id.tvfopaEmail)
    MaterialEditText email;
    FirebaseAuth mFirebaseAuth;
    ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_fopa, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btnfopa)
    void fopa() {

        String smail = email.getText().toString().trim();
        if (Patterns.EMAIL_ADDRESS.matcher(smail).matches()) {
            dialog = ProgressDialog.show(getActivity(), null, "Sending", false, false);
            mFirebaseAuth.sendPasswordResetEmail(smail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialog.dismiss();
                    Toast.makeText(getActivity(), "Reset E-mail has been sent", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else
            email.setError("Invalid E-mail");
    }

}
