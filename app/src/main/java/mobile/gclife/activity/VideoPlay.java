package mobile.gclife.activity;

import mobile.gclife.PojoGson.MyApplication;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

@SuppressWarnings("deprecation")
public class VideoPlay extends ActionBarActivity {
	android.support.v7.app.ActionBar actionBar;
	ProgressDialog pDialog;
	VideoView videoview;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_play_activity);
		videoview = (VideoView) findViewById(R.id.VideoView);
		// setting Action Bar
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Video");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));
		Intent ii = getIntent();
		String videoUrl = ii.getStringExtra("Video");

		System.out.println(videoUrl + " *************  VIDEO URL ");

		// Execute StreamVideo AsyncTask

		// Create a progressbar
		pDialog = new ProgressDialog(VideoPlay.this);
		// Set progressbar title
//		pDialog.setTitle("Please wait");
		// Set progressbar message
		pDialog.setMessage("Buffering...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		// Show progressbar
		pDialog.show();

		try {
			// Start the MediaController
			MediaController mediacontroller = new MediaController(
					VideoPlay.this);
			mediacontroller.setAnchorView(videoview);
			// Get the URL from String VideoURL
			Uri video = Uri.parse(videoUrl);
			videoview.setMediaController(mediacontroller);
			videoview.setVideoURI(video);

		} catch (Exception e) {
			e.printStackTrace();
		}

		videoview.requestFocus();
		videoview.setOnPreparedListener(new OnPreparedListener() {
			// Close the progress bar and play the video
			public void onPrepared(MediaPlayer mp) {
				pDialog.dismiss();
				videoview.start();
			}
		});

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

}
