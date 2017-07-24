package citzen.jbc.myapplication.exam;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import citzen.jbc.myapplication.R;

public class Result extends AppCompatActivity {

    @BindView(R.id.reList)
    ListView listView;
    @BindView(R.id.tvScore)
    TextView tvscore;
    String[] vans, uans;
    int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        uans = intent.getStringArrayExtra("uans");
        vans = intent.getStringArrayExtra("vans");
        tvscore.setText("You have scored " + String.valueOf(intent.getIntExtra("score", 0)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MyAdapter adapter = new MyAdapter(this, R.layout.twotext);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return true;
    }

    class MyAdapter extends ArrayAdapter<String> {

        MyAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {
            return uans.length;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(Result.this).inflate(R.layout.twotext, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            holder.qno.setText(String.valueOf(position + 1));
            holder.ans.setText(uans[position]);
            holder.key.setText(vans[position]);
            if (uans[position].equals(vans[position]))
                holder.back.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
            else
                holder.back.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            return convertView;
        }


        class ViewHolder {
            @BindView(R.id.tvqno)
            TextView qno;
            @BindView(R.id.tvans)
            TextView ans;
            @BindView(R.id.tvkey)
            TextView key;
            View back;

            public ViewHolder(View view) {
                back = view;
                ButterKnife.bind(this, view);
            }
        }
    }
}
