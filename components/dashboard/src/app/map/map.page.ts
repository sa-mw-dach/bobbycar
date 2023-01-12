import { Component, OnInit } from '@angular/core';
import { Platform } from '@ionic/angular';
import { Router } from '@angular/router';
import { ConfigService } from '../providers/config.service';
import { CarEventsService } from '../providers/ws.service';
import { CarMetricsAggregatedService } from '../providers/carmetrics-aggregated.service';
import { SpeedAlertService } from '../providers/speed-alert.service';
import { CacheService } from '../providers/cache.service';
import { map, tap, delay, retryWhen, delayWhen } from 'rxjs/operators';

@Component({
    selector: 'app-map',
    templateUrl: './map.page.html',
    styleUrls: ['./map.page.scss'],
})
export class MapPage implements OnInit {

    map: google.maps.Map;
    marker: google.maps.Marker;
    infowindow: google.maps.InfoWindow;
    searchArea: google.maps.Circle;
    initialPosition: any;

    isQuery = false;
    showSpeedAlerts = false;

    zones = [];
    bobbycars = new Map();
    metricsAggregated = new Map();
    speedAlerts = new Map();

    constructor(
        private platform: Platform,
        private carEventsService: CarEventsService,
        private cacheService: CacheService,
        private metricsAggregatedService: CarMetricsAggregatedService,
        private speedAlertService: SpeedAlertService,
        private configService: ConfigService,
        private router: Router
        ) {
            this.initialPosition = configService.INITIAL_MAP_POSITION;
        }

    initializeMap() {
        setTimeout(() => {
            this.map = new google.maps.Map(document.getElementById('map'), {
                center: this.initialPosition,
                zoom: 12,
                mapTypeId: 'terrain'
                // mapTypeId: google.maps.MapTypeId.HYBRID,
            });
            this.infowindow = new google.maps.InfoWindow({
                content: ''
            });
            const icon = {
                url: "https://cdn-icons-png.flaticon.com/512/882/882829.png", // url
                scaledSize: new google.maps.Size(50, 50), // scaled size
            };

            const marker = new google.maps.Marker({
                        position: new google.maps.LatLng({ lat: 41.10847153962609, lng: -73.72042478641728 }),
                        // label: 'IBM HQ Armonk',
                        icon: icon,
                        map: this.map
                    });
        }, 10);
    }

    displaySpeedAlerts(){
        if(this.showSpeedAlerts){
            this.showSpeedAlerts = false;
            this.speedAlertService.close();
            this.speedAlerts.clear();
        } else {
            this.showSpeedAlerts = true;
            this.speedAlertService.connect();
            this.speedAlertService.getMessages().pipe(retryWhen((errors) => errors.pipe(delay(1_000)))).subscribe(
                msg => {
                    this.speedAlerts.set(msg.vin, msg);
                    console.log(msg);
                }, // Called whenever there is a message from the server.
                err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
                () => console.log('complete') // Called when connection is closed (for whatever reason).
            );
        }
    }

    simulateQuery(){
        if(this.isQuery){
            this.isQuery = false;
            this.metricsAggregatedService.close();
            this.metricsAggregated.clear();
        } else {
            this.isQuery = true;
            this.metricsAggregatedService.connect();
            this.metricsAggregatedService.getMessages().pipe(retryWhen((errors) => errors.pipe(delay(1_000)))).subscribe(
                msg => {
                    this.metricsAggregated.set(msg.vin, msg);
                    //console.log(this.metricsAggregated);
                }, // Called whenever there is a message from the server.
                err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
                () => console.log('complete') // Called when connection is closed (for whatever reason).
            );
        }
    }

    getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
        const R = 6371; // Radius of the earth in km
        const dLat = this.deg2rad(lat2-lat1);  // deg2rad below
        const dLon = this.deg2rad(lon2-lon1);
        const a =
            Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(this.deg2rad(lat1)) * Math.cos(this.deg2rad(lat2)) *
            Math.sin(dLon/2) * Math.sin(dLon/2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        const d = R * c; // Distance in km
        return d;
    }

    // degree to radius
    deg2rad(deg) {
        return deg * (Math.PI/180);
    }

