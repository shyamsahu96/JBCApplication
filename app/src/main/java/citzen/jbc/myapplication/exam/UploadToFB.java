package citzen.jbc.myapplication.exam;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import citzen.jbc.myapplication.R;

public class UploadToFB extends AppCompatActivity {

    @BindView(R.id.tvQuestions)
    TextView view;
    Vector<String> vquestions, vopa, vopb, vopc, vopd, vanswer;
    RequestQueue queue;
    ProgressDialog dialog;
    String url = "https://pillared-fillers.000webhostapp.com/prepn/fetch_q.php";

    FirebaseDatabase mDatabase;
    DatabaseReference mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_to_fb);
        ButterKnife.bind(this);
        queue = Volley.newRequestQueue(this);
        initVector();
    }

    private void initVector() {
        vopa = new Vector<>();
        vopb = new Vector<>();
        vopc = new Vector<>();
        vopd = new Vector<>();
        vquestions = new Vector<>();
        vanswer = new Vector<>();
        mDatabase=FirebaseDatabase.getInstance();
        mReference=mDatabase.getReference();
    }

    @OnClick(R.id.btnUpload)
    void uploadQuestions() {
        fetchQuestions();
    }

    void fetchQuestions() {
        dialog = ProgressDialog.show(this, null, "Loading...", false, false);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.e("JSON String", response);
                view.setText(response);
                try {
                    JSONObject object=new JSONObject(response);
                    JSONArray qarray=object.getJSONArray("result");
                    if (qarray.length()<=0)
                        return;
                    initVector();
                    for (int i=0;i<qarray.length();i++){
                        JSONObject data=qarray.getJSONObject(i);
                        String question,opa,opc,opd,opb,answer;
                        question=data.getString("question");
                        opa=data.getString("opa");
                        opb=data.getString("opb");
                        opc=data.getString("opc");
                        opd=data.getString("opd");
                        answer=data.getString("answer");
                        Prepn prepn=new Prepn(question,opa,opb,opc,opd,answer);
                        mReference.child("prepn").push().setValue(prepn).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("FB Failure",e.getMessage());
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("Firebase","Success");
                            }
                        });
                        vquestions.add(question);
                        vopa.add(opa);
                        vopb.add(opb);
                        vopc.add(opc);
                        vopd.add(opd);
                        vanswer.add(answer);
                    }
                } catch (Exception e) {
                    Log.e("JSON Exception",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.e("Volley Error", error.toString());
            }
        });
        queue.add(request);
    }
}
