package mobile.gclifetest.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import mobile.gclifetest.utils.MyApplication;

@SuppressWarnings("deprecation")
public class VideoPlay extends AppCompatActivity implements
		YouTubePlayer.OnInitializedListener {
	ProgressDialog pDialog;
	YouTubePlayerView videoview;
	android.support.v7.app.ActionBar actionBar;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_play_activity);
		videoview = (YouTubePlayerView) findViewById(R.id.youtube_view);

		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(android.R.color.transparent);
		actionBar.setTitle("Video");
		actionBar.setBackgroundDrawable(new ColorDrawable(Color
				.parseColor(MyApplication.actiobarColor)));
		Intent ii = getIntent();
		String videoUrl = ii.getStringExtra("Video");

		System.out.println(videoUrl + " *************  VIDEO URL ");

		// Initializing video player with developer key
		//  videoview.initialize("AIzaSyBj4Z-0tjXgGthPks6OxyBvRaqRlMHVh2k", VideoPlay.this);
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl)));


		// Execute StreamVideo AsyncTask

		// Create a progressbar
		/*pDialog = new ProgressDialog(VideoPlay.this);
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
		});*/

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

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

	}

	@Override
	public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

	}
}
