package com.redhat.bobbycar.ota;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.bobbycar.ota.model.EngineBehavior;
import io.quarkus.runtime.Startup;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Startup
@ApplicationScoped
public class CampaignService {

    private ConcurrentHashMap<String, EngineBehavior> campaigns = new ConcurrentHashMap<>();
    private static final String DEFAULT_ENGINE_CONFIG_JSON = "/engines/default.json";
    private static final String US_ZONE_1_ENGINE_CONFIG_JSON = "/engines/us-zone-1.json";
    private static final String US_ZONE_2_ENGINE_CONFIG_JSON = "/engines/us-zone-2.json";
    private static final String US_ZONE_3_ENGINE_CONFIG_JSON = "/engines/us-zone-3.json";
    private static final String US_ZONE_4_ENGINE_CONFIG_JSON = "/engines/us-zone-4.json";


    public CampaignService(){
        try {
            addCampaign("1", readConfiguration(CampaignService.DEFAULT_ENGINE_CONFIG_JSON));
            addCampaign("us-zone-1", readConfiguration(US_ZONE_1_ENGINE_CONFIG_JSON));
            addCampaign("us-zone-2", readConfiguration(US_ZONE_2_ENGINE_CONFIG_JSON));
            addCampaign("us-zone-3", readConfiguration(US_ZONE_3_ENGINE_CONFIG_JSON));
            addCampaign("us-zone-4", readConfiguration(US_ZONE_4_ENGINE_CONFIG_JSON));
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

    private EngineBehavior readConfiguration(String engineConfig) throws IOException {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(getClass().getResourceAsStream(engineConfig),
                    EngineBehavior.class);
    }

}
