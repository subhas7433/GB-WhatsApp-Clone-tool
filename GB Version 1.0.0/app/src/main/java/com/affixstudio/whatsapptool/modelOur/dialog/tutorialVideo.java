package com.affixstudio.whatsapptool.modelOur.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.affixstudio.whatsapptool.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

public class tutorialVideo {

    public  void showVideo(int i, Context c,String name)
    {

        String[] videoIds=c.getResources().getStringArray(R.array.videoIds);
        String videoId=videoIds[i];

        Dialog video=new Dialog(c);


        video.setCancelable(false);

        video.getWindow().setBackgroundDrawable(c.getDrawable(R.drawable.custom_dialog));

        View v=LayoutInflater.from(c).inflate(R.layout.tutorial_video,null);

        ImageButton close=v.findViewById(R.id.close);
        TextView tutorialName=v.findViewById(R.id.tutorialName);

        tutorialName.setText(name);

        Log.i("showVideo",i+"showVideo id "+videoIds[i]);
        YouTubePlayerView youTubePlayerView = v.findViewById(R.id.youtube_player_view);

        youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                youTubePlayer.loadVideo(videoId,0);

            }
        });


        close.setOnClickListener(view -> {
            youTubePlayerView.release();
            video.dismiss();
        });

        video.setContentView(v);
        video.show();
    }

}
