package com.zhoubo.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.net.URI;

/**
 * Created by zhoubo on 2017/7/12.
 */
public class HttpUtil {
    public static InputStream getImage(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        InputStream inputStream = null;
        try {
            // NOTE: You must use the same location in your REST call as you used to obtain your subscription keys.
            //   For example, if you obtained your subscription keys from westus, replace "westcentralus" in the
            //   URL below with "westus".
            URIBuilder uriBuilder = new URIBuilder(url);
//            URIBuilder uriBuilder = new URIBuilder("https://westcentralus.api.cognitive.microsoft.com/vision/v1.0/generateThumbnail");

//            uriBuilder.setParameter("width", "100");
//            uriBuilder.setParameter("height", "150");
//            uriBuilder.setParameter("smartCropping", "true");

            URI uri = uriBuilder.build();
            HttpGet request = new HttpGet(uri);
//            HttpPost request = new HttpPost(uri);

            // Request headers.
//            request.setHeader("Content-Type", "application/json");

            // NOTE: Replace the "Ocp-Apim-Subscription-Key" value with a valid subscription key.
//            request.setHeader("Ocp-Apim-Subscription-Key", "c8d980c17d494213958318ed78c1b8d4");

            // Request body. Replace the example URL with the URL for the JPEG image of a person.
//            StringEntity requestEntity = new StringEntity("{\"url\":" + url + "}");
//            StringEntity requestEntity = new StringEntity("{\"url\":\"http://a.hiphotos.baidu.com/zhidao/pic/item/e4dde71190ef76c647fd64809c16fdfaaf51676a.jpg\"}");
//            request.setEntity(requestEntity);

            HttpResponse response = httpClient.execute(request);
//            System.out.println(response);

            // Display the thumbnail.
            HttpEntity httpEntity = response.getEntity();
            inputStream = httpEntity.getContent();
//            displayImage(httpEntity.getContent());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return inputStream;
    }
}
