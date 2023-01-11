import { Component } from '@angular/core';
import { ConfigService } from '../providers/config.service';

@Component({
  selector: 'app-home',
  templateUrl: 'home.page.html',
  styleUrls: ['home.page.scss'],
})
export class HomePage {

    showArch: boolean = false;
    ocpConsole: string = "https://openshift.com";

    constructor(
        private configService: ConfigService
        ) {
        let index = this.configService.CAR_ENDPOINT.indexOf('.');
        let basedomain = this.configService.CAR_ENDPOINT.substring(index);
        console.log(basedomain);
        this.ocpConsole = "https://console-openshift-console" + basedomain + "/topology/ns/bobbycar?view=graph";
    }

    toggleArchitecture() {
        if(this.showArch) {
            this.showArch = false;
        } else {
            this.showArch = true;
        }
    }

}
