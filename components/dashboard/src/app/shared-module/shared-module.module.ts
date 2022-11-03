import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { VjsPlayerComponent } from './vjs-player.component';

@NgModule({
 imports:      [ CommonModule ],
 declarations: [ VjsPlayerComponent ],
 exports:      [ VjsPlayerComponent, CommonModule, FormsModule ]
})
export class SharedModule { }
