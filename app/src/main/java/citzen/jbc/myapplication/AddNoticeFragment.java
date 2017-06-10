package citzen.jbc.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shyam on 02-Jun-17.
 */

public class AddNoticeFragment extends DialogFragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_notice, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.tvAddNotice)
    void addNotice() {
        Toast.makeText(getActivity(), "Added Notice", Toast.LENGTH_SHORT).show();
    }
}
