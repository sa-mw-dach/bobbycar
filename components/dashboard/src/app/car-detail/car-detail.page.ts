import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { WSService } from '../providers/ws.service';
import { CacheService } from '../providers/cache.service';

@Component({
  selector: 'app-car-detail',
  templateUrl: './car-detail.page.html',
  styleUrls: ['./car-detail.page.scss'],
})
export class CarDetailPage implements OnInit {

  map: google.maps.Map;
  initialPosition = { lat: 50.1146997, lng: 8.6185411 };
  carId = '';
  marker: google.maps.Marker;
  panorama: google.maps.StreetViewPanorama;
  sv = new google.maps.StreetViewService();
  streetName = '';
  showHUD = false;

  constructor(private socketService: WSService, private cacheService: CacheService, private route: ActivatedRoute) {}

  initializeMap() {

    setTimeout(() => {

        this.map = new google.maps.Map(document.getElementById('map'), {
            center: this.initialPosition,
            zoom: 13,
            disableDefaultUI: true,
            mapTypeControl: true,
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

  toggleHUD() {
    if(this.showHUD) {
      this.showHUD = false;
    } else {
      this.showHUD = true;
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
    }, (result, status) => {
          console.log(result);
          if (status === google.maps.StreetViewStatus.OK) {
              this.streetName = result.location.description;
              const calcHeading = google.maps.geometry.spherical.computeHeading(result.location.latLng, result.location.latLng);
                this.panorama.setPov({
                  heading: result.tiles.originHeading,
                  pitch: result.tiles.originPitch
              });
          }
     });

    }
}

  ngOnInit() {

    this.carId = this.route.snapshot.paramMap.get('id');
    this.initializeMap();
    this.socketService.connect();

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

    this.socketService.getMessages().subscribe(
      msg => {
          this.createOrUpdateMarker(msg);
      }, // Called whenever there is a message from the server.
      err => console.error(err), // Called if at any point WebSocket API signals some kind of error.
      () => console.log('complete') // Called when connection is closed (for whatever reason).
  );
  }

}
