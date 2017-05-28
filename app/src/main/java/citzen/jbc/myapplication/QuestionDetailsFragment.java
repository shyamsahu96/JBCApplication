package citzen.jbc.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shyam on 27-May-17.
 */

public class QuestionDetailsFragment extends Fragment {

    View view;
    FragmentActivity mActivity;
    String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    @BindView(R.id.addViews)
    RecyclerView cotainer;
    @BindView(R.id.metAnswer)
    MaterialEditText metAnswer;
    @BindView(R.id.tvName)
    TextView name;
    @BindView(R.id.tvQuestion)
    TextView question;

    ArrayList<Replies> repliesArrayList;
    RecyclerView.Adapter mAdapter;
    FirebaseDatabase mDatabase;
    FirebaseUser user;
    DatabaseReference mRepliesReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_question_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = mActivity.getIntent();
        name.setText(intent.getStringExtra("name"));
        question.setText(intent.getStringExtra(getString(R.string.questionsKey)));
        mRepliesReference = mDatabase.getReference().child("questions").child(intent.getStringExtra(getString(R.string.questionkey))).child(getString(R.string.repliesKey));
        repliesArrayList = new ArrayList<>();
        mAdapter = new FirebaseRecyclerAdapter<Replies, MyHolder>(Replies.class, R.layout.item_replies_list, MyHolder.class, mRepliesReference) {
            @Override
            protected void populateViewHolder(MyHolder viewHolder, Replies model, int position) {
                viewHolder.name.setText(model.getName());
                viewHolder.msg.setText(model.getMessage());
            }

            @Override
            public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v=LayoutInflater.from(mActivity).inflate(R.layout.item_replies_list,parent,false);
                return new MyHolder(v);
            }
        };
        cotainer.setLayoutManager(new LinearLayoutManager(mActivity));
        cotainer.setAdapter(mAdapter);
        cotainer.addItemDecoration(new DividerItemDecoration(mActivity,DividerItemDecoration.VERTICAL));


    }

    class MyHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profImg)
        CircleImageView profImg;
        @BindView(R.id.tvDetailsName)
        TextView name;
        @BindView(R.id.tvMsg)
        TextView msg;

        public MyHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @OnClick(R.id.fabReply)
    void replyToQuestion() {
        String answer = metAnswer.getText().toString().trim();
        if (!metAnswer.isCharactersCountValid() || TextUtils.isEmpty(answer)) {
            metAnswer.setError("Invalid Format");
            return;
        }
        Replies userReply = new Replies("blank", user.getDisplayName(), answer);
        metAnswer.setText("");
        View focusedView=mActivity.getCurrentFocus();
        if (focusedView!=null){
            InputMethodManager methodManager=(InputMethodManager)mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            methodManager.hideSoftInputFromWindow(focusedView.getWindowToken(),0);
        }
        mRepliesReference.push().setValue(userReply).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                cotainer.scrollToPosition(cotainer.getChildCount()-1);
            }
        });

    }

}
