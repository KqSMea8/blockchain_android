/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.demo.crop.media;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Objects;

public class MediaDir {

    public String thumbnailUrl;

    public String dirName;

    public String videoDirPath;

    public int id;

    public int type;

    public int fileCount;

    public int resId;

    @Override
    public boolean equals(Object o) {
        if(o instanceof MediaDir){
            MediaDir md = (MediaDir) o;
            return dirName.equals(md.dirName);
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // your code available only above api 19
            return Objects.hash(videoDirPath, dirName);
        } else {
            // compatibility code
            return videoDirPath.hashCode() + dirName.hashCode();
        }
    }
}
