import { Component, OnInit } from '@angular/core';
import { WSService } from '../providers/ws.service';

@Component({
  selector: 'app-car-detail',
  templateUrl: './car-detail.page.html',
  styleUrls: ['./car-detail.page.scss'],
})
export class CarDetailPage implements OnInit {

  map: google.maps.Map;
  initialPosition = { lat: 50.1146997, lng: 8.6185411 };
  carId = 'c4b88a68-6efb-4070-adf7-a7fef2ef777e';
  marker: google.maps.Marker;
  panorama: google.maps.StreetViewPanorama;
  sv = new google.maps.StreetViewService();

  constructor(private socketService: WSService) {
    this.initializeMap();
    this.socketService.connect();
   }

  initializeMap() {
    setTimeout(() => {

        this.map = new google.maps.Map(document.getElementById('map'), {
            center: this.initialPosition,
            zoom: 15
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

  processSVData(data, status) {

    if (status === google.maps.StreetViewStatus.OK) {
        const calcHeading = google.maps.geometry.spherical.computeHeading(data.location.latLng, data.location.latLng);
        this.panorama.setPov({
            heading: calcHeading,
            pitch: 0
        });
    }

  }

  ionViewWillLeave(){
    console.debug('ionViewWillLeave()');
    this.socketService.close();
  }

  createOrUpdateMarker(data){
    if(data.carid === this.carId){
      this.marker.setPosition(new google.maps.LatLng({ lat: data.lat, lng: data.long }));
      this.map.setCenter({ lat: data.lat, lng: data.long });
      this.panorama.setPosition({ lat: data.lat, lng: data.long });
      this.sv.getPanorama({
        location: { lat: data.lat, lng: data.long },
        radius: 50
    }, this.processSVData);

    }
}

  ngOnInit() {
    this.socketService.getMessages().subscribe(
      msg => {
          this.createOrUpdateMarker(msg);
      }, // Called whenever there is a message from the server.
      err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
      () => console.log('complete') // Called when connection is closed (for whatever reason).
  );
  }

}
