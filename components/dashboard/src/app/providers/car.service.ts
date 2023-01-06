import { HttpClient, HttpHeaders } from '@angular/common/http';
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
    getMetrics() {
        //const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');
        return this.http.get<any[]>(this.configService.CAR_ENDPOINT+"/metrics");
    }
    scaleDeployment(replicas) {
        const headers = { 'Authorization': 'Bearer '+this.configService.OCP_TOKEN, 'Content-Type': 'application/json'};
        const body = {
                       "kind": "Scale",
                       "apiVersion": "autoscaling/v1",
                       "metadata": {
                         "name": "car-simulator",
                         "namespace": "bobbycar"
                       },
                       "spec": {
                         "replicas": parseInt(replicas)
                       }
                     };

        return this.http.put<any[]>(this.configService.OCP_API_SERVER+"/apis/apps/v1/namespaces/bobbycar/deployments/car-simulator/scale", body, { headers });
    }
}