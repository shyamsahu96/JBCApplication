package citzen.jbc.myapplication.exam;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import citzen.jbc.myapplication.R;
import citzen.jbc.myapplication.firebase.ExamResult;
import citzen.jbc.myapplication.service.LocalService;

/**
 * Created by shyam on 20-Dec-16.
 */
public class ActivityMain extends AppCompatActivity {

    public static String url = "https://pillared-fillers.000webhostapp.com/prepn/fetch_q.php";
    public final String jresult = "result";
    public final String jquestion = "question";
    public final String jopa = "opa";
    public final String jopb = "opb";
    public final String jopc = "opc";
    public final String jopd = "opd";
    public final String jans = "answer";
    public final String jid = "id";
    int position = 0, score = 0;
    Vector<String> vquestion, vopa, vopb, vopc, vopd, vans, uans, id;
    TextView tvquestion, tvscore;
    RadioButton ropa, ropb, ropc, ropd;
    RadioGroup rg;
    Button b;
    NetworkInfo info;
    TextView timer;
    //    CountDownTimer cdt;
    RequestQueue requestQueue;
    ConnectivityManager manager;
    long milisFuture = 300000, interval = 1000, millisUntill;

    FirebaseDatabase mDatabase;
    DatabaseReference mExamReference;
    FirebaseAuth mFirebaseAuth;
    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateGUI(intent); // or whatever method used to update your GUI fields
        }
    };

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (vquestion == null)
            return;
        outState.putStringArray(jquestion, vquestion.toArray(new String[vquestion.size()]));
        outState.putStringArray(jopa, vopa.toArray(new String[vopa.size()]));
        outState.putStringArray(jopb, vopb.toArray(new String[vopb.size()]));
        outState.putStringArray(jopc, vopc.toArray(new String[vopc.size()]));
        outState.putStringArray(jopd, vopd.toArray(new String[vopd.size()]));
        outState.putStringArray(jans, vans.toArray(new String[vans.size()]));
        outState.putStringArray(jid, id.toArray(new String[id.size()]));
        outState.putStringArray("uans", uans.toArray(new String[uans.size()]));
        outState.putInt("rg", rg.getVisibility());
        outState.putInt("timer", timer.getVisibility());
        outState.putInt("position", position);
        outState.putLong("milis", millisUntill);
//        if (cdt!=null)
    }

    @Override
    protected void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.design);
        ropa = (RadioButton) findViewById(R.id.opa);
        ropb = (RadioButton) findViewById(R.id.opb);
        ropc = (RadioButton) findViewById(R.id.opc);
        ropd = (RadioButton) findViewById(R.id.opd);
        rg = (RadioGroup) findViewById(R.id.fchoice);
        tvquestion = (TextView) findViewById(R.id.fquestion);
        timer = (TextView) findViewById(R.id.btimer);
        b = (Button) findViewById(R.id.button);
        requestQueue = Volley.newRequestQueue(this);
        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mExamReference = mDatabase.getReference(getString(R.string.userKey)).child(mFirebaseAuth.getCurrentUser().getUid()).child(getString(R.string.examKey));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsub("Are you Sure you want to Submit?");
            }
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.opa:
                        uans.set(position, "A");
                        break;
                    case R.id.opb:
                        uans.set(position, "B");
                        break;
                    case R.id.opc:
                        uans.set(position, "C");
                        break;
                    case R.id.opd:
                        uans.set(position, "D");
                        break;
                    default:
                        uans.set(position, "N");
                        break;
                }
            }
        });

        if (state == null) {
            rg.setVisibility(View.INVISIBLE);
//            setUpTimer(milisFuture,interval);
            timer.setVisibility(View.INVISIBLE);
            fetch_question();

        } else {
            try {
                vquestion = new Vector<String>(Arrays.asList(state.getStringArray(jquestion)));
                vopa = new Vector<String>(Arrays.asList(state.getStringArray(jopa)));
                vopb = new Vector<String>(Arrays.asList(state.getStringArray(jopb)));
                vopc = new Vector<String>(Arrays.asList(state.getStringArray(jopc)));
                vopd = new Vector<String>(Arrays.asList(state.getStringArray(jopd)));
                vans = new Vector<String>(Arrays.asList(state.getStringArray(jans)));
                uans = new Vector<String>(Arrays.asList(state.getStringArray("uans")));
                id = new Vector<String>(Arrays.asList(state.getStringArray(jid)));
                if (state.getInt("rg") == View.VISIBLE)
                    rg.setVisibility(View.VISIBLE);
                else
                    rg.setVisibility(View.INVISIBLE);
                if (state.getInt("timer") == View.VISIBLE) {
                    millisUntill = state.getLong("milis");
                    setUpTimer(millisUntill, interval);
                    timer.setVisibility(View.VISIBLE);
//                    if (cdt!=null)
//                    cdt.start();
                } else
                    timer.setVisibility(View.INVISIBLE);
                position = state.getInt("position");
                setQuestion();
            } catch (Exception e) {
                Toast.makeText(this, "Null" + e.getMessage(), Toast.LENGTH_SHORT).show();
                rg.setVisibility(View.INVISIBLE);
                timer.setVisibility(View.INVISIBLE);
            }
        }

    }

    private void setUpTimer(long milisFuture, long interval) {
       /* cdt = new CountDownTimer(milisFuture, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisUntill=millisUntilFinished;
                long rem = millisUntilFinished % 60000;
                String s = String.valueOf(rem);
                if (rem < 1000)
                    s = "0";
                else if (rem < 10000)
                    s = s.substring(0, 1);
                else
                    s = s.substring(0, 2);
                timer.setText(millisUntilFinished / 60000 + " : " + s);//min:sec
            }

            @Override
            public void onFinish() {
                timer.setText("0 : 00");
                bsub(b);
            }

        };*/


        stopService(new Intent(this, LocalService.class));
        startService(new Intent(this, LocalService.class));
    }

    private void setUpVector() {
        vquestion = new Vector<String>();
        vopa = new Vector<String>();
        vopb = new Vector<String>();
        vopc = new Vector<String>();
        vopd = new Vector<String>();
        vans = new Vector<String>();
        uans = new Vector<String>();
        id = new Vector<String>();
        for (int i = 0; i < 10; i++)
            uans.add("N");
    }

    private void fetch_question() {
        info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) ;
        else {
            Toast.makeText(this, "Network Offline", Toast.LENGTH_SHORT).show();
            return;
        }
        final ProgressDialog pd = ProgressDialog.show(this, "Fetching Questions...", "Please Wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pd.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray(jresult);
                    if (results.length() > 0)
                        setUpVector();
                    else {
                        Toast.makeText(ActivityMain.this, "No questions available...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject row = results.getJSONObject(i);
                        vquestion.add(row.getString(jquestion));
                        vopa.add(row.getString(jopa));
                        vopb.add(row.getString(jopb));
                        vopc.add(row.getString(jopc));
                        vopd.add(row.getString(jopd));
                        vans.add(row.getString(jans));
                        id.add(row.getString(jid));
                    }
                    rg.setVisibility(View.VISIBLE);
                    timer.setVisibility(View.VISIBLE);
                    setQuestion();
//                    if (cdt!=null)
//                        cdt.cancel();
                    setUpTimer(milisFuture, interval);
//                    cdt.start();

                } catch (JSONException e) {
                    Log.e("JSON Exception", e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    public void bsub(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        submit();
                    }
                }).setNegativeButton("No", null);
        if (vquestion == null) {
            Toast.makeText(this, "Hit the Refresh button at the top to load questions", Toast.LENGTH_SHORT).show();
        } else
            builder.show();

    }

    private void submit() {
        info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) ;
        else {
            Toast.makeText(this, "Network Offline", Toast.LENGTH_SHORT).show();
            return;
        }
        if (vquestion == null) {
            Toast.makeText(this, "Hit the Refresh button at the top to load questions", Toast.LENGTH_SHORT).show();
            return;
        }
