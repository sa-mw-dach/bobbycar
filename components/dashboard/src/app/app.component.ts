import {Component, OnInit} from '@angular/core';

import {Platform} from '@ionic/angular';
import {SplashScreen} from '@ionic-native/splash-screen/ngx';
import {StatusBar} from '@ionic-native/status-bar/ngx';
import {ConfigService} from './providers/config.service';
import {Observable, of} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {HttpClient} from '@angular/common/http';


@Component({
    selector: 'app-root',
    templateUrl: 'app.component.html',
    styleUrls: ['app.component.scss']
})
export class AppComponent {

    public mapsLoaded: Observable<boolean>;
    public selectedIndex = 0;
    public appPages = [
        {
            title: 'Home',
            url: '/home',
            icon: 'home'
        },
        {
            title: 'Map',
            url: '/map',
            icon: 'map'
        },
        {
            title: 'Driver Recognition - AI/ML',
            url: '/hddp',
            icon: 'speedometer-outline'
        }
    ];

    constructor(
        private platform: Platform,
        private splashScreen: SplashScreen,
        private statusBar: StatusBar,
        private configService: ConfigService,
        private httpClient: HttpClient
    ) {
        this.initializeApp();

        const apiKey = this.configService.GOOGLE_API_KEY || '';

        this.mapsLoaded = httpClient.jsonp('https://maps.googleapis.com/maps/api/js?key=' + apiKey, 'callback')
            .pipe(
                map(() => true),
                catchError(() => of(false)),
            );
    }

    initializeApp() {
        this.platform.ready().then(() => {
            this.statusBar.styleDefault();
            this.splashScreen.hide();
        });
    }

}