    createOrUpdateMarker(data){

        if(this.bobbycars.has(data.carid)){
            this.bobbycars.get(data.carid).setPosition(new google.maps.LatLng({ lat: data.lat, lng: data.long }));
        } else {
            console.debug('create marker for carid: ' + data.carid);

            const icon = {
                // BMW: https://assets.stickpng.com/images/580b57fcd9996e24bc43c46e.png
                url: "https://cdn1.iconfinder.com/data/icons/cars-5/512/mercedes-pointer-point-car-auto-poi-map-place-geo-512.png", // url
                scaledSize: new google.maps.Size(40, 40), // scaled size
            };

            const marker = new google.maps.Marker({
                position: new google.maps.LatLng({ lat: data.lat, lng: data.long }),
                title: data.carid,
                map: this.map,
                icon: icon,
                animation: google.maps.Animation.DROP,
                // label: data.carid,
                draggable: false
            });

            // tslint:disable-next-line:only-arrow-functions
            google.maps.event.addListener(marker, 'click', (function(marker, content, infowindow) {
                return function() {
                    infowindow.setContent(
                        `<span style="color: #000000;">
                            <h4>Bobbycar Id:</h4>
                            <p>`+content.carid+`<br/>
                            <h4>Zone:</h4>`+content?.zone?.spec.name+`</p><br/>
                            <ion-button color="danger" href="/car-detail/`+content.carid+`">Car Detail</ion-button>
                        </span>`);
                    infowindow.open(this.map, marker);
                }
            })(marker, data, this.infowindow));

            this.bobbycars.set(data.carid, marker);
        }
    }

    realtimeQuery(){
        if(!this.searchArea) {
            this.searchArea = new google.maps.Circle({
                strokeColor: '#eeeeee',
                strokeOpacity: 0.7,
                strokeWeight: 1,
                fillColor: '#444444',
                fillOpacity: 0.35,
                map: this.map,
                center: this.map.getCenter(),
                editable: true,
                radius: 3000
            });
        } else if(this.searchArea && !this.searchArea.getVisible()) {
            this.searchArea.setCenter(this.map.getCenter());
            this.searchArea.setVisible(true);
        } else {
            this.searchArea.setVisible(false);
        }

    }

    createZone(){
        const circle = new google.maps.Circle({
            strokeColor: '#FF0000',
            strokeOpacity: 0.7,
            strokeWeight: 1,
            fillColor: '#FF0000',
            fillOpacity: 0.35,
            map: this.map,
            center: this.map.getCenter(),
            editable: true,
            radius: 3000
        });
        // this.addCircleListener(circle);
        this.addZoneContextListener(circle);
        this.zones.push(circle);
    }

    addCircleListener(circle: google.maps.Circle) {
        google.maps.event.addListener(circle, 'click', (event) => {
            console.log(event.latLng.toString());
            console.log(circle.getCenter().toString());
            console.log(circle.getRadius());
            console.log(this.getDistanceFromLatLonInKm(event.latLng.lat(), event.latLng.lng(),
                circle.getCenter().lat(), circle.getCenter().lng()))
          });
    }

    addZoneContextListener(circle: google.maps.Circle) {
        google.maps.event.addListener(circle, 'rightclick', (event) => {
            // alert(circle.getCenter().toString()+' '+circle.getRadius());
            circle.setMap(null);
          });
    }

    resetMap() {
        this.bobbycars.forEach(el => {
            el.setMap(null);
        });
        this.bobbycars.clear();

        this.zones.forEach(element => {
            element.setMap(null);
        });
        this.zones = [];

        this.cacheService.getCars()
        .subscribe((data) => {
            console.log(data);
            data.forEach(element => {
                this.createOrUpdateMarker(element);
            });
        });

        this.cacheService.getZones()
        .subscribe((data) => {
            if(this.map){
                data.forEach(element => {
                    const zone = new google.maps.Circle({
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
                    this.zones.push(zone);
                });
            }
        });
    }

    ionViewWillLeave(){
        console.debug('ionViewWillLeave()');
        this.carEventsService.close();
        this.metricsAggregatedService.close();
    }

    async ngOnInit() {

        this.initializeMap();
        this.carEventsService.connect();
        //this.metricsAggregatedService.connect();

        this.cacheService.getZones().subscribe((data) => {
            if(this.map){
                data.forEach(element => {
                    const zone = new google.maps.Circle({
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
                    this.zones.push(zone);
                });
            }
        });

        this.cacheService.getCars().subscribe((data) => {
            console.log(data);
            data.forEach(element => {
                this.createOrUpdateMarker(element);
            });
        });

        this.carEventsService.getMessages().pipe(retryWhen((errors) => errors.pipe(delay(1_000)))).subscribe(
            msg => {
                this.createOrUpdateMarker(msg);
            }, // Called whenever there is a message from the server.
            err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
            () => console.log('complete') // Called when connection is closed (for whatever reason).
        );

    }
}