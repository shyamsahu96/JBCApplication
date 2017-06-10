package citzen.jbc.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shyam on 18-May-17.
 */

public class SignInFragment extends Fragment {

    View view;

    @BindView(R.id.etemail)
    MaterialEditText etemail;
    @BindView(R.id.etpass)
    MaterialEditText etpass;
    FirebaseAuth mFirebaseAuth;
    FragmentActivity mActivity;

    ProgressDialog mSignDialog;

    String email, pass;
    final String LOG_TAG = "SIGNIN FRAGMENT";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_signin, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mActivity = getActivity();
    }

    @OnClick(R.id.btnSignIn)
    void signIn() {
        email = etemail.getText().toString().trim();
        pass = etpass.getText().toString().trim();

        if (!etemail.isCharactersCountValid() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etemail.setError("Invalid E-mail");
            return;
        }
        if (!etpass.isCharactersCountValid() || !pass.matches(SignUpFragment.PASSWORD_PATTERN)) {
            etpass.setError("Invalid Password Format");
            return;
        }

        mSignDialog = ProgressDialog.show(mActivity, null, "Signing In...", false, false);

        mFirebaseAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                mSignDialog.dismiss();
                mActivity.setResult(Activity.RESULT_OK);
                mActivity.finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mSignDialog.dismiss();
                mActivity.setResult(Activity.RESULT_CANCELED);
                Toast.makeText(mActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, e.getMessage());
            }
        });

    }
}
