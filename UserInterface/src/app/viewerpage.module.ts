import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { routing } from './viewerpage.routing';
import { NgaModule } from './theme/nga.module';
import { AppTranslationModule } from './app.translation.module';

import { ViewerPageComponent } from './viewerpage.component';


@NgModule({
  imports: [CommonModule, AppTranslationModule, NgaModule, routing, RouterModule],
  declarations: [ViewerPageComponent],
})

export class ViewerPageModule {
  
}
