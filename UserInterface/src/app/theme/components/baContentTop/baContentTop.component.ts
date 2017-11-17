import {Component} from '@angular/core';

import {GlobalState} from '../../../global.state';

@Component({
  selector: 'ba-content-top',
  styleUrls: ['./baContentTop.scss'],
  templateUrl: './baContentTop.html',
})
export class BaContentTop {

  public activePageTitle: string = '';
  public errorMessage: string = '';
  public infoMessage: string = '';

  constructor(private _state: GlobalState) {

    this._state.subscribe('menu.activeLink', (activeLink) => {
      console.log('activeLink', activeLink);
      if (activeLink) {
        this.activePageTitle = activeLink.title;
        this.errorMessage = this.infoMessage = '';
      }
    });

    this._state.subscribe('title.change', (title) => {
      this.activePageTitle = title;
    });

    const that = this;

    this._state.subscribe('message.error', (message) => {
      that.errorMessage = message;
    });

    this._state.subscribe('message.info', (message) => {
      that.infoMessage = message;
    });
  }

}
