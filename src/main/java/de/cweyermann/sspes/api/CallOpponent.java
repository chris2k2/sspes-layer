package de.cweyermann.sspes.api;

import de.cweyermann.sspes.api.repo.Participant;
import lombok.extern.log4j.Log4j2;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;

@Log4j2
public class CallOpponent {

    public String getChoiceOf(Participant team, String hint) {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(team.getUrl() + hint);

            try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    log.info(team.getName() + ": " + result);
                    String lowerCase = result.toLowerCase();
                    String replaced = lowerCase.replaceAll("[^a-z]", "");
                    log.info("saving \": " + replaced);
                    return replaced;
                } else {
                    log.error(team.getName() + " returned nothing!");
                    return null;
                }
            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public String buildHint(Participant opponent) {
        String lastChoice = encode(opponent.getLastChoice());
        String name = encode(opponent.getName());


        return "?opponent=" + name + "&lastChoice=" + lastChoice;
    }

    private String encode(String lastChoice) {
        String urlLc = "unknown";
        try{
            if (lastChoice == null) {
                lastChoice = "";
            }
            urlLc = URLEncoder.encode(lastChoice, "UTF-8");
        } catch(Exception e) {
            log.warn("cannot decode: " + lastChoice);
        }
        return urlLc;
    }
}
