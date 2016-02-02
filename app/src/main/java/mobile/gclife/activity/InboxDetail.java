package mobile.gclife.activity;

import mobile.gclife.PojoGson.MyApplication;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class InboxDetail extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	TextView unameTxt,subjectTxt,messageTxt,recvdDateTxt;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inbox_mail_detail);
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Mail detail");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
	/*	String sender_name = bundle.getString("sender_name");
		String subject = bundle.getString("subject");
		String message = bundle.getString("message");
		String created_at = bundle.getString("created_at");
		*/
		unameTxt = (TextView) 
				findViewById(R.id.unameTxt);
		subjectTxt = (TextView) 
				findViewById(R.id.subjectTxt);
		recvdDateTxt = (TextView) 
				findViewById(R.id.recvdDateTxt);
		messageTxt = (TextView) 
				findViewById(R.id.discTxt);
		
		unameTxt.setText(bundle.getString("sender_name"));
		subjectTxt.setText( bundle.getString("subject"));
		recvdDateTxt.setText( bundle.getString("created_at"));
		messageTxt.setText(bundle.getString("message"));
		
		
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			onBackPressed();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
