import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { routing } from './pages.routing';
import { NgaModule } from '../theme/nga.module';
import { AppTranslationModule } from '../app.translation.module';
import { FormGroup } from '@angular/forms';

import { Pages } from './pages.component';

// pages.component.ts uses OmsService to populate navigation with Create menu
import { OmsService } from './objects/components/shared/oms.service';

@NgModule({
  imports: [CommonModule, AppTranslationModule, NgaModule, routing],
  declarations: [Pages],
  providers: [
    // pages.component.ts uses OmsService to populate navigation with Create menu
    OmsService
  ],
})
export class PagesModule {
}
