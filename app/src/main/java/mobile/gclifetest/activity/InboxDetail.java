package mobile.gclifetest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import mobile.gclifetest.pojoGson.InboxPojo;
import mobile.gclifetest.utils.MyApplication;
import mobile.gclifetest.db.DatabaseHandler;

public class InboxDetail extends BaseActivity {
    TextView unameTxt, subjectTxt, messageTxt, recvdDateTxt,tounameTxt;
    String dbNameRcv = "MAILRECV", dbNameSent = "MAILSENT";
    DatabaseHandler db;
    List<InboxPojo> inboxPojo;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox_mail_detail);
        unameTxt = (TextView)
                findViewById(R.id.unameTxt);
        subjectTxt = (TextView)
                findViewById(R.id.subjectTxt);
        recvdDateTxt = (TextView)
                findViewById(R.id.recvdDateTxt);
        messageTxt = (TextView)
                findViewById(R.id.discTxt);
        tounameTxt = (TextView)
                findViewById(R.id.tounameTxt);
        setUpActionBar("Mail detail");

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        db = new DatabaseHandler(this);
        int pos = bundle.getInt("position");
        String msgType = bundle.getString("mailType");
        if (msgType == "receive" || msgType.equals("receive")) {
            if (db.getEventNews(dbNameRcv) != "null") {
                Log.d("DB NOT NULL: " + dbNameRcv, db.getEventNews(dbNameRcv));
                inboxPojo = gson.fromJson(db.getEventNews(dbNameRcv), new TypeToken<List<InboxPojo>>() {
                }.getType());
                InboxPojo pojo = inboxPojo.get(pos);
                unameTxt.setText(pojo.getSender_name());
                subjectTxt.setText(pojo.getSubject());
                tounameTxt.setText(pojo.getReceiver_name());
                String createdAt = pojo.getCreated_at();
                MyApplication app = new MyApplication();
                createdAt = app.convertDateEmail(createdAt);

                String time = pojo.getCreated_at().substring(11, 19);
                time = app.convertTimeEmail(time);

                recvdDateTxt.setText((createdAt + "," + time));
                messageTxt.setText(pojo.getMessage());
            } else {
                Log.d("DB  NULL: " + dbNameRcv, db.getEventNews(dbNameRcv));
            }
        } else {

            Log.d("DB NOT NULL: " + dbNameSent, db.getEventNews(dbNameSent));
            inboxPojo = gson.fromJson(db.getEventNews(dbNameSent), new TypeToken<List<InboxPojo>>() {
            }.getType());
            InboxPojo pojo = inboxPojo.get(pos);
            unameTxt.setText(pojo.getSender_name());
            subjectTxt.setText(pojo.getSubject());
            tounameTxt.setText(pojo.getReceiver_name());
            String createdAt = pojo.getCreated_at();
            MyApplication app = new MyApplication();
            createdAt = app.convertDateEmail(createdAt);

            String time = pojo.getCreated_at().substring(11, 19);
            time = app.convertTimeEmail(time);

            recvdDateTxt.setText((createdAt + "," + time));
            messageTxt.setText(pojo.getMessage());
        }

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
