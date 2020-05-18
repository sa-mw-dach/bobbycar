import { Component, OnInit } from '@angular/core';
import { Platform } from '@ionic/angular';

@Component({
    selector: 'app-map',
    templateUrl: './map.page.html',
    styleUrls: ['./map.page.scss'],
})
export class MapPage implements OnInit {

    map: google.maps.Map;
    marker: google.maps.Marker;
    position = { lat: 50.1146997, lng: 8.6185411 };

    constructor(private platform: Platform) {
        this.initialize();
    }

    initialize() {
        setTimeout(() => {

            this.map = new google.maps.Map(document.getElementById('map'), {
                center: this.position,
                zoom: 16
            });

            this.marker = new google.maps.Marker({
                position: new google.maps.LatLng(this.position),
                title: 'Quarkus Car',
                map: this.map,
                label: 'Quarkus Car',
                draggable: true
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

    updatePosition() {
        this.position = { lat: this.position.lat + 0.0001, lng: this.position.lng + 0.0002 };
        // this.map.setCenter(this.position);
        this.marker.setPosition(this.position);
    }

    async ngOnInit() {
        setInterval(() => {
            this.updatePosition();
            console.log(this.position);
        }, 2000);
    }



}
