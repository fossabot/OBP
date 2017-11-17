import { Component, ViewEncapsulation, Input, Self, ViewChild, OnInit } from '@angular/core';
import { AutoCompleteModule } from 'primeng/primeng';
import { SearchService } from '../search/search.service';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import * as moment from 'moment';
import { Util } from '../shared/Util';

@Component({
  selector: 'auto-complete',
  templateUrl: './autocomplete.component.html',
  styleUrls: ['./autocomplete.component.css'],
  encapsulation: ViewEncapsulation.None,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: AutoCompleteComponent,
      multi: true,
    },
  ],
})
export class AutoCompleteComponent implements ControlValueAccessor {
  @Input() inputId: string;
  @Input() elementName: string;
  @Input() placeHolderText: string;
  @Input() cssClass: string;
  @Input() type: string;
  @Input() name: string;
  @Input() disabled: boolean;

  suggestions: any;
  _value: string;

  constructor(private searchService: SearchService) {
  }

  ngOnInit() {
    const selector = `#${this.inputId}`;
    $(selector).attr('name', this.name);
    $(selector).attr('ng-reflect-name', this.name);
  }

  @Input() get value() {
    return this._value;
  }

  set value(val) {
    val = val.replace(/\?/g, ' ');
    if (this.type === 'date' && this.isFullDateString(val)) {
        this._value = moment(new Date(val)).format('YYYY-MM-DD');
    } else {
      this._value = val;
    }
    this.onChange(this._value);
    this.onTouched();
  }

  isISODateString(dateString: string): boolean {
    const theArray = dateString.split('-');
    return theArray.length === 3 && theArray[0].length === 4 && theArray[1].length === 2 && theArray[2].length === 2;
  }

  isFullDateString(dateString: string): boolean {
    const reg = /^(0[1-9]|1[012])\/(0[1-9]|[12][0-9]|3[01])\/((19|20)\d\d)$/;
    return reg.test(dateString);
  }

  onChange: any = () => { };
  onTouched: any = () => { };

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  writeValue(value) {
    if (value) {
      this.value = value;
    }
  }

  suggest(): void {
    if (this.type !== 'date' && this._value !== undefined && this._value.trim() !== '') {
        this.searchService.searchSuggestion(this.elementName, this._value.replace(/\//g,' '))
          .subscribe(data => {
              this.suggestions = this.mergeSuggestions(data, this._value.replace(/\//g,' '));
            });
      }
  }

  mergeSuggestions(dataset: any, searchString: string): string[] {
    let suggestions = [];
    const thisFieldName = this.elementName;
    const data = dataset.data[0];

    const complete = Object.keys(data).some(function (fieldName, index, _ary) {
      return data[fieldName].some(function (elem, index1, _ary1) {
        if (elem[thisFieldName] !== undefined
        && elem[thisFieldName] !== null
        && elem[thisFieldName].toLowerCase().indexOf(searchString.toLowerCase()) >= 0) {
          if (!suggestions.includes(elem[thisFieldName].trim())) {
            suggestions = suggestions.concat(elem[thisFieldName].trim());
          }
        }
      });
    });

    return suggestions.sort(function (a, b) {
        return a.toLowerCase().localeCompare(b.toLowerCase());
    }).slice(0, 5);
  }
}
