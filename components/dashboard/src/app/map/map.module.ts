import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';
import { MapPageRoutingModule } from './map-routing.module';

import { MapPage } from './map.page';

import more from 'highcharts/highcharts-more.src';
import exporting from 'highcharts/modules/exporting.src';
import stock from 'highcharts/modules/stock.src';
import solidGauge from 'highcharts/modules/solid-gauge.src';
import { GoogleMapsModule } from '@angular/google-maps';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    MapPageRoutingModule,
    GoogleMapsModule
  ],
  declarations: [MapPage],
  providers: []
})
export class MapPageModule {}
