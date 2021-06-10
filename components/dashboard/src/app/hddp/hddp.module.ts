import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { HddpPageRoutingModule } from './hddp-routing.module';

import { HddpPage } from './hddp.page';
import { VjsPlayerComponent } from './vjs-player.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    HddpPageRoutingModule
  ],
  declarations: [HddpPage, VjsPlayerComponent]
})
export class HddpPageModule {}
