import { Component, OnInit, NgZone } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CarEventsService } from '../providers/ws.service';
import { CarMetricsService } from '../providers/carmetrics.service';
import { ZoneChangeService } from '../providers/zonechange.service';
import { CacheService } from '../providers/cache.service';
import { CarService } from '../providers/car.service';
import { PredictiveService } from '../providers/predictive.service';
import { ConfigService } from '../providers/config.service';
import { ToastController } from '@ionic/angular';
import { map, tap, delay, retryWhen, delayWhen } from 'rxjs/operators';

@Component({
  selector: 'app-car-detail',
  templateUrl: './car-detail.page.html',
  styleUrls: ['./car-detail.page.scss'],
})
export class CarDetailPage implements OnInit {

  map: google.maps.Map;
  initialPosition = { lat: 50.1146997, lng: 8.6185411 };
  currentPosition = { lat: 0, lon: 0 };
  carId = '';
  carMetric = { driverId: '', manufacturer: '', model: '', co2: '', fuel: '', gear: '', rpm: '', speed: '', zone: 'Default Zone', vin: '' };
  marker: google.maps.Marker;
  panorama: google.maps.StreetViewPanorama;
  sv = new google.maps.StreetViewService();
  streetName = '';
  showHUD = false;
  showDriverMonitoring = false;
  carBg = 'MB';
  engineOverlayHidden: boolean = true;
  engineData;
  weatherData;
  roadClassificationResult = {
     "prediction": {
       "Road Condition": "Great",
       "Vehicle Config": "166Y.2"
     }
   };
  enableStreetView: boolean = true;

  constructor(
    private carEventsService: CarEventsService,
    private carMetricsService: CarMetricsService,
    private cacheService: CacheService,
    private carService: CarService,
    private zoneChangeService: ZoneChangeService,
    private configService: ConfigService,
    private route: ActivatedRoute,
    public toastController: ToastController,
    private predictiveService: PredictiveService,
    private zone: NgZone,
    ) {
        console.log('Constructing car-detail-page');
        this.carBg = this.configService.DEFAULT_CAR_BRAND;
    }

    // ***********************
    // Presenting a message at the top of the screen
    // ***********************
    async presentToast(msg, duration) {
      const toast = await this.toastController.create({
        message: msg,
        duration: duration,
        color: 'danger',
        position: 'top'
      });
      toast.present();
    }

