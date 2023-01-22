import { Component, OnInit, ViewChild } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from '../authentication';
import { Captcha } from 'primeng/captcha';
import { environment } from 'environments/environment';
import { MustMatch } from 'app/shared/must-match.validator';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {

  usernameMinLength = 3;
  usernameMaxLength = 50;
  passwordMinLength = 8;
  passwordMaxLength = 64;

  recaptchaSiteKey = environment.recaptcha.siteKey;

  @ViewChild('recaptcha')
  private recaptchaComponent: Captcha;
  recaptcha: any;

  form: UntypedFormGroup;

  private recaptchaResponse: string;
  error: string;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private authenticationService: AuthenticationService,
    private router: Router) {}

  ngOnInit(): void {
    this.recaptcha = (window as any).grecaptcha;

    this.form = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(this.usernameMinLength), Validators.maxLength(this.usernameMaxLength)]],
      password: ['', [Validators.required, Validators.minLength(this.passwordMinLength), Validators.maxLength(this.passwordMaxLength)]],
      confirmation: ['', [Validators.required]]
    }, {
      validators: MustMatch('password', 'confirmation')
    });
  }

  get f() { return this.form.controls; }

  showResponse(event: any) {
    this.recaptchaResponse = event.response;
  }

  expireRecaptcha() {
    this.recaptchaResponse = null;
  }

  resetRecaptcha() {
    this.recaptchaComponent.reset();
    this.recaptchaResponse = null;
  }

  signup() {
    if (this.form.invalid) {
      return;
    }

    this.authenticationService.signup(this.form.value.username, this.form.value.password, this.recaptchaResponse)
    .subscribe(
      () => this.router.navigate(['/portofolio']),
      response => {
        this.error = response.error.message;
        this.resetRecaptcha();
      });
  }
}
