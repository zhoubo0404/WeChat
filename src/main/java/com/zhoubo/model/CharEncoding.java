package com.zhoubo.model;

import java.io.UnsupportedEncodingException;

/**
 * 字符编码常量以及相关方法。
 * @author 鹿振
 * @see org.apache.commons.lang.CharEncoding
 */
public class CharEncoding
{
    public static final String ISO_8859_1 = "ISO-8859-1";
    
    public static final String US_ASCII = "US-ASCII";
    
    public static final String UTF_8 = "UTF-8";
    
    public static final String UTF_16 = "UTF-16";
    
    public static final String UTF_16BE = "UTF-16BE";
    
    public static final String UTF_16LE = "UTF-16LE";
    
    public static final String GBK = "GBK";
    
    /**
     * 判断给出的字符编码名称是否被系统支持
     * @param name 字符编码名称
     * @return true：被系统支持，false：字符编码名称为null或不被系统支持。
     */
    public static boolean isSupported(String name)
    {
        if (name == null)
        {
            return false;
        }
        try
        {
            new String(new byte[0], name);
        }
        catch (UnsupportedEncodingException e)
        {
            return false;
        }
        return true;
    }
}
