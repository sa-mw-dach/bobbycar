import { Component, OnInit } from '@angular/core';
import { Platform } from '@ionic/angular';
import { Router } from '@angular/router';
import { ConfigService } from '../providers/config.service';
import { WSService } from '../providers/ws.service';

@Component({
    selector: 'app-map',
    templateUrl: './map.page.html',
    styleUrls: ['./map.page.scss'],
})
export class MapPage implements OnInit {

    map: google.maps.Map;
    marker: google.maps.Marker;
    infowindow: google.maps.InfoWindow;
    initialPosition = { lat: 50.1146997, lng: 8.6185411 };
    bobbycars = new Map();
    zones = [];
    searchArea: google.maps.Circle;

    constructor(
        private platform: Platform,
        private socketService: WSService,
        private router: Router) {

        this.initializeMap();
        this.socketService.connect();
    }

    initializeMap() {
        setTimeout(() => {

            this.map = new google.maps.Map(document.getElementById('map'), {
                center: this.initialPosition,
                zoom: 11
            });

            this.infowindow = new google.maps.InfoWindow({
                content: ''
            });

        }, 10);
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
            const marker = new google.maps.Marker({
                position: new google.maps.LatLng({ lat: data.lat, lng: data.long }),
                title: data.carid,
                map: this.map,
                // label: data.carid,
                draggable: false
            });

            google.maps.event.addListener(marker, 'click', (function(marker, content, infowindow) {
                return function() {
                    infowindow.setContent(
                        `<span style="color: #000000;">
                            <h4>Bobbycar Id:</h4><br/>
                            <p>`+content+`</p>
                            <ion-button href="/car-detail/`+content+`">Car Detail</ion-button>
                        </span>`);
                    infowindow.open(this.map, marker);
                }
            })(marker, data.carid, this.infowindow));

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
        this.addCircleListener(circle);
        this.zones.push(circle);
    }

    addCircleListener(circle: google.maps.Circle) {
        google.maps.event.addListener(circle, 'click', (event) => {
            console.log(event.latLng.toString());
            console.log(circle.getCenter().toString());
            console.log(circle.getRadius());
            console.log(this.getDistanceFromLatLonInKm(event.latLng.lat(), event.latLng.lng(), circle.getCenter().lat(), circle.getCenter().lng()))
          });
    }

    resetMap() {
        console.debug('resetMap()');

        this.bobbycars.forEach(el => {
            el.setMap(null);
        });
        this.bobbycars.clear();

        this.zones.forEach(element => {
            element.setMap(null);
        });
        this.zones = [];
    }

    ionViewWillLeave(){
        console.debug('ionViewWillLeave()');
        this.socketService.close();
    }

    async ngOnInit() {
        this.socketService.getMessages().subscribe(
            msg => {
                this.createOrUpdateMarker(msg);
            }, // Called whenever there is a message from the server.
            err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
            () => console.log('complete') // Called when connection is closed (for whatever reason).
        );

    }


}
