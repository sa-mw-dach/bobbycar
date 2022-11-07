import { Component } from '@angular/core';
import { ToastController } from '@ionic/angular';
import { CacheService } from '../providers/cache.service';
import { CarService } from '../providers/car.service';
import { WeatherService } from '../providers/weather.service';

@Component({
  selector: 'app-admin',
  templateUrl: 'admin.page.html',
  styleUrls: ['admin.page.scss'],
})
export class AdminPage {

  bobbycarZones: any = undefined;
  bobbycars: any = undefined;
  carData: any = undefined;
  weatherData: any = undefined;
  engineDataSearch = { carId: "" };
  latitudeSearch = 41.11804887672318;
  longitudeSearch = -73.7198836780401;

  constructor(
    private cacheService: CacheService,
    private carService: CarService,
    private weatherService: WeatherService,
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

    async getWeatherData(type){
        if(this.weatherData === undefined) {
            if(type === 'weather'){
                this.weatherService.getCurrentWeather(this.latitudeSearch, this.longitudeSearch).subscribe((data) => {
                    console.log(data);
                    this.weatherData = data;
                });
            } else if (type === 'pollution'){
                this.weatherService.getAirPollution(this.latitudeSearch, this.longitudeSearch).subscribe((data) => {
                    console.log(data);
                    this.weatherData = data;
                });
            }
        } else {
            this.weatherData = undefined;
        }
    }

    async ngOnInit() {

    }
}
