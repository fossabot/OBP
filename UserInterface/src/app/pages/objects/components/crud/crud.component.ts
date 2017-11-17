import { Component, AfterViewChecked , OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';


import { OmsService } from '../shared/oms.service';
import { Util } from '../shared/Util';
import * as moment from 'moment';
import { AuthService } from '../../../auth/auth.service';
import { GlobalState } from '../../../../global.state';


@Component({
  selector: 'oms-crud',
  styleUrls: ['./crud.component.css'],
  templateUrl: './crud.component.html',
  encapsulation: ViewEncapsulation.None,
})
export class CrudComponent implements OnInit, AfterViewChecked {
  properties: any;
  hiddenFields: string[];
  disabledFields: string[];
  dateFields: string[];
  clazz: string;
  fields: string[];
  model: any;
  canSave: boolean;
  canDelete: boolean;
  canEdit: boolean;

  private undefinedProtect: any = { location: {} }; // any nestable properties go here, with sensible default

  camelToReg = Util.camelToReg;

  constructor(private omsService: OmsService,
              private route: ActivatedRoute,
              private router: Router,
              private authService: AuthService,
              private state: GlobalState) {
  }

  private onGetAttributes(data: any) {
    this.properties = [];

    let assigned = '';

    console.log('getting attributes ---- ');

    for ( const key in data ) {
      if (data.hasOwnProperty(key)) {
        assigned = data[key] === null ? '0' : '1';
        this.properties.push({
          'name': key, 'value': data[key], 'assigned': assigned,
        });
      }
    }
  }

  private getAdditionalProperties(): any {
    const additionalProperties = {};

    for (const property of this.properties) {
      if (property.value !== null && property.value !== '') {
        additionalProperties[property.name] = property.value;
      }
    }

    return additionalProperties;
  }

  ngOnInit() {
    const hasRWCD: boolean = this.authService.hasRoleOrAdmin(this.authService.ROLE_RWCD);
    const hasRWO: boolean = this.authService.hasRoleOrAdmin(this.authService.ROLE_RWO);

    this.canEdit = hasRWCD || hasRWO;
    this.canDelete = hasRWCD;

    this.properties = [];
    this.hiddenFields = ['otherAttributes'];
    this.dateFields = ['startDate', 'endDate', 'dateOfBirth', 'dateBuilt'];
    this.disabledFields = ['id', 'lastUpdated', 'lastUpdatedBy'];

    this.route.params.map(params => params['clazz']).subscribe(clazz => {
      this.properties = [];
      this.fields = [];
      this.model = jQuery.extend({}, { location: {} });
      this.canSave = !this.model.id && hasRWO || hasRWCD;
      this.clazz = clazz;
      this.model.clazz = this.clazz;
      this.state.notifyDataChanged('title.change', clazz);

      this.route.params.map(params => params['id']).subscribe(id => this.model.id = id);
      if (this.model.id) {
        this.refreshObject();
      }
      this.omsService.getAllProperties().subscribe(o => {
        // this.state.notifyDataChanged('menu.activeLink', { title: clazz });
        // this.state.notifyDataChanged('title.change', clazz);
        this.fields = o;
      });

    });
  }

  private refreshObject() {
    this.omsService.getAttributes(this.model.id, this.clazz).subscribe({
      next: (data) => this.onGetAttributes( data ),
      error: (error) => this.state.notifyDataChanged('message.error',
        'There was an error getting the attributes for this object.'),
    });

    this.omsService.getObject(this.model.id, this.clazz).subscribe(obj => {
      this.model = Object.assign(this.model, obj);
      this.assignReqDefaultValues();
      this.removeClazzField();
    });
  }

  private assignReqDefaultValues() {
    Object.keys(this.undefinedProtect).forEach(field => {
        if (this.model[field] === undefined || this.model[field] === null) {
          this.model[field] = this.undefinedProtect[field];
        }
      },
    );
  }

  private removeClazzField() {
    if (this.fields != undefined) {
      const clazzIndex = this.fields.indexOf('clazz');
      if (clazzIndex > -1) {
        this.fields.splice(clazzIndex, 1);
      }
    }
  }

  private onCreate(data: any) {
    this.router.navigate([`pages/objects/${this.clazz}/${data.id}`]);
  }

  private onCreateComplete() {
    this.state.notifyDataChanged('message.info', 'Object created.');
    // alert('Object created');
  }

  private onUpdate(success: boolean) {
    if (success) {
      this.refreshObject();
    } else {
      this.state.notifyDataChanged('message.error', 'There was an issue updating the object.');
      // alert('there was an issue updating the object.');
    }
  }

  private onUpdateComplete() {
    this.state.notifyDataChanged('message.info', 'Object updated.');
    // alert('Object updated');
  }

  private onDelete(data: any) {
    this.router.navigate([`pages/objects/${this.clazz}/`]);
  }

  private onDeleteComplete() {
    this.state.notifyDataChanged('message.info', 'Object deleted.');
    // alert('Object deleted');
  }

  private onSaveError(error: any) {
    console.log(error);
    this.state.notifyDataChanged('message.error', 'Unable to save changes.');
    // alert('Unable to save changes.');
  }

  private onDeleteError(error: any) {
    console.log(error);
    this.state.notifyDataChanged('message.error', 'Unable to delete object.');
    // alert('Unable to delete object.');
  }

  saveObject() {
    this.model['lastUpdatedBy'] = sessionStorage.getItem('currentUser');
    this.model['lastUpdated'] = null;
    this.model['otherAttributes'] = this.getAdditionalProperties();

    this.state.notifyDataChanged('message.info', '');
    this.state.notifyDataChanged('message.error', '');

    const that = this;

    // Convert the dates to mm/dd/yyyy format
    Object.keys(this.model).forEach(prop => {
      if (this.dateFields.includes(prop)) {
        if (this.model[prop] === undefined || this.model[prop] === null || this.model[prop] === '') {
          this.model[prop] = null;
        } else {
          this.model[prop] = moment(new Date(`${this.model[prop]}T12:00:00Z`)).format('L');
        }
      }
    });

    if (this.model.id === undefined || this.model.id === null) {
      this.omsService.addObject(this.model).subscribe({
        next: (value) => this.onCreate(value),
        error: (error) => this.onSaveError(error),
        complete: () => this.onCreateComplete(),
      });
    } else {
      this.omsService.updateObject(this.model).subscribe({
        next: (value) => this.onUpdate(value),
        error: (error) => this.onSaveError(error),
        complete: () => this.onUpdateComplete(),
      });
    }
  }

  deleteObject() {
    if (confirm( `Are you sure you want to delete ${this.model.name}?` )) {
      this.omsService.deleteObject(this.model).subscribe({
        next: (value) => this.onDelete(value),
        error: (error) => this.onDeleteError(error),
        complete: () => this.onDeleteComplete(),
      });
    }
  }

  displayProperty( property: any ) {
    property.assigned = '1';
    $('dropdown-list').hide();
  }
  ngAfterViewChecked(){
    this.state.notifyDataChanged('title.change', this.clazz);
  }
}
