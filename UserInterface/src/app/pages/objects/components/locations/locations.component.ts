import { Component, OnInit, Input, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { HistoryService } from '../reports/history.service';
import { OmsService } from '../shared/oms.service';
import { Util } from '../shared/Util';
import * as moment from 'moment';
import { AuthService } from '../../../auth/auth.service';
import { GlobalState } from '../../../../global.state';


@Component({
  selector: 'oms-locations',
  styleUrls: ['./locations.component.css'],
  templateUrl: './locations.component.html',
  encapsulation: ViewEncapsulation.None,
})
export class LocationsComponent implements OnInit {
  properties: any;
  @Input() response: any;

  id: string;
  clazz: string;
  model: any;
  canDelete: boolean;
  errorMessage: string;
  dateranges: any[];
  selectedTimeRange = 'none';



  constructor(private historyService: HistoryService,
              private omsService: OmsService,
              private route: ActivatedRoute,
              private router: Router,
              private authService: AuthService,
              private state: GlobalState) {
  }

  onChange(selectedValue) {
    console.log(selectedValue);
    this.selectedTimeRange = selectedValue;
    this.getObjects(this.clazz, this.id, selectedValue);
  }

  getObjects(objectType: string, objectId: string, timerange: string): void {
    this.errorMessage = '';
    if (objectType === undefined || objectType === '' || objectId === undefined || objectId === '') {
      this.errorMessage = 'Please enter a search criteria.';
    } else {
      this.historyService.getObjectLocationHistory(objectType,objectId, timerange)
        .subscribe(data => {
            this.response = data;
          },
          error => this.errorMessage = <any>error);
    }
  }

  isValidLocation(loc: any): boolean {
    return loc !== undefined
      && loc !== null
      && loc.lat !== undefined
      && loc.lat !== null
      && loc.lon !== undefined
      && loc.lon !== null;
  }

  ngOnInit() {
    const hasRWCD: boolean = this.authService.hasRoleOrAdmin(this.authService.ROLE_RWCD);
    const hasRWO: boolean = this.authService.hasRoleOrAdmin(this.authService.ROLE_RWO);

    this.dateranges = this.historyService.getDateRanges();
    this.route.params.map(params => params['clazz']).subscribe(clazz => {
      this.clazz = clazz;
      this.route.params.map(params => params['id']).subscribe(id => {
        this.id = id;
        this.getObjects(this.clazz, this.id, this.selectedTimeRange);
      });
    });


  }
}
