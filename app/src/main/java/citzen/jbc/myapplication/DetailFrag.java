package citzen.jbc.myapplication;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by shyam on 31-May-17.
 */

public class DetailFrag extends Fragment {
    @BindView(R.id.ivDetail)
    ImageView imageView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_detail,container,false);
        ButterKnife.bind(this,view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.detail_enter));
            getActivity().getWindow().setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(R.transition.detail_enter));
        }
        return view;
    }
}
