import { Component, OnInit } from '@angular/core';
import { ToastController } from '@ionic/angular';
import { Platform } from '@ionic/angular';
import { Router } from '@angular/router';

import { ConfigService } from '../providers/config.service';
import { CarEventsService } from '../providers/ws.service';
import { CacheService } from '../providers/cache.service';
import { ZoneChangeService } from '../providers/zonechange.service';

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

    zones = [];
    carMarker = new Map();
    carZones = new Map()

    carBg = 'CAR1';

    constructor(
        private platform: Platform,
        private carEventsService: CarEventsService,
        private cacheService: CacheService,
        private zoneChangeService: ZoneChangeService,
        private configService: ConfigService,
        private router: Router,
        private toastController: ToastController,
    ) {
        this.initialPosition = configService.INITIAL_MAP_POSITION;
        this.carBg = this.configService.DEFAULT_CAR_BRAND;
    }

    async initializeMap() {
        await setTimeout(() => {
            this.map = new google.maps.Map(document.getElementById('map'), {
                center: this.initialPosition,
                zoom: 15,
                mapTypeId: google.maps.MapTypeId.TERRAIN,
            });
            this.infowindow = new google.maps.InfoWindow({
                content: ''
            });
        }, 100);
    }

    async presentToast(msg, duration, color) {
        const toast = await this.toastController.create({
            message: msg,
            duration: duration,
            color: color,
            position: 'top'
        });
        toast.present();
    }

    async clearCache() {
        this.cacheService.clearCache().subscribe((data) => {
            console.log(data);
            this.presentToast('The Zones and Cars Cache has been cleared.', 3000, 'danger');
        });
    }

    public getCarInZone(carid) {
        if (this.carZones.has(carid)) {
            return this.carZones.get(carid)
        } else {
            return 'red'
        }
    }

    private createMarker(carid, map, lat, long, zone) {
        const icon = {
            url: "assets/luxoft-marker-white.png",
            scaledSize: new google.maps.Size(30, 30) // scaled size
        };

        const marker = new google.maps.Marker({
            position: new google.maps.LatLng({ lat: lat, lng: long }),
            title: carid,
            map: map,
            icon: icon,
            // animation: google.maps.Animation.DROP,
            draggable: false,
            optimized: true
        });

        return marker
    }

    private createOrUpdateMarker(data) {

        if (this.carMarker.has(data.carid)) {
            this.carMarker.get(data.carid).setPosition(new google.maps.LatLng({ lat: data.lat, lng: data.long }));
            console.log(data)
        } else {            
            const marker = this.createMarker(data.carid, this.map, data.lat, data.long, 'red')

            google.maps.event.addListener(marker, 'click', (function (marker, content, infowindow, zones) {
                return function () {
                    infowindow.setContent(
                        `<span style="color: #000000;">
                            <b>`+ content.carid + `</b>
                            <br>Zone: `+ zones.get(content.carid) + `</br>
                        </span>`);
                    infowindow.open(this.map, marker);
                }
            })(marker, data, this.infowindow, this.carZones));

            this.carMarker.set(data.carid, marker);
            this.carZones.set(data.carid, 'red')
        }
    }

    public resetMap() {
        this.carMarker.forEach(el => {
            el.setMap(null);
        });
        this.carMarker.clear();
        this.carZones.clear();

        this.zones.forEach(element => {
            element.setMap(null);
        });
        this.zones = [];

        this.cacheService.getZones()
            .subscribe((data) => {
                if (this.map) {
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

        this.cacheService.getCars()
            .subscribe((data) => {
                data.forEach(element => {
                    this.createOrUpdateMarker(element);
                });
            });
    }

    ionViewWillEnter() {
        console.debug('ionViewWillEnter()');
        this.initializeMap();

        this.cacheService.getZones().subscribe((data) => {
            if (this.map) {
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
            // console.log(data);
            data.forEach(element => {
                this.createOrUpdateMarker(element);
            });
        });
    }

    ionViewWillLeave() {
        console.debug('ionViewWillLeave()');
        this.carMarker.clear();
        this.carZones.clear();

        this.zones = [];

        this.carEventsService.close();
        this.zoneChangeService.close();
    }

    ngOnInit() {

        this.carEventsService.connect();
        this.carEventsService.getMessages().pipe(retryWhen((errors) => errors.pipe(delay(1_000)))).subscribe(
            msg => {
                this.createOrUpdateMarker(msg);
            },
            err => console.error(err),
            () => console.log('Connection has been closed')
        );

        // Retrieving zone change events and displaying them
        this.zoneChangeService.connect();
        this.zoneChangeService.getMessages().pipe(retryWhen((errors) => errors.pipe(delay(1_000)))).subscribe(
            msg => {
                //console.log(msg)
                
                this.cacheService.getCar(msg.carId).subscribe(car => {
                    console.log(car)
                });
                    

                console.log(this.cacheService.getCar(msg.carId))
                if (msg.nextZoneId !== null) {
                    this.carZones.set(msg.carId, msg.nextZoneId);
                    this.presentToast('Vehicle ' + msg.carId + ' is entering the Zone: ' + msg.nextZoneId, 5000, 'primary');
                }
            },
            err => console.error(err),
            () => console.log('complete')
        );

    }
}