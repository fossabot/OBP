import { Component } from '@angular/core';
import { Routes } from '@angular/router';

import { BaMenuService } from '../theme';
import { PAGES_MENU, CONFIG_MENU } from './pages.menu';
import { OmsService } from './objects/components/shared/oms.service';
import { GlobalState } from '../global.state';
import { AuthService } from './auth/auth.service';

@Component({
  selector: 'pages',
  templateUrl: `./pages.component.html`,
  styleUrls: ['./pages.component.scss']
})
export class Pages {
  private createMenuTitle: string = 'Create';
  public isMenuCollapsed: boolean = false;

  constructor(private _state: GlobalState,
    private _menuService: BaMenuService,
    private omsService: OmsService,
    private authService: AuthService) {

    this._state.subscribe('menu.isCollapsed', (isCollapsed) => {
      this.isMenuCollapsed = isCollapsed;
    });

  }

  private MenuOptionExists(title: string): boolean {
    let nextTitle = '';

    for ( let key in PAGES_MENU[0].children ) {
      nextTitle = PAGES_MENU[0].children[key].data.menu.title;

      if ( nextTitle === title ) {
        return true;
      }
    }

    return false;
  }

  private AddCreateMenuOption(): void {

    this.omsService.availableObjects().subscribe(data => {
      const isRWCD = this.authService.hasRoleOrAdmin(this.authService.ROLE_RWCD);

      if (isRWCD) {
        const crudMenuOptions = {
          path: 'objects',
          data: {
            menu: {
              title: this.createMenuTitle,
              icon: 'ion-edit',
              selected: false,
              expanded: false,
              order: 10,
            },
          },
          children: [],
        };

        for ( const i in data ) {
          if (data.hasOwnProperty(i)) {
            crudMenuOptions.children.push({
              path: data[i],
              data: {
                menu: {
                  title: data[i].charAt(0).toUpperCase() + data[i].slice(1),
                },
              },
            });
          }
        }

        PAGES_MENU[0].children.push( crudMenuOptions );

        this._menuService.updateMenuByRoutes(<Routes>PAGES_MENU);
      }
    });

    this._menuService.updateMenuByRoutes(<Routes>PAGES_MENU);
  }

  private addConfigurationIfAdmin() {
    if (this.authService.hasRoleOrAdmin(this.authService.ROLE_ADMIN)) {
      PAGES_MENU[0].children.push(CONFIG_MENU);
      this._menuService.updateMenuByRoutes(<Routes>PAGES_MENU);
    }
  }

  ngOnInit() {

    // The "Create" menu option must be created dynamically.
    if ( !this.MenuOptionExists( this.createMenuTitle ) ) {
      this.AddCreateMenuOption();
    }

    this.addConfigurationIfAdmin();

  }
}
