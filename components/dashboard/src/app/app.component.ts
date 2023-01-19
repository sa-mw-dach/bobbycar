import { Component, OnInit } from '@angular/core';

import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';
import { ConfigService } from './providers/config.service';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent {
  
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
      title: 'Driver Monitoring',
      url: '/hddp',
      icon: 'speedometer-outline'
    },
    {
      title: 'Settings',
      url: '/admin',
      icon: 'keypad-outline'
    }
  ];

  constructor(
    private platform: Platform,
    private splashScreen: SplashScreen,
    private statusBar: StatusBar,
    private configService: ConfigService
  ) {
    // Use matchMedia to check the user preference
    if (window.matchMedia('(prefers-color-scheme)').media !== 'not all') {
        console.log('🎉 Dark mode is supported');
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)');
        console.log(prefersDark);
    } else {
        toggleDarkTheme(true);
    }

    //toggleDarkTheme(prefersDark.matches);

    // Listen for changes to the prefers-color-scheme media query
    //prefersDark.addListener((mediaQuery) => toggleDarkTheme(mediaQuery.matches));

    // Add or remove the "dark" class based on if the media query matches
    function toggleDarkTheme(shouldAdd) {
      document.body.classList.toggle('dark', shouldAdd);
    }
    this.initializeApp();
  }

  initializeApp() {
    this.platform.ready().then(() => {
      this.statusBar.styleDefault();
      this.splashScreen.hide();
      this.loadGoogleMaps();
    });
  }

  loadGoogleMaps() {
    /*load google map script dynamically */
    const script = document.createElement('script');
    script.id = 'googleMap';
    if (this.configService.GOOGLE_API_KEY) {
      script.src = 'https://maps.googleapis.com/maps/api/js?key=' + this.configService.GOOGLE_API_KEY;
    } else {
      script.src = 'https://maps.googleapis.com/maps/api/js?key=';
    }
    document.head.appendChild(script);
  }

}
