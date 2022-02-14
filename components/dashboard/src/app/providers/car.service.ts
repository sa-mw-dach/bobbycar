import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ConfigService } from './config.service';

@Injectable({
    providedIn: 'root'
})
export class CarService {

    carsUrl = '/api/cars';

    constructor(public http: HttpClient, private configService: ConfigService) {
    }
    // example -> curl http://car-simulator-bobbycar.apps.ocp4.rhlab.de/api/cars/efcff881-ac7a-4957-b16c-a3f1b7702f06
    getCarById(carId) {
        return this.http.get<any[]>(this.configService.CAR_ENDPOINT+this.carsUrl+"/"+carId);
    }
}