package com.affixstudio.whatsapptool.modelOur;

import android.net.Uri;

public class whatsappStatusModel {
    private String name;
    private Uri uri;
    private String fileName;
    private String path;

    public whatsappStatusModel(String name, Uri uri, String fileName, String path) {
        this.name = name;
        this.uri = uri;
        this.fileName = fileName;
        this.path = path;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }
}
