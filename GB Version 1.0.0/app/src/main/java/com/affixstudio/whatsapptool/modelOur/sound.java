package com.affixstudio.whatsapptool.modelOur;

import android.content.Context;
import android.media.MediaPlayer;

public class sound {

    public static void playSound(Context context,int fileId)
    {
        MediaPlayer player=MediaPlayer.create(context, fileId);
        player.start();
    }
}
