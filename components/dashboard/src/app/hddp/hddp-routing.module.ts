import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HddpPage } from './hddp.page';

const routes: Routes = [
  {
    path: '',
    component: HddpPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HddpPageRoutingModule {}
