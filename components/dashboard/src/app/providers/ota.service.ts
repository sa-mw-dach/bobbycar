import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConfigService } from './config.service';

@Injectable({
    providedIn: 'root'
})
export class OTAService {

    constructor(public http: HttpClient, private configService: ConfigService) {
    }

    getPayloadForCampaign(campaignId) {
        return this.http.get<any[]>(this.configService.OTA_SERVER_ENDPOINT+'/api/ota/payload/'+campaignId);
    }

    assignVehicleToCampaign(vehicleId, campaignId) {
        return this.http.get<any[]>(this.configService.OTA_SERVER_ENDPOINT+'/api/ota/assign/'+vehicleId+'/campaign/'+campaignId);
    }

    createPayloadForCampaign(campaignId, payload) {
        return this.http.post<any[]>(this.configService.OTA_SERVER_ENDPOINT+'/api/ota/payload/'+campaignId, JSON.parse(payload));
    }

}