    // ***********************
    // Initialize the Google Map, Google Streetview and set the marker for the vehicle
    // ***********************
    initializeMap() {
        setTimeout(() => {
            this.map = new google.maps.Map(document.getElementById('map'), {
                center: this.initialPosition,
                zoom: 13,
                disableDefaultUI: true,
                mapTypeControl: false,
                mapTypeId: 'terrain'
            });

            const icon = {
                url: "assets/luxoft-marker.png",
                scaledSize: new google.maps.Size(30, 30), // scaled size
            };

            /*
            if(this.carBg === 'VW') {
                icon.url = "assets/vw-marker.png"
            } else if (this.carBg === 'BMW'){
                icon.url = "assets/bmw-marker.png"
            } else if (this.carBg === 'MB'){
                icon.url = "assets/mb-marker.png"
            } else if (this.carBg === 'P'){
                icon.url = "assets/porsche-marker.png";
                icon.scaledSize = new google.maps.Size(24,30);
            } else if (this.carBg === 'FORD'){
                icon.url = "assets/ford-marker.png";
                icon.scaledSize = new google.maps.Size(40,30);
            } else if (this.carBg === 'F150'){
                icon.url = "assets/ford-marker.png";
                icon.scaledSize = new google.maps.Size(40,30);
            }
            */

            this.marker = new google.maps.Marker({
                position: new google.maps.LatLng(this.initialPosition),
                title: 'Car Detail',
                map: this.map,
                icon: icon,
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

        }, 100);
    }

    // ***********************
    // Update the marker and Google Streetview for new positions
    // ***********************
    async createOrUpdateMarker(data) {
        if(data.carid === this.carId) {

          this.marker.setPosition(new google.maps.LatLng({ lat: data.lat, lng: data.long }));
          this.map.setCenter({ lat: data.lat, lng: data.long });

          this.currentPosition.lat = data.lat;
          this.currentPosition.lon = data.long;

          await this.sv.getPanorama({
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

  // ***********************
  // Show or hide the Heads-Up display with the telemetry data
  // ***********************
  toggleHUD(){
    if(this.showHUD) {
      this.showHUD = false;
    } else {
      this.showHUD = true;
    }
  }

    // ***********************
    // Show or hide the Driver Monitoring application in the Infotainment section.
    // ***********************
    toggleInfotainment(){
        if(this.showDriverMonitoring) {
        this.showDriverMonitoring = false;
        } else {
        this.showDriverMonitoring = true;
        }
    }

    // ***********************
    // Enable or disable Google Streetview updates
    // ***********************
    toggleStreetView(){
        if(this.enableStreetView) {
          this.enableStreetView = false;
        } else {
          this.enableStreetView = true;
        }
    }

  // ***********************
  // Calling the Weather API and optionally the Road Classification service
  // ***********************
  getWeatherData(){
    if(this.engineOverlayHidden) {
        this.predictiveService.getCurrentWeather(this.currentPosition.lat, this.currentPosition.lon, 'ibm').subscribe(
        (data) => {
            this.weatherData = data;
            this.engineOverlayHidden = false;
        },
        (err) => {
            console.error(err);
            this.presentToast("The Weather Service is not enabled for this vehicle!", 6000);
            }
        );
        if(this.configService.ROAD_CLASSIFICATION_ENABLE === 'true'){
            this.predictiveService.getRoadClassification([0.36, 0.20, 9.79, 0.009, -0.13, -0.02, 0.14]).subscribe((data) => {
                this.roadClassificationResult = data;
                console.log(data);
            },
            err => console.error(err));
        }
    } else {
        this.engineOverlayHidden = true;
        this.weatherData = undefined;
        this.roadClassificationResult = undefined;
    }
  }

  // ***********************
  // Switch cockpit views
  // ***********************
  switchCockpitView(viewId) {
    this.carBg = viewId;
    // this.configService.DEFAULT_CAR_BRAND = viewId;
    this.initializeMap();
  }

  // ***********************
  // Calling the Car Service API to get the current engine configuration
  // ***********************
  showConfig() {
    if(this.engineOverlayHidden) {
        let temp = this.carService.getCarById(this.carId).subscribe((data) => {
            console.log(data);
            this.engineData = data;
            this.engineOverlayHidden = false;
            // this.presentConfig(JSON.stringify(data, null, 4));
        })
    } else {
        this.engineOverlayHidden = true;
        this.engineData = undefined;
    }
  }

  // ***********************
  // Closing the Websocket connections when leaving the page
  // ***********************
  ionViewWillLeave(){
    console.debug('ionViewWillLeave()');
    // this.carEventsService.close();
    this.carMetricsService.close();
    this.zoneChangeService.close();
  }

    // ***********************
    // TODO: Implement data collection and upload to S3 for this specific vehicle id
    // ***********************
    scheduleMaintenance(){
        this.presentToast('You have scheduled a maintenance appointment. The collection of telemetry data has been enabled and will be forwarded to your car repair shop.', 5000);
    }

  // ***********************
  // Initialize the car detail page. This runs at the beginning...
  // ***********************
    ngOnInit() {

    this.carId = this.route.snapshot.paramMap.get('id');    // get the car id from the URL
    this.initializeMap();               // initialize the Google Map
    this.carEventsService.connect();    // Open Websocket connection to retrieve GPS positions
    this.carMetricsService.connect();   // Open Websocket connection to retrieve car metrics
    this.zoneChangeService.connect();   // Open Websocket connection to retrieve zonechange events

    this.cacheService.getZones()    // Retrieve the Bobbycarzones from the cache and create circles on the map
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

    // Retrieving new GPS postions and updating the marker and Google Streetview
    this.carEventsService.getMessages().pipe(retryWhen((errors) => errors.pipe(delay(1_000)))).subscribe(
      msg => {
          this.createOrUpdateMarker(msg);
      }, // Called whenever there is a message from the server.
      err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
      () => console.log('complete') // Called when connection is closed (for whatever reason).
    );

    // Retrieving new car metrics
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

    // Retrieving zone change events and displaying them
    this.zoneChangeService.getMessages().pipe(retryWhen((errors) => errors.pipe(delay(1_000)))).subscribe(
      msg => {
          if(msg.carId === this.carId){
            if(msg.nextZoneId !== null){
                this.carMetric.zone = msg.nextZoneId;
                this.presentToast('ZONE CHANGE EVENT! The vehicle '+ msg.carId + ' is entering the Bobbycarzone: ' + msg.nextZoneId, 5000);
            } else {
                this.carMetric.zone = 'Default Zone';
                this.presentToast('ZONE CHANGE EVENT! The vehicle '+ msg.carId + ' is leaving the Bobbycarzone.', 5000);
            }
          }
      }, // Called whenever there is a message from the server.
      err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
      () => console.log('complete') // Called when connection is closed (for whatever reason).
    );

  }

}
