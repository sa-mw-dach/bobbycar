import { Component, OnInit } from '@angular/core';

import { Platform } from '@ionic/angular';
import { SplashScreen } from '@ionic-native/splash-screen/ngx';
import { StatusBar } from '@ionic-native/status-bar/ngx';

@Component({
  selector: 'app-root',
  templateUrl: 'app.component.html',
  styleUrls: ['app.component.scss']
})
export class AppComponent implements OnInit {
  public selectedIndex = 0;
  public appPages = [
    {
      title: 'Dashboard',
      url: '/folder/Dashboard',
      icon: 'map'
    },
    {
      title: 'Car Details',
      url: '/folder/Details',
      icon: 'paper-plane'
    },
    {
      title: 'Zone Administration',
      url: '/folder/Admin',
      icon: 'settings'
    },
    {
      title: 'Realtime Search',
      url: '/folder/Search',
      icon: 'search'
    }
  ];
  public labels = ['OpenShift', 'Kafka', 'MQTT', 'Quarkus', 'Datagrid', 'Angular'];

  constructor(
    private platform: Platform,
    private splashScreen: SplashScreen,
    private statusBar: StatusBar
  ) {
    this.initializeApp();
  }

  initializeApp() {
    this.platform.ready().then(() => {
      this.statusBar.styleDefault();
      this.splashScreen.hide();
    });
  }

  ngOnInit() {
    const path = window.location.pathname.split('folder/')[1];
    if (path !== undefined) {
      this.selectedIndex = this.appPages.findIndex(page => page.title.toLowerCase() === path.toLowerCase());
    }
  }
}
