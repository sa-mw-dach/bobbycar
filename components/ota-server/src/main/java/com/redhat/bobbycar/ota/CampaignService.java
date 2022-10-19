package com.redhat.bobbycar.ota;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.bobbycar.ota.model.EngineBehavior;
import io.quarkus.runtime.Startup;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

@Startup
@ApplicationScoped
public class CampaignService {

    private ConcurrentHashMap<String, EngineBehavior> campaigns = new ConcurrentHashMap<>();
    private static final String DEFAULT_ENGINE_CONFIG_JSON = "/engines/default.json";

    public CampaignService(){
        EngineBehavior defaultConfig = null;
        try {
            defaultConfig = readConfiguration();
            addCampaign("1", defaultConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addCampaign(String id, EngineBehavior payload) {
        campaigns.put(id, payload);
    }

    public void deleteCampaign(String id) {
        campaigns.remove(id);
    }

    public void clearCampaigns() {
        campaigns.clear();
    }

    public EngineBehavior getPayloadById(String id){
        return campaigns.get(id);
    }

    private EngineBehavior readConfiguration() throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(getClass().getResourceAsStream(CampaignService.DEFAULT_ENGINE_CONFIG_JSON),
                    EngineBehavior.class);
    }

}
