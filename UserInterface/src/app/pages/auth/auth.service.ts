
import { Injectable } from '@angular/core';
import { ActivatedRoute, Router} from '@angular/router';
import { Http, Response, RequestOptions, RequestMethod, Headers } from '@angular/http';
import 'rxjs/add/operator/map';
import jwtDecode from 'jwt-decode';

import { environment } from '../../../environments/environment';

const ROOT_URL: string = environment.userServiceRoot || 'https://obp-05.esl.saic.com:8082';
const LOGIN_API: string = ROOT_URL + '/user/login';
const LOGIN_PAGE: string = '/login';
// const LOGIN_API: string = 'https://localhost:8082/user/login';


@Injectable()
export class AuthService {

  public ROLE_RWCD: string = 'ROLE_RWCD';
  public ROLE_RWO: string = 'ROLE_RWO';
  public ROLE_ADMIN: string = 'ROLE_ADMIN';

  constructor( private http: Http, private router: Router ) {
  }

 authenticate(username:string, password:string) {
    return this.http.post(LOGIN_API, { username: username, password: password })
      .map((response: Response) => {
        // login successful if there's a jwt token in the response
        let user = response.json();
        let token: string = user.data[0].access_token;
        //alert(user.data[0].access_token);
        if (user && token) {
          // store user details and jwt token in local storage to keep user logged in between page refreshes
          sessionStorage.setItem('authToken', token);
          sessionStorage.setItem('currentUser', username);
        }
      });
  }

  logout() {
    // remove user from local storage to log user out
    const hadToken = sessionStorage.getItem('authToken');
    sessionStorage.removeItem('currentUser');
    sessionStorage.removeItem('authToken');

    if (hadToken) {
        window.location.reload(true);
    }
  }

  getToken(): string {
    var token:string = sessionStorage.getItem('authToken');
    if (!token) {
      this.router.navigate([LOGIN_PAGE], { queryParams: { returnUrl: this.router.url } });
    }
    return token;
  }

  enhanceConfig(config?:RequestOptions) : RequestOptions {
    let newHeaders = new Headers();
    if (config) {
      newHeaders = config.headers;
    } else {
        config = new RequestOptions()
    }
    newHeaders.delete('Authorization');
    newHeaders.set('Authorization', `Bearer ${this.getToken()}`);

    config.headers = newHeaders;

     return config;
  }

  decode() : any {
    const token: string = this.getToken();
    if (!token) {
        return {};
    }
    const decoded: any = jwtDecode(token);
    return decoded;
  }

  hasRoleOrAdmin(role: string) : boolean {
    const decoded = this.decode();
    if (decoded.scope) {
      return decoded.scope.includes(role) || decoded.scope.includes('ROLE_ADMIN');
    }

    return false;
  }

}
