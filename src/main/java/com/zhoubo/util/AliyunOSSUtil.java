package com.zhoubo.util;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * Created by zhoubo on 2017/7/14.
 */
public class AliyunOSSUtil {
    public static OSSClient getOSSClient() {
        // endpoint以杭州为例，其它region请按实际情况填写
//        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
        String endpoint = "oss-cn-shenzhen.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建
        String accessKeyId = "";
        String accessKeySecret = "";
        // 创建OSSClient实例
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);

//        client.createBucket("zhoubodebucketname1");
//        System.out.println(client.);
        // 使用访问OSS
        // 关闭client
        return client;
    }

    public static String putObject(InputStream inputStream) {
        OSSClient ossClient = getOSSClient();
        String bucketName = "zhoubodebucket";
        String key = "testImg.jpg";
//        PutObjectResult putObjectResult = ossClient.putObject("zhoubodebucket","testImg5.jpg",inputStream);
//        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        // 设置URL过期时间为1小时
        Date expiration = new Date(new Date().getTime() + 3600 * 1000);
// 生成URL
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
        System.out.println(url.toString());
//        return putObjectResult.getRequestId();
        return url.toString();
    }

    public static String getImage() {
        String bucketName = "zhoubodebucket";
        String key = "testImg.jpg";
        OSSClient ossClient = getOSSClient();
        // 图片处理样式
//        String style = "image/resize,m_fixed,w_100,h_100/rotate,90";
        // 过期时间10分钟
        Date expiration = new Date(new Date().getTime() + 1000 * 60 * 10);
        GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, key, HttpMethod.GET);
        req.setExpiration(expiration);
//        req.setProcess(style);
        URL signedUrl = ossClient.generatePresignedUrl(req);
        System.out.println(signedUrl);
        return signedUrl.toString();
    }

    public static void main(String[] args) throws FileNotFoundException {
//        File file = new File("G:\\git\\image\\e4dde71190ef76c647fd64809c16fdfaaf51676a.jpg");
//        InputStream inputStream = new FileInputStream(file);
//        System.out.println(putObject(inputStream));
        getImage();

    }


}
