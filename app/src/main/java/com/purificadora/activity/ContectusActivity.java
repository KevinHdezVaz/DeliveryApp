package com.purificadora.activity;

import static com.purificadora.utils.SessionManager.contactUs;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.purificadora.R;
import com.purificadora.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContectusActivity extends BaseActivity {
    @BindView(R.id.txt_contac)
    TextView txtContac;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contectus);
        ButterKnife.bind(this);
        sessionManager = new SessionManager(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtContac.setText(Html.fromHtml(sessionManager.getStringData(contactUs), Html.FROM_HTML_MODE_COMPACT));
        } else {
            txtContac.setText(Html.fromHtml(sessionManager.getStringData(contactUs)));
        }
    }
}
