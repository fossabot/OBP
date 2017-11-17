import { Component, Input } from '@angular/core';
import {GlobalState} from '../../../global.state';

@Component({
  selector: 'ba-card',
  templateUrl: './baCard.html',
})
export class BaCard {
  @Input() title: String;
  @Input() baCardClass: String;
  @Input() cardType: String;  
  @Input() showFooterMessages: string;
  errorMessage: string = '';
  infoMessage: string = '';

  constructor(private _state: GlobalState) {
    const that = this;
    this.showFooterMessages = 'false';

    this._state.subscribe('menu.activeLink', (activeLink) => {
      if (activeLink) {
        this.errorMessage = this.infoMessage = '';
      }
    });

    this._state.subscribe('message.error', (message) => {
      if (that.showFooterMessages === 'true') {
        that.errorMessage = message;
      }
    });

    this._state.subscribe('message.info', (message) => {
      if (that.showFooterMessages === 'true') {
        that.infoMessage = message;
      }
    });
  }
}
