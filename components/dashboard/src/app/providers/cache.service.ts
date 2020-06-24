import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConfigService } from './config.service';

@Injectable({
    providedIn: 'root'
})
export class CacheService {

    zonesUrl = '/zones';
    carsUrl = '/cars';

    constructor(public http: HttpClient, private configService: ConfigService) {
    }

    getZones() {
        return this.http.get<any[]>(this.configService.CACHE_ENDPOINT+this.zonesUrl);
    }

    getCars() {
        return this.http.get<any[]>(this.configService.CACHE_ENDPOINT+this.carsUrl);
    }

}