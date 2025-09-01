package api;

import org.testng.Assert;
import org.testng.annotations.Test;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CountriesAPITest {

    @Test
    public void testFetchCountriesGraphQL() throws Exception {
        String query = "{ countries { code name continent { name } } }";
        String jsonInput = "{ \"query\": \"" + query.replace("\"", "\\\"") + "\" }";

        URL url = new URL("https://countries.trevorblades.com/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = con.getResponseCode();
        Assert.assertEquals(status, 200, "Status code should be 200");

        BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8)
        );
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        con.disconnect();

        String resp = response.toString();
        Assert.assertTrue(resp.contains("South Africa"), "Response should contain South Africa");
        Assert.assertTrue(resp.contains("code"), "Each country must have code");
        Assert.assertTrue(resp.contains("continent"), "Each country must have continent");
    }
}
