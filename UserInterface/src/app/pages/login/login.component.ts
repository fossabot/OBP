import { Component, ViewEncapsulation, OnInit } from '@angular/core';
import { ActivatedRoute, Router} from '@angular/router';
import { Http, Response, RequestOptions, RequestMethod } from '@angular/http';
import { FormGroup, AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../auth/auth.service';
import { Util } from '../objects/components/shared/Util';

import 'rxjs/add/operator/map';
import { environment } from '../../../environments/environment';

const ROOT_URL: string = environment.userServiceRoot || 'https://obp-05.esl.saic.com:8082';

const LOGIN_API: string = `${ROOT_URL}/user/login`;
// const LOGIN_API: string = 'https://localhost:8082/user/login';
const LOGIN_PAGE: string = '/login';

@Component({
  selector: 'login',
  templateUrl: './login.html',
  styleUrls: ['./login.scss'],
})
export class Login implements OnInit {

  form: FormGroup;
  email: AbstractControl;
  password: AbstractControl;
  submitted: boolean = false;
  private route: ActivatedRoute;
  private router: Router;
  private http: Http;
  private returnUrl: string = '/pages/objects/search';
  private authService: AuthService;

  constructor(fb: FormBuilder,
              route: ActivatedRoute,
              router: Router,
              http: Http) {
    this.form = fb.group({
      'email': ['', Validators.compose([Validators.required, Validators.minLength(4), Validators.maxLength(80)])],
      'password': ['', Validators.compose([Validators.required, Validators.minLength(4), Validators.maxLength(20)])],
    });
    this.email = this.form.controls['email'];
    this.password = this.form.controls['password'];
    this.route = route;
    this.router = router;
    this.authService = new AuthService(http, router);
  }

  ngOnInit() {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
    this.authService.logout();
    const that = this;

    $('#inputEmail3').on('input', function() {
      that.clearError();
    });
    $('#inputPassword3').on('input', function() {
      that.clearError();
    });

    this.clearError();
  }

  onSubmit(values: Object): void {
    this.submitted = true;
    if (this.form.valid) {
      const username: string = values['email'];
      const pw: string = values['password'];
      this.signIn(username, pw);
    }
  }

  signIn(username: string, password: string) {
    this.authService.authenticate(username, password)
      .subscribe(
        data => {
          this.router.navigate([this.returnUrl]);
        },
        error => {
          this.showError();
          // this.password.setValue('');
        });
  }

  showError(): void {
    this.email.setErrors(['Invalid Credentials']);
    this.password.setErrors(['Invalid Credentials']);
    $('#error-span').html('Invalid Credentials');
    $('#error-div').show();
  }

  clearError(): void {
    $('#error-div').hide();
    this.email.updateValueAndValidity();
    this.password.updateValueAndValidity();
  }
}
