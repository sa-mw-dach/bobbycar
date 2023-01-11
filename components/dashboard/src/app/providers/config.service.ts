import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';


@Injectable({
    providedIn: 'root'
})
export class ConfigService {

    GOOGLE_API_KEY;
    BOBBYCAR_API_KEY;
    SSE_ENDPOINT;
    CAR_EVENTS_ENDPOINT;
    CAR_METRICS_ENDPOINT;
    CAR_ZONECHANGE_ENDPOINT;
    CACHE_ENDPOINT;
    HDPP_ENDPOINT;
    CAR_ENDPOINT;
    CAR_METRICS_AGGREGATED_ENDPOINT;
    SPEED_ALERT_ENDPOINT;
    WEATHER_API_ENDPOINT;
    OTA_SERVER_ENDPOINT;
    ROAD_CLASSIFICATION_ENDPOINT;
    ROAD_CLASSIFICATION_ENABLE;
    INITIAL_MAP_POSITION;
    OCP_API_SERVER;
    OCP_TOKEN;

    constructor(public http: HttpClient) {}

    async loadConfigurationData() {
        console.debug('loadConfigurationData');

        const data = await this.http.get<any>('conf/config.json').toPromise();

        console.debug('App config loaded: ' + JSON.stringify(data));

        this.GOOGLE_API_KEY = data.GOOGLE_API_KEY;
        this.BOBBYCAR_API_KEY = data.BOBBYCAR_API_KEY;
        this.SSE_ENDPOINT = data.SSE_ENDPOINT;
        this.CAR_EVENTS_ENDPOINT = data.CAR_EVENTS_ENDPOINT;
        this.CAR_METRICS_ENDPOINT = data.CAR_METRICS_ENDPOINT;
        this.CAR_ZONECHANGE_ENDPOINT = data.CAR_ZONECHANGE_ENDPOINT;
        this.CACHE_ENDPOINT = data.CACHE_ENDPOINT;
        this.HDPP_ENDPOINT = data.HDPP_ENDPOINT;
        this.CAR_ENDPOINT = data.CAR_ENDPOINT;
        this.CAR_METRICS_AGGREGATED_ENDPOINT = data.CAR_METRICS_AGGREGATED_ENDPOINT;
        this.SPEED_ALERT_ENDPOINT = data.SPEED_ALERT_ENDPOINT;
        this.WEATHER_API_ENDPOINT = data.WEATHER_API_ENDPOINT;
        this.OTA_SERVER_ENDPOINT = data.OTA_SERVER_ENDPOINT;
        this.ROAD_CLASSIFICATION_ENDPOINT = data.ROAD_CLASSIFICATION_ENDPOINT;
        this.ROAD_CLASSIFICATION_ENABLE = data.ROAD_CLASSIFICATION_ENABLE;
        this.INITIAL_MAP_POSITION = data.INITIAL_MAP_POSITION;
        this.OCP_API_SERVER = data.OCP_API_SERVER;
        this.OCP_TOKEN = data.OCP_TOKEN;
    }

}