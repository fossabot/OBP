import { AfterViewChecked, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { PropertiesService } from './properties.service';
import { OmsService } from '../shared/oms.service';
import { Util } from '../shared/Util';

import { GlobalState } from '../../../../global.state';

@Component({
  styleUrls: ['./properties.component.css'],
  templateUrl: './properties.component.html',
})
export class PropertiesComponent implements OnInit {
  objectTypes: string[];
  properties: any;
  camelToReg = Util.camelToReg;

  constructor(private omsService: OmsService,
              private propertiesService: PropertiesService,
              private router: Router,
              private state: GlobalState ) {
  }

  private onGet(data: any) {
    console.log('onGet');
    this.properties = data;
  }

  private onGetError(error: string) {
    console.log(error);
  }

  private onGetComplete() {
    console.log('onGetComplete');
  }

  private onGetClassNames(classNames: any) {
    this.objectTypes = classNames;

    // The services are returning the class names all lowercase so let's capitalize them.
    for (let i = 0; i < this.objectTypes.length; i++ ) {
      this.objectTypes[i] = this.camelToReg(this.objectTypes[i]);
    }
  }

  ngOnInit() {
    this.omsService.availableObjects().subscribe({
      next: (data) => this.onGetClassNames(data),
    });

    this.propertiesService.getDictionary().subscribe({
      next: (data) => this.onGet(data),
      error: (error) => this.onGetError(error),
      complete: () => this.onGetComplete(),
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
    // alert('Properties updated');
  }

  private getClassesAssignedToField(fieldName: string): any {
    const classesArray = [];

    for (const className of this.objectTypes) {
      const id = `${className}_${fieldName}`;
      const element = $(`#${id}`).first();
      const checked = element.prop('checked');
      if (checked) {
        classesArray.push(className);
      }
    }

    return classesArray;
  }

  private updateClasses() {
    for (const property of this.properties) {
      const classesArray = this.getClassesAssignedToField(property.field);
      property.classes = classesArray.valueOf();
    }
  }

  saveProperties() {
    this.state.notifyDataChanged('message.info', '');
    this.state.notifyDataChanged('message.error', '');

    this.updateClasses();

    this.propertiesService.setDictionary(this.properties).subscribe({
      next: (value) => this.onSave(value),
      error: (error) => this.onSaveError(error),
      complete: () => this.onSaveComplete(),
    });
  }

  addProperty() {
    this.router.navigate(['/pages/objects/property'] );
  }
}
