package com.orientationchangedemo.caij.weiyo;

import com.caij.emore.utils.DateUtil;
import com.caij.emore.utils.HtmlUtil;
import com.caij.emore.utils.MD5Util;

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

    @Test
    public void testMd5() {
        String title = MD5Util.string2MD5("caij");
        System.out.print(title);
//        467934697c9ac5440028fc281e7428aa
//        467934697c9ac5440028fc281e7428aa
//        467934697c9ac5440028fc281e7428aa
    }

    @Test
    public void convertMD5() {
        String title = MD5Util.convertMD5("caij");
//        title = MD5Util.convertMD5(title);
        System.out.print(title);
//        467934697c9ac5440028fc281e7428aa
//        467934697c9ac5440028fc281e7428aa
//        467934697c9ac5440028fc281e7428aa
    }
}
