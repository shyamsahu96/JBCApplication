package citzen.jbc.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import citzen.jbc.myapplication.firebase.Question;

/**
 * Created by shyam on 18-May-17.
 */

public class QuestionsFragment extends Fragment {

    View view;
    @BindView(R.id.rvQuestions)
    ListView mQuestions;
    FragmentActivity mActivity;

    FirebaseDatabase mDatabase;
    DatabaseReference mQuestionsReference;
    ChildEventListener mQuestionChildListener;

    MyAdapter questionAdapter;
    Vector<String> keys;
    Vector<String> replies;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_questions, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();
        mDatabase = FirebaseDatabase.getInstance();
        mQuestionsReference = mDatabase.getReference().child("questions");
        questionAdapter = new MyAdapter(mActivity, R.layout.item_questions_list);
        mQuestions.setAdapter(questionAdapter);
        keys = new Vector<String>();
        replies = new Vector<String>();
        mQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                Intent questionDetails = new Intent(mActivity, QuestionDetails.class);
                questionDetails.putExtra(getString(R.string.questionkey), keys.get(pos));
                questionDetails.putExtra(getString(R.string.questionsKey), questionAdapter.getItem(pos).question);
                questionDetails.putExtra("name", questionAdapter.getItem(pos).name);
                startActivity(questionDetails);
                Log.e("Intent Data", keys.get(pos) + "\n" + questionAdapter.getItem(pos).question + "\n" + questionAdapter.getItem(pos).name);
            }
        });
    }

    @OnClick(R.id.fabAdd)
    void addDialog() {
        new AddFragment().show(mActivity.getSupportFragmentManager(), "add");
    }

    class MyAdapter extends ArrayAdapter<Question> {

        public MyAdapter(Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MyViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_questions_list, parent, false);
                holder = new MyViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }

            Question itemQuestion = getItem(position);
            holder.tvquestion.setText(itemQuestion.getQuestion());
            holder.tvreplies.setText(replies.get(position));
            return convertView;
        }

        class MyViewHolder {
            @BindView(R.id.tvQuestions)
            TextView tvquestion;
            @BindView(R.id.tvReplies)
            TextView tvreplies;

            public MyViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mQuestionChildListener == null) {
            mQuestionChildListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Question item = dataSnapshot.getValue(Question.class);
                    questionAdapter.add(item);
                    keys.add(dataSnapshot.getKey());
                    if (dataSnapshot.hasChild(getString(R.string.repliesKey))) {
                        long noOfReplies = dataSnapshot.child(getString(R.string.repliesKey)).getChildrenCount();
                        replies.add(String.valueOf(noOfReplies) + " replies");
                    } else {
                        replies.add("no replies");
                    }
                    Log.e("Message", String.valueOf(dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    int changedKeyIndex = keys.indexOf(dataSnapshot.getKey());
                    Question unChangedQuestion = questionAdapter.getItem(changedKeyIndex);
                    questionAdapter.remove(unChangedQuestion);
                    questionAdapter.insert(dataSnapshot.getValue(Question.class), changedKeyIndex);
                    replies.remove(changedKeyIndex);

                    if (dataSnapshot.hasChild(getString(R.string.repliesKey))) {
                        long noOfReplies = dataSnapshot.child(getString(R.string.repliesKey)).getChildrenCount();
                        replies.add(changedKeyIndex, String.valueOf(noOfReplies) + " replies");
                    } else {
                        replies.add(changedKeyIndex, "no replies");
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String removedKey = dataSnapshot.getKey();
                    Question removedQuestion = questionAdapter.getItem(keys.indexOf(removedKey));
                    questionAdapter.remove(removedQuestion);
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Database Error", databaseError.getMessage());
                    Toast.makeText(mActivity, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };
        }
        mQuestionsReference.addChildEventListener(mQuestionChildListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mQuestionsReference != null) {
            mQuestionsReference.removeEventListener(mQuestionChildListener);
        }
        questionAdapter.clear();
        keys.clear();
        replies.clear();
    }
}
