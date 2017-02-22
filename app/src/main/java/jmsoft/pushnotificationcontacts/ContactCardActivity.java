package jmsoft.pushnotificationcontacts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ContactCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_card);

        setUIData();
    }

    private void setUIData() {
        int contactId = getIntent().getIntExtra("contactId", 0);
        String name = getIntent().getStringExtra("contactName");
        String phone = getIntent().getStringExtra("contactPhone");

        TextView tvId = (TextView) findViewById(R.id.tvId);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvPhone = (TextView) findViewById(R.id.tvPhone);

        tvId.setText(tvId.getText().toString() + " " + contactId);
        tvName.setText(tvName.getText().toString() + " " + name);
        tvPhone.setText(tvPhone.getText().toString() + " " + phone);
    }
}
