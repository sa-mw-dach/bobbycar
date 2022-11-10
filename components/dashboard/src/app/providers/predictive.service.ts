import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConfigService } from './config.service';

@Injectable({
    providedIn: 'root'
})
export class PredictiveService {

    constructor(public http: HttpClient, private configService: ConfigService) {
    }

    // Weather API Endpoint, default provider is OpenWeatherMap specify 'ibm' for IBM weather service
    getCurrentWeather(lat, lon, provider) {
        return this.http.get<any[]>(this.configService.WEATHER_API_ENDPOINT+'/weather/current/lat/'+lat+'/lon/'+lon+'?provider='+provider);
    }

    // OpenWeatherMap Endpoint
    getAirPollution(lat, lon) {
        return this.http.get<any[]>(this.configService.WEATHER_API_ENDPOINT+'/weather/pollution/lat/'+lat+'/lon/'+lon);
    }

    // Red Hat AI/ML model serving service
    getRoadClassification(data){
        return this.http.post<any>(this.configService.ROAD_CLASSIFICATION_ENDPOINT+'/predictions', data);
    }

}