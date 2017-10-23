package com.zhoubo.util.microsoft;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by zhoubo on 2017/7/18.
 */
public class CognitiveServicesUtil {
    /**
     * 获得缩略图url
     **/
    public static final String GENERATE_THUMBNAIL = "https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/generateThumbnail";
    public static final String FACE_DETECT = "https://westcentralus.api.cognitive.microsoft.com/face/v1.0/detect";
    /**
     * 微软认知服务请求头参数
     **/
    private static final String IMAGE_OCP_APIM_SUBSCRIPTION_KEY = "c8d980c17d494213958318ed78c1b8d4";
    private static final String FACE_OCP_APIM_SUBSCRIPTION_KEY = "989b4a54952244d38543f5779212aaa0";

    /**
     * 获得缩略图方法
     **/
    public static InputStream generateThumbnail(byte[] bytes) {
        HttpClient httpClient = new DefaultHttpClient();
        InputStream inputStream = null;
        try {
            // NOTE: You must use the same location in your REST call as you used to obtain your subscription keys.
            //   For example, if you obtained your subscription keys from westus, replace "westcentralus" in the
            //   URL below with "westus".
            URIBuilder uriBuilder = new URIBuilder(GENERATE_THUMBNAIL);
            uriBuilder.setParameter("width", "100");
            uriBuilder.setParameter("height", "150");
            uriBuilder.setParameter("smartCropping", "true");
            URI uri = uriBuilder.build();
            HttpPost request = new HttpPost(uri);
//            request.setHeader("Content-Type", "application/json");
            // Request headers.
//            request.setHeader("Content-Type", "application/json");
//            request.setHeader("Content-Type", "application/json");
            // NOTE: Replace the "Ocp-Apim-Subscription-Key" value with a valid subscription key.
            request.setHeader("Ocp-Apim-Subscription-Key", IMAGE_OCP_APIM_SUBSCRIPTION_KEY);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.addBinaryBody("url", bytes);
            HttpEntity httpEntity1 = multipartEntityBuilder.build();
            request.setEntity(httpEntity1);
//            InputStreamEntity inputStreamEntity = new InputStreamEntity(imageInputStream);
//            request.setEntity(inputStreamEntity);
            HttpResponse response = httpClient.execute(request);
            System.out.println(response);

            // Display the thumbnail.
            HttpEntity httpEntity = response.getEntity();
            inputStream = httpEntity.getContent();
//            displayImage(httpEntity.getContent());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return inputStream;
    }

    public static String faceDetect(byte[] bytes) {
        HttpClient httpclient = new DefaultHttpClient();
        String jsonString = null;
        try {
            URIBuilder builder = new URIBuilder(FACE_DETECT);

            // Request parameters. All of them are optional.
            builder.setParameter("returnFaceId", "true");
            builder.setParameter("returnFaceLandmarks", "false");
            builder.setParameter("returnFaceAttributes", "age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise");

            // Prepare the URI for the REST API call.
            URI uri = builder.build();
            HttpPost request = new HttpPost(uri);

            // Request headers.
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", FACE_OCP_APIM_SUBSCRIPTION_KEY);
/*            StringEntity reqEntity = new StringEntity("{body}");
            request.setEntity(reqEntity);*/
            // Request body.
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            multipartEntityBuilder.addBinaryBody("url", new File("G:\\git\\image\\IMG_1141.JPG"));
            multipartEntityBuilder.setContentType(ContentType.APPLICATION_OCTET_STREAM);
            HttpEntity httpEntity1 = multipartEntityBuilder.build();
            request.setEntity(httpEntity1);

            // Execute the REST API call and get the response entity.
            HttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                System.out.println("REST Response:\n");

                jsonString = EntityUtils.toString(entity).trim();
                if (jsonString.charAt(0) == '[') {
                    JSONArray jsonArray = new JSONArray(jsonString);
                    System.out.println(jsonArray.toString(2));
                } else if (jsonString.charAt(0) == '{') {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    System.out.println(jsonObject.toString(2));
                } else {
                    System.out.println(jsonString);
                }
            }
        } catch (Exception e) {
            // Display error message.
            System.out.println(e.getMessage());
        }
        return jsonString;
    }


    public static String faceDetect1(byte[] bytes) {
        // 定义数据分隔线
        String BOUNDARY = "========7d4a6d158c9";
        // 换行符
        final String newLine = "\r\n";
        final String boundaryPrefix = "--";
        String fileName = "testImage.jpg";
        OutputStream outputStream = null;
        DataOutputStream out = null;
        HttpURLConnection conn = null;
        try {
            String urlStr = FACE_DETECT + "?" + "returnFaceId=true&returnFaceLandmarks=false&returnFaceAttributes=age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise";
            URL url = new URL(urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Ocp-Apim-Subscription-Key", FACE_OCP_APIM_SUBSCRIPTION_KEY);
            outputStream = conn.getOutputStream();
            out = new DataOutputStream(outputStream);

            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix);
            sb.append(BOUNDARY);
            sb.append(newLine);
            // 文件参数,photo参数名可以随意修改
            sb.append("Content-Disposition: form-data;name=\"photo\";filename=\"" + fileName
                    + "\"" + newLine);
            sb.append("Content-Type:application/octet-stream");
            // 参数头设置完以后需要两个换行，然后才是参数内容
            sb.append(newLine);
            sb.append(newLine);

            out.write(sb.toString().getBytes());
            File file = new File("G:\\git\\image\\IMG_1141.JPG");
            InputStream inputStream = new FileInputStream(file);
            byte[] bytesBuffer = new byte[1024];
            int b = inputStream.read(bytesBuffer);
            while (-1 != b) {
                out.write(bytesBuffer);
                b = inputStream.read(bytesBuffer);
            }
            out.write(bytes);
            // 最后添加换行
            out.write(newLine.getBytes());
            // 定义最后数据分隔线，即--加上BOUNDARY再加上--。
            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes();
            // 写上结尾标识
            out.write(end_data);
            out.flush();
            out.close();
            System.out.println(conn.getResponseCode());
            System.out.println(conn.getResponseMessage());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != outputStream) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            return conn.getResponseMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
