package com.orientationchangedemo.caij.weiyo;

import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.HtmlUtil;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by Caij on 2016/7/12.
 */
public class UtilTest {

    @Test
    public void testPraseTime() {
        String time = DateUtil.formatCreatetime(System.currentTimeMillis());
        System.out.print(time);
    }

    @Test
    public void getHtmlTitle() {
        try {
            String title = HtmlUtil.getHtmlTitleByUrl("http://www.baidu.com");
            System.out.print(title);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
