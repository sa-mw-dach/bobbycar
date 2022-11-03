import { Component, OnInit, NgZone } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CarEventsService } from '../providers/ws.service';
import { CarMetricsService } from '../providers/carmetrics.service';
import { ZoneChangeService } from '../providers/zonechange.service';
import { CacheService } from '../providers/cache.service';
import { CarService } from '../providers/car.service';
import { ToastController } from '@ionic/angular';
import { map, tap, delay, retryWhen, delayWhen } from 'rxjs/operators';

import * as am4core from "@amcharts/amcharts4/core";
import * as am4charts from "@amcharts/amcharts4/charts";
import am4themes_animated from "@amcharts/amcharts4/themes/animated";

am4core.useTheme(am4themes_animated);

@Component({
  selector: 'app-car-detail',
  templateUrl: './car-detail.page.html',
  styleUrls: ['./car-detail.page.scss'],
})
export class CarDetailPage implements OnInit {

  private chart: am4charts.XYChart;
  map: google.maps.Map;
  initialPosition = { lat: 50.1146997, lng: 8.6185411 };
  carId = '';
  carMetric = { driverId: '', manufacturer: '', model: '', co2: '', fuel: '', gear: '', rpm: '', speed: '', zone: 'Default Zone', vin: '' };
  marker: google.maps.Marker;
  panorama: google.maps.StreetViewPanorama;
  sv = new google.maps.StreetViewService();
  streetName = '';
  showHUD = false;
  showDriverMonitoring = false;
  carBg = 'S';
  // infotainmentContent = 'https://www.youtube.com/embed/kFyxe_tCxto?autoplay=1&mute=1&controls=0&loop=1'
  engineOverlayHidden: boolean = true;
  engineData;
  enableStreetView: boolean = true;

  constructor(
    private carEventsService: CarEventsService,
    private carMetricsService: CarMetricsService,
    private cacheService: CacheService,
    private carService: CarService,
    private zoneChangeService: ZoneChangeService,
    private route: ActivatedRoute,
    public toastController: ToastController,
    private zone: NgZone,
    ) {}

  initializeMap() {

    setTimeout(() => {

        this.map = new google.maps.Map(document.getElementById('map'), {
            center: this.initialPosition,
            zoom: 13,
            disableDefaultUI: true,
            mapTypeControl: false,
        });

        this.marker = new google.maps.Marker({
          position: new google.maps.LatLng(this.initialPosition),
          title: 'Car Detail',
          map: this.map,
          draggable: false
      });

      this.panorama = new google.maps.StreetViewPanorama(
        document.getElementById('pano'),
        {
          position: this.initialPosition,
          pov: {heading: 360, pitch: 0},
          zoom: 1
        });

        this.map.setStreetView(this.panorama);

    }, 10);
  }

  toggleHUD(){
    if(this.showHUD) {
      this.showHUD = false;
    } else {
      this.showHUD = true;
    }
  }

  toggleInfotainment(){
      if(this.showDriverMonitoring) {
        this.showDriverMonitoring = false;
      } else {
        this.showDriverMonitoring = true;
      }
    }

    toggleStreetView(){
        if(this.enableStreetView) {
          this.enableStreetView = false;
        } else {
          this.enableStreetView = true;
        }
      }

  async showConfig(){
    if(this.engineOverlayHidden) {
        let temp = await this.carService.getCarById(this.carId).subscribe((data) => {
            this.engineData = data;
            this.engineOverlayHidden = false;
            // this.presentConfig(JSON.stringify(data, null, 4));
        })
    } else {
        this.engineOverlayHidden = true;
    }

  }

  async presentToast() {
    const toast = await this.toastController.create({
      message: 'ZONE CHANGE EVENT: Applying new zone configuration.',
      duration: 4000,
      color: 'danger',
      position: 'top'
    });
    toast.present();
  }

  ionViewWillLeave(){
    console.debug('ionViewWillLeave()');
    this.carEventsService.close();
    this.carMetricsService.close();
    this.zoneChangeService.close();
  }



  createOrUpdateMarker(data){
    if(data.carid === this.carId){
      this.marker.setPosition(new google.maps.LatLng({ lat: data.lat, lng: data.long }));
      this.map.setCenter({ lat: data.lat, lng: data.long });

      this.sv.getPanorama({
        location: { lat: data.lat, lng: data.long },
        radius: 50
    }, (result, status) => {
          // console.log(result);
          if (status === google.maps.StreetViewStatus.OK && this.enableStreetView) {
              this.panorama.setPosition({ lat: data.lat, lng: data.long });
              this.streetName = result.location.description;
              // const calcHeading = google.maps.geometry.spherical.computeHeading(result.location.latLng, result.location.latLng);
                this.panorama.setPov({
                  heading: result.tiles.centerHeading,
                  pitch: -2
              });
          }
     });

    }
}

  ngOnInit() {

    this.carId = this.route.snapshot.paramMap.get('id');
    this.initializeMap();
    this.carEventsService.connect();
    this.carMetricsService.connect();
    this.zoneChangeService.connect();

    this.cacheService.getZones()
        .subscribe((data) => {
            if(this.map){
                data.forEach(element => {
                    // tslint:disable-next-line:no-unused-expression
                    new google.maps.Circle({
                        strokeColor: '#FF0000',
                        strokeOpacity: 0.7,
                        strokeWeight: 1,
                        fillColor: '#FF0000',
                        fillOpacity: 0.35,
                        map: this.map,
                        center: { lat: element.spec.position.lat, lng: element.spec.position.lng },
                        editable: false,
                        radius: element.spec.radius
                    });
                });
            }
        });

    this.carEventsService.getMessages().pipe(retryWhen((errors) => errors.pipe(delay(1_000)))).subscribe(
      msg => {
          this.createOrUpdateMarker(msg);
      }, // Called whenever there is a message from the server.
      err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
      () => console.log('complete') // Called when connection is closed (for whatever reason).
    );

    this.carMetricsService.getMessages().pipe(retryWhen((errors) => errors.pipe(delay(1_000)))).subscribe(
      msg => {
          if(msg.driverId === this.carId){
            this.carMetric.driverId = msg.driverId;
            this.carMetric.vin = msg.vin;
            this.carMetric.manufacturer = msg.manufacturer;
            this.carMetric.model = msg.model;
            this.carMetric.co2 = msg.engineData.co2Emission;
            this.carMetric.fuel = msg.engineData.fuelConsumptionPer100km;
            this.carMetric.gear = msg.engineData.gear;
            this.carMetric.rpm = msg.engineData.rpm;
            this.carMetric.speed = msg.engineData.speedInKmh;
          }
      }, // Called whenever there is a message from the server.
      err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
      () => console.log('complete') // Called when connection is closed (for whatever reason).
    );

    this.zoneChangeService.getMessages().pipe(retryWhen((errors) => errors.pipe(delay(1_000)))).subscribe(
      msg => {
          if(msg.carId === this.carId){
            this.presentToast();
            if(msg.nextZoneId !== null){
                this.carMetric.zone = msg.nextZoneId;
            } else {
                this.carMetric.zone = 'Default Zone';
            }
          }
      }, // Called whenever there is a message from the server.
      err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
      () => console.log('complete') // Called when connection is closed (for whatever reason).
    );

  }

}
