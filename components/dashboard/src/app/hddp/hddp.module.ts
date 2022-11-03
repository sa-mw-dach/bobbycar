import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IonicModule } from '@ionic/angular';
import { HddpPageRoutingModule } from './hddp-routing.module';
import { HddpPage } from './hddp.page';

import { SharedModule } from '../shared-module/shared-module.module';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    SharedModule,
    HddpPageRoutingModule
  ],
  declarations: [HddpPage]
})
export class HddpPageModule {}
