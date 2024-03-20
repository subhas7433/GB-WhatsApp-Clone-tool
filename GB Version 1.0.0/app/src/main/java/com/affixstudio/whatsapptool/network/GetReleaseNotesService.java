package com.affixstudio.whatsapptool.network;

import com.affixstudio.whatsapptool.model.GithubReleaseNotes;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetReleaseNotesService {
    @GET("/repos/adeekshith/watomatic/releases")
    Call<List<GithubReleaseNotes>> getReleaseNotes();
}
