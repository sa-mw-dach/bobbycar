import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';


@Injectable({
    providedIn: 'root'
})
export class ConfigService {

    GOOGLE_API_KEY;
    BOBBYCAR_API_KEY;
    SSE_ENDPOINT;
    WS_ENDPOINT;
    CACHE_ENDPOINT;

    constructor(public http: HttpClient) {}

    async loadConfigurationData() {
        console.debug('loadConfigurationData');

        const data = await this.http.get<any>('conf/config.json').toPromise();

        console.debug('App config loaded: ' + JSON.stringify(data));

        this.GOOGLE_API_KEY = data.GOOGLE_API_KEY;
        this.BOBBYCAR_API_KEY = data.BOBBYCAR_API_KEY;
        this.SSE_ENDPOINT = data.SSE_ENDPOINT;
        this.WS_ENDPOINT = data.WS_ENDPOINT;
        this.CACHE_ENDPOINT = data.CACHE_ENDPOINT;
    }

}