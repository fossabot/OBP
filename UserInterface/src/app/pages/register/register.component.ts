import {Component} from '@angular/core';
import {FormGroup, AbstractControl, FormBuilder, Validators} from '@angular/forms';
import {EmailValidator, EqualPasswordsValidator} from '../../theme/validators';
import { Http, Response, RequestOptions, RequestMethod } from '@angular/http';
import { RegisterUserService } from './register.service';
import { Router } from '@angular/router';
import { GlobalState } from '../../global.state';

@Component({
  selector: 'register',
  templateUrl: './register.html',
  styleUrls: ['./register.scss'],
  providers: [RegisterUserService],
})
export class Register {
  public form:FormGroup;
  public name:AbstractControl;
  public email:AbstractControl;
  public password:AbstractControl;
  public repeatPassword:AbstractControl;
  public passwords:FormGroup;
  registerUserService: RegisterUserService;
  parentRouter: Router;

  public submitted:boolean = false;

  constructor(fb: FormBuilder, parentRouter: Router, registerUserService: RegisterUserService, private state: GlobalState) {

    this.form = fb.group({
      'name': ['', Validators.compose([Validators.required, Validators.minLength(4), Validators.maxLength(80)])],
      'email': ['', Validators.compose([Validators.required, EmailValidator.validate, Validators.maxLength(80)])],
      'passwords': fb.group({
        'password': ['', Validators.compose([Validators.required, Validators.minLength(4), Validators.maxLength(20)])],
        'repeatPassword': ['', Validators.compose([Validators.required, Validators.minLength(4), Validators.maxLength(20)])],
      }, { validator: EqualPasswordsValidator.validate('password', 'repeatPassword') } )
    });

    this.name = this.form.controls['name'];
    this.email = this.form.controls['email'];
    this.passwords = <FormGroup> this.form.controls['passwords'];
    this.password = this.passwords.controls['password'];
    this.repeatPassword = this.passwords.controls['repeatPassword'];
    this.registerUserService = registerUserService;
    this.parentRouter = parentRouter;
  }

  public onSubmit(values:Object):void {
    this.submitted = true;
    if (this.form.valid) {
      // your code goes here
      // console.log(values);
      this.registerUser();
    }
  }

  cancelRegistration() {
    this.parentRouter.navigateByUrl('/login');
  }
  private registerUser(): void {
    this.registerUserService.createUser(
      this.name.value, this.password.value )
      .subscribe(
        result => this.onSuccess(result),
        error => this.handleError(error),
      );
  }

  private handleError(error) {
    console.log(error);
    alert('Could not create user.');
  }

  private onSuccess(result) {
    console.log(result);
    if (result.code === 201) {
      alert(result.message);
      this.parentRouter.navigateByUrl('/login');
    } else {
      alert(result.message);
    }
  }
}
