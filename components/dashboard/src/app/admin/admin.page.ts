import { Component } from '@angular/core';
import { ToastController } from '@ionic/angular';
import { CacheService } from '../providers/cache.service';
import { CarService } from '../providers/car.service';
import { PredictiveService } from '../providers/predictive.service';
import { OTAService } from '../providers/ota.service';
import { ConfigService } from '../providers/config.service';

@Component({
  selector: 'app-admin',
  templateUrl: 'admin.page.html',
  styleUrls: ['admin.page.scss'],
})
export class AdminPage {

  bobbycarZones: any = undefined;
  bobbycars: any = undefined;
  carData: any = undefined;
  carMetrics: any = undefined;
  weatherData: any = undefined;
  otaPayloadData: any = undefined;
  otaPayloadInput: any = undefined;
  engineDataSearch = { carId: "" };
  otaCampaignIdSearch = 1;
  latitudeSearch = 41.11804887672318;
  longitudeSearch = -73.7198836780401;
  carSimReplicas: number = 1;

  constructor(
    private cacheService: CacheService,
    private carService: CarService,
    private predictiveService: PredictiveService,
    private otaService: OTAService,
    private configService: ConfigService,
    private toastController: ToastController,
    ) {}

    async presentToast(msg, duration) {
        const toast = await this.toastController.create({
          message: msg,
          duration: duration,
          color: 'danger',
          position: 'top'
        });
        toast.present();
    }

    async getOTAPayload(){
        if(this.otaPayloadInput === undefined) {
            this.otaService.getPayloadForCampaign(this.otaCampaignIdSearch).subscribe((data) => {
                this.otaPayloadInput = JSON.stringify(data, null, 4);
            });
        } else {
            this.otaPayloadInput = undefined;
        }
    }

    async createOTAPayload(){
        if(this.otaPayloadInput !== undefined && this.otaCampaignIdSearch) {
            this.otaService.createPayloadForCampaign(this.otaCampaignIdSearch, this.otaPayloadInput).subscribe((data) => {
                this.otaPayloadInput = undefined;
                this.presentToast("You have successfully updated the payload for the campaign: "+this.otaCampaignIdSearch, 3000);
            });
        }
    }


    async getZonesFromCache(){
        if(this.bobbycarZones === undefined) {
            this.cacheService.getZones().subscribe((data) => {
                this.bobbycarZones = data;
            });
        } else {
            this.bobbycarZones = undefined;
        }
    }

    async getCarsFromCache(){
        if(this.bobbycars === undefined) {
            this.cacheService.getCars().subscribe((data) => {
                this.bobbycars = data;
            });
        } else {
            this.bobbycars = undefined;
        }
    }

    async clearCache(){
        this.cacheService.clearCache().subscribe((data) => {
            console.log(data);
            this.presentToast('The Zones and Cars Cache has been cleared.', 3000);
        });
    }

    async getEngineDataByCarId(){
        if(this.carData === undefined) {
            this.carService.getCarById(this.engineDataSearch.carId).subscribe((data) => {
                this.carData = data;
            });
        } else {
            this.carData = undefined;
            this.engineDataSearch = { carId: "" };
        }
    }

    async getCarMetrics(){
        if(this.carMetrics === undefined) {
            this.carService.getMetrics().subscribe((data) => {
                this.carMetrics = data;
            });
        } else {
            this.carMetrics = undefined;
        }
    }

    async getWeatherData(type){
        if(this.weatherData === undefined) {
            if(type === 'weather'){
                this.predictiveService.getCurrentWeather(this.latitudeSearch, this.longitudeSearch, 'owm').subscribe((data) => {
                    console.log(data);
                    this.weatherData = data;
                });
            } else if (type === 'pollution'){
                this.predictiveService.getAirPollution(this.latitudeSearch, this.longitudeSearch).subscribe((data) => {
                    console.log(data);
                    this.weatherData = data;
                });
            }
        } else {
            this.weatherData = undefined;
        }
    }

    async getWeatherObservationData(type){
        if(this.weatherData === undefined) {
            this.predictiveService.getCurrentWeather(this.latitudeSearch, this.longitudeSearch, 'ibm').subscribe((data) => {
                console.log(data);
                this.weatherData = data;
            });
        } else {
            this.weatherData = undefined;
        }
    }

    async scaleCarSim(){
        this.carService.scaleDeployment(this.carSimReplicas).subscribe((data) => {
            console.dir(data);
            this.clearCache();
            this.presentToast(JSON.stringify(data, null, 2), 5000);
        });
    }

    async ngOnInit() {

    }
}
