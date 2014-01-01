package org.lantern.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * StatHat metrics reporting utility from <a
 * href="https://raw.github.com/stathat/shlibs/master/java/StatHat.java"
 * >here</a>.
 */
public class StatHat {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatHat.class);
    private static final String STAT_HAT_EMAIL = "ox@getlantern.org";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static void httpPost(String path, String data) {
        try {
            URL url = new URL(path);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(data);
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
            }
            wr.close();
            rd.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void ezPostValues(List<Map<String, Object>> values) {
        LOGGER.debug("Posting Values");
        Map<String, Object> request = new HashMap<String, Object>();
        request.put("ezkey", STAT_HAT_EMAIL);
        request.put("data", values);
        try {
            URL url = new URL("http://api.stathat.com/ez");
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            MAPPER.writeValue(conn.getOutputStream(), request);

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                LOGGER.error(line);
                // Just consume the response
                // TODO: check it
            }
            conn.getOutputStream().close();
            rd.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void ezPostValue(String statName, Number value) {
        Double now = new Double(System.currentTimeMillis() / 1000.0);
        try {
            String data = URLEncoder.encode("ezkey", "UTF-8") + "="
                    + URLEncoder.encode(STAT_HAT_EMAIL, "UTF-8");
            data += "&" + URLEncoder.encode("stat", "UTF-8") + "="
                    + URLEncoder.encode(statName, "UTF-8");
            data += "&" + URLEncoder.encode("value", "UTF-8") + "="
                    + URLEncoder.encode(value.toString(), "UTF-8");
            data += "&" + URLEncoder.encode("t", "UTF-8") + "="
                    + URLEncoder.encode(now.toString(), "UTF-8");
            httpPost("http://api.stathat.com/ez", data);
        } catch (Exception e) {
            System.err.println("ezPostValue exception:  " + e);
        }
    }
}