package com.example.youtube;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.app.placefinderapp.R;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;
 
public class YoutubePlay extends YouTubeFailureRecoveryActivity {

	private String id;

	@SuppressLint("NewApi") @Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youtube);

		Bundle b = getIntent().getExtras();
		id = (String) b.get("id");
		Log.e("id", id);
		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView
				.initialize(
						getString(R.string.youtube_api_key),
						this);
		

	}

	@Override
	public void onInitializationSuccess(Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		// TODO Auto-generated method stub
		if (!wasRestored) {
			player.loadVideo(id);
		}
		 
	}

	@Override
	protected Provider getYouTubePlayerProvider() {
		// TODO Auto-generated method stub
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}
}