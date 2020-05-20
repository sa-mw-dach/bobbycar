import { Component, OnInit } from '@angular/core';
import { Platform } from '@ionic/angular';
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
    initialPosition = { lat: 50.1146997, lng: 8.6185411 };
    bobbycars = new Map();

    constructor(
        private platform: Platform,
        private socketService: WSService,
        private configService: ConfigService) {

        this.initializeMap();
        this.socketService.connect();
    }

    initializeMap() {
        setTimeout(() => {

            this.map = new google.maps.Map(document.getElementById('map'), {
                center: this.initialPosition,
                zoom: 13
            });

            const cityCircle = new google.maps.Circle({
                strokeColor: '#FF0000',
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillColor: '#FF0000',
                fillOpacity: 0.35,
                map: this.map,
                center: { lat: 50.1115432, lng: 8.6965495 },
                radius: 5000
            });
        }, 10);
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
            this.bobbycars.set(data.carid, marker);
        }

    }

    ionViewWillLeave(){
        console.debug('ionViewWillLeave()');
        this.socketService.close();
    }

    async ngOnInit() {
        /*
        setInterval(() => {
            this.updatePosition();
            console.log(this.position);
        }, 2000);
        */
        this.socketService.getMessages().subscribe(
            msg => {
                console.debug('message received from: ' + msg.carid);
                this.createOrUpdateMarker(msg);
            }, // Called whenever there is a message from the server.
            err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
            () => console.log('complete') // Called when connection is closed (for whatever reason).
        );

    }


}
