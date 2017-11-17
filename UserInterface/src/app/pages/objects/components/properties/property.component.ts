import {  Component, OnInit } from '@angular/core';
import {  Router } from '@angular/router';

import { PropertiesService } from './properties.service';
import { OmsService } from '../shared/oms.service';
import { AuthService } from '../../../auth/auth.service';
import { Util } from '../shared/Util';

import { GlobalState } from '../../../../global.state';

@Component({
  styleUrls: ['./property.component.css'],
  templateUrl: './property.component.html',
})
export class PropertyComponent implements OnInit {
  camelToReg = Util.camelToReg;
  field: string;
  types = ['string', 'date'];
  objectTypes: string[];
  dataType= 'string';
  required= false;
  core= false;
  classes: string[]= [''];
  canSave: boolean;
  canDelete: boolean;


  constructor(private omsService: OmsService,
              private propertiesService: PropertiesService,
              private authService: AuthService,
              private router: Router,
              private state: GlobalState ) {

  }

  saveObject() {
    this.state.notifyDataChanged('message.info', '');
    this.state.notifyDataChanged('message.error', '');


    this.propertiesService.setDictionaryItem( this.field, this.classes, this.dataType, this.core, this.required).subscribe({
      next: (value) => this.onSave(value),
      error: (error) => this.onSaveError(error),
      complete: () => this.onSaveComplete(),
    });
  }

  private onSave(success: boolean) {
    if (!success) {
      this.state.notifyDataChanged('message.error', 'There was an issue updating the properties.');
      // alert('there was an issue updating the properties.');
    }
  }

  private onSaveError(error: any) {
    console.log(error);
    this.state.notifyDataChanged('message.error', 'Unable to update properties.');
    // alert('Unable to update properties.');
  }

  private onSaveComplete() {
    this.state.notifyDataChanged('message.info', 'Properties updated.');
    this.router.navigate(['/pages/objects/properties'] );
    // alert('Properties updated');
  }

  private onGetClassNames(classNames: any) {
    this.objectTypes = classNames;

    // The services are returning the class names all lowercase so let's capitalize them.
    for (let i = 0; i < this.objectTypes.length; i++ ) {
      this.objectTypes[i] = this.camelToReg(this.objectTypes[i]);
    }
  }
  ngOnInit() {
    const hasRWCD: boolean = this.authService.hasRoleOrAdmin(this.authService.ROLE_ADMIN);
    const hasRWO: boolean = this.authService.hasRoleOrAdmin(this.authService.ROLE_ADMIN);

    this.canSave = hasRWCD || hasRWO;
    this.canDelete = hasRWCD;

    this.omsService.availableObjects().subscribe({
      next: (data) => this.onGetClassNames(data),
    });

  }


}