//        if (cdt!=null)
//            cdt.cancel();
        stopService(new Intent(this, LocalService.class));
        timer.setText("0 : 00");
        int correct = 0;
        for (int i = 0; i < vans.size(); i++) {
            if (vans.get(i).equals(uans.get(i)))
                correct++;
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d-M-y");
            String sdate = dateFormat.format(new Date());
            Log.e("Date", sdate);
            ExamResult result = new ExamResult(sdate, String.valueOf(correct));
            Log.i("Score", String.valueOf(correct));
            mExamReference.push().setValue(result);
        } catch (Exception e) {
            Log.e("Date Exception", e.getMessage());
        }
        Intent intent = new Intent(ActivityMain.this, Result.class);
        intent.putExtra("vans", vans.toArray(new String[vans.size()]));
        intent.putExtra("uans", uans.toArray(new String[uans.size()]));
        intent.putExtra("score", correct);
        startActivity(intent);
        finish();

    }

    public void next(View v) {
        position++;
        setQuestion();
    }

    public void previous(View v) {
        position--;
        setQuestion();
    }

    public void setQuestion() {
        if (vquestion == null || vquestion.size() <= 0) {
            Toast.makeText(this, "Hit the Refresh button at the top to load questions", Toast.LENGTH_SHORT).show();
            return;
        }
        if (position < 0)
            position = vquestion.size() - 1;
        else if (position >= vquestion.size())
            position = 0;
        tvquestion.setText(vquestion.get(position));
        ropa.setText(vopa.get(position));
        ropb.setText(vopb.get(position));
        ropc.setText(vopc.get(position));
        ropd.setText(vopd.get(position));
        switch (uans.get(position)) {

            case "A":
                ropa.setChecked(true);
                break;
            case "B":
                ropb.setChecked(true);
                break;
            case "C":
                ropc.setChecked(true);
                break;
            case "D":
                ropd.setChecked(true);
                break;
            default:
                rg.clearCheck();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.exam_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                fetch_question();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void updateGUI(Intent intent) {
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            Log.i("TAG", "Countdown seconds remaining: " + millisUntilFinished / 1000);
            millisUntill = millisUntilFinished;
            long rem = millisUntilFinished % 60000;
            String s = String.valueOf(rem);
            if (rem < 1000)
                s = "0";
            else if (rem < 10000)
                s = s.substring(0, 1);
            else
                s = s.substring(0, 2);
            timer.setText(millisUntilFinished / 60000 + " : " + s);//min:sec

            if ((millisUntilFinished / 1000) == 1) {
                timer.setText("0 : 00");
                bsub("Time's up click YES to submit");

            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(LocalService.COUNTDOWN_BR));
        Log.i("TAG", "Registered broacast receiver");
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
        Log.i("TAG", "Unregistered broacast receiver");
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, LocalService.class));
        Log.i("TAG", "Stopped service");
        super.onDestroy();
    }
}
