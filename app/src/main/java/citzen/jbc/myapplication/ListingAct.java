package citzen.jbc.myapplication;

import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class ListingAct extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing);
        imageView=(ImageView)findViewById(R.id.ivList);
    }

    public void share(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle sharedBundle= ActivityOptionsCompat.makeSceneTransitionAnimation(this,imageView,imageView.getTransitionName()).toBundle();
            Bundle bundle= ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle();
            startActivity(new Intent(this,DetailingAct.class),bundle);
        }
    }
}
