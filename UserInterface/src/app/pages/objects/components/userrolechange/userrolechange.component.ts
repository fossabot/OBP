import { Component } from '@angular/core';
import { FormGroup, AbstractControl, FormBuilder, Validators } from '@angular/forms';
import { EmailValidator } from '../../../../theme/validators';
import { UserRoleAdminService } from './userrolechange.service';
import { IAvailableRoles } from './availableroles';
import { AuthService } from '../../../auth/auth.service';
import { GlobalState } from '../../../../global.state';

@Component({
  selector: 'user-role-change',
  templateUrl: './userrolechange.component.html',
  styleUrls: ['./userrolechange.component.scss'],
  providers: [UserRoleAdminService],
})

export class UserRoleRequestAdminComponent {

  availableRolesList: IAvailableRoles[] = [
    { id: 'ROLE_RO', desc: 'Read Only', selected: true },
    { id: 'ROLE_RWO', desc: 'Read/Write', selected: false },
    { id: 'ROLE_RWCD', desc: 'Read/Write/Create/Delete', selected: false },
    { id: 'ROLE_ADMIN', desc: 'OMS Admin', selected: false },
  ];

  form: FormGroup;
  name: AbstractControl;
  // email: AbstractControl;
  // justify: AbstractControl;
  requestedRole: AbstractControl;
  userRolesService: UserRoleAdminService;

  submitted: boolean = false;

  constructor(fb: FormBuilder,
              userRolesService: UserRoleAdminService,
              authService: AuthService,
              private state: GlobalState) {

    this.form = fb.group({
      'name': ['', Validators.compose([Validators.required, Validators.minLength(4), Validators.maxLength(50)])],
      // 'email': ['', Validators.compose([Validators.required, EmailValidator.validate, Validators.maxLength(50)])],
      // 'justify': ['', Validators.compose([Validators.required, Validators.maxLength(100)])],
      'requestedrole': ['ROLE_RO'],
    });
    this.name = this.form.controls['name'];
    // this.email = this.form.controls['email'];
    // this.justify = this.form.controls['justify'];
    this.requestedRole = this.form.controls['requestedrole'];
    this.userRolesService = userRolesService;
  }

  onSubmit(values: Object): void {
    this.submitted = true;
    this.updateUserRole();
  }

  private updateUserRole(): void {
     this.userRolesService.updateUserRole(
      this.form.controls['name'].value, this.requestedRole.value )
       .subscribe(
         result => this.onSuccess(result),
         error =>  this.handleError(error)
       );
  }

  private handleError(error) {
    console.log(error);
    this.state.notifyDataChanged('message.error', 'Could not change user role.');
    // alert('Coult not change user role');
  }

  private onSuccess(result) {
    console.log(result);
    this.state.notifyDataChanged('message.error', '');
    this.state.notifyDataChanged('message.info', '');

    if (result.code === 200) {
      this.state.notifyDataChanged('message.info', result.message);
    } else {
      this.state.notifyDataChanged('message.error', result.message);
    }
  }
}
