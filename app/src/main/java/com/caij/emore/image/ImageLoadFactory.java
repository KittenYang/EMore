package com.caij.emore.image;

/**
 * Created by Ca1j on 2016/12/29.
 */

public class ImageLoadFactory {

    private static EMoreImageLoad sEMoreImageLoad;

    public static EMoreImageLoad getImageLoad() {
        if (sEMoreImageLoad == null) {
            synchronized (ImageLoadFactory.class) {
                if (sEMoreImageLoad == null) {
                    sEMoreImageLoad = new EMoreImageLoad(new GlideImageLoad());
                }
            }
        }
        return sEMoreImageLoad;
    }
}
