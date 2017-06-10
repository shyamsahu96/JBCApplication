package citzen.jbc.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by shyam on 18-May-17.
 */

public class NoticeFragment extends Fragment {

    View view;
    FragmentActivity mActivity;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_notices,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity=getActivity();
    }

    @OnClick(R.id.fabNotice)
    void sendNotice(){
        new AddNoticeFragment().show(mActivity.getSupportFragmentManager(),"Notice");
    }
}
