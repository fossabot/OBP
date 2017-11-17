import {Component} from '@angular/core';
import { ActivatedRoute, Router} from '@angular/router';
import {GlobalState} from '../../../global.state';
import { Http } from "@angular/http";

@Component({
  selector: 'ba-page-top',
  templateUrl: './baPageTop.html',
  styleUrls: ['./baPageTop.scss']
})
export class BaPageTop {

  public isScrolled:boolean = false;
  public isMenuCollapsed:boolean = false;


  constructor(private _state:GlobalState, private http: Http, private router: Router, private route: ActivatedRoute) {
    this._state.subscribe('menu.isCollapsed', (isCollapsed) => {
      this.isMenuCollapsed = isCollapsed;
    });
  }

  public toggleMenu() {
    this.isMenuCollapsed = !this.isMenuCollapsed;
    this._state.notifyDataChanged('menu.isCollapsed', this.isMenuCollapsed);
    return false;
  }

  public scrolledChanged(isScrolled) {
    this.isScrolled = isScrolled;
  }

  public currentPage(): string {
    return this.router.url;
  }

  public quickSearch(searchInput: string): void {
    this.router.navigate(['/pages/objects/search'], {queryParams: {filter: searchInput}});
  }
}
