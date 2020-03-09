package org.telegram.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.telegram.dto.LocationData;

import java.io.IOException;
import java.util.*;

@Slf4j
public class HttpRequest {
    private static final String CORONA_MAIN_URL = "https://corona-countries-tracker.herokuapp.com/";
    private static final String CORONA_PATH = "corona/";

    public List<LocationData> getLocationsData(String country) throws IOException, NotFound {
        List<LocationData> locationsData = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(CORONA_MAIN_URL + CORONA_PATH + country);
            CloseableHttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                String result = EntityUtils.toString(entity);
                ObjectMapper mapper = new ObjectMapper();
                log.debug("result = {}", result);
                locationsData = mapper.readValue(result, new TypeReference<List<LocationData>>() {
                });
            }
        } catch (Exception e) {
            throw new NotFound();
        }
        return locationsData;
    }

}
