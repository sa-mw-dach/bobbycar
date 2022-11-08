import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConfigService } from './config.service';

@Injectable({
    providedIn: 'root'
})
export class PredictiveService {

    constructor(public http: HttpClient, private configService: ConfigService) {
    }

    getCurrentWeather(lat, lon) {
        return this.http.get<any[]>(this.configService.OPEN_WEATHERMAP_API_ENDPOINT+'/weather/current/lat/'+lat+'/lon/'+lon);
    }

    getAirPollution(lat, lon) {
        return this.http.get<any[]>(this.configService.OPEN_WEATHERMAP_API_ENDPOINT+'/weather/pollution/lat/'+lat+'/lon/'+lon);
    }

    getRoadClassification(data){
        return this.http.post<any>(this.configService.ROAD_CLASSIFICATION_ENDPOINT+'/predictions', data);
    }

}