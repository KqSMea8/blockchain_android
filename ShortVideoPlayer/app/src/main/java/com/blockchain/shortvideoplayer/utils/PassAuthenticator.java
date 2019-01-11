package com.blockchain.shortvideoplayer.utils;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by Administrator on 2017/3/30 0030.
 */
public class PassAuthenticator extends Authenticator
{
    public PasswordAuthentication getPasswordAuthentication()
    {
        /**
         * 这个地方需要添加上自己的邮箱的账号和密码
         */
        String username = "liumin@muyouguanxi.com";
        String pwd = "Liu1980min";
        return new PasswordAuthentication(username, pwd);
    }
}
