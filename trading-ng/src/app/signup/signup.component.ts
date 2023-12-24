import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild } from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { RecaptchaComponent, RecaptchaModule } from 'ng-recaptcha';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { PasswordModule } from 'primeng/password';
import { environment } from 'environments/environment';
import { MustMatch } from 'app/shared/must-match.validator';
import { AuthenticationService } from '../authentication/authentication.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [
    CommonModule,
    TranslateModule,
    ReactiveFormsModule,
    RecaptchaModule,
    ButtonModule,
    MessageModule,
    InputTextModule,
    PasswordModule
  ],
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {

  usernameMinLength = 3;
  usernameMaxLength = 50;
  passwordMinLength = 8;
  passwordMaxLength = 64;

  @ViewChild('recaptcha')
  private recaptchaComponent: RecaptchaComponent;

  recaptchaSiteKey = environment.recaptcha.siteKey;

  form: UntypedFormGroup;

  private recaptchaResponse: string;
  error: string;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private translateService: TranslateService,
    private authenticationService: AuthenticationService,
    private router: Router) {}

  ngOnInit(): void {
    this.form = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(this.usernameMinLength), Validators.maxLength(this.usernameMaxLength)]],
      password: ['', [Validators.required, Validators.minLength(this.passwordMinLength), Validators.maxLength(this.passwordMaxLength)]],
      confirmation: ['', [Validators.required]]
    }, {
      validators: MustMatch('password', 'confirmation')
    });
  }

  get f() { return this.form.controls; }

  showResponse(captchaResponse: any) {
    this.recaptchaResponse = captchaResponse;
  }

  signup() {
    if (this.form.invalid) {
      return;
    }

    this.authenticationService.signup(this.form.value.username, this.form.value.password, this.recaptchaResponse)
    .subscribe(
      () => this.router.navigate(['/portofolio']),
      error => {
        this.error = this.translateService.instant(error.error.message, error.error.args);
        this.recaptchaComponent.reset();
      });
  }
}
