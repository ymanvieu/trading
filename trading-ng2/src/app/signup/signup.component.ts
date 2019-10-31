import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthenticationService } from '../authentication';
import { Captcha } from 'primeng/captcha';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {

  @ViewChild('recaptcha', { static: false })
  private recaptchaComponent: Captcha;
  recaptcha: any;

  signupForm: FormGroup;

  private recaptchaResponse: string;
  error: string;

  constructor(
    private formBuilder: FormBuilder,
    private authenticationService: AuthenticationService,
    private router: Router) {}

  ngOnInit(): void {
    this.recaptcha = (window as any).grecaptcha;

    this.signupForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]],
      confirmation: ['', [Validators.required]]
    });
  }

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
    if (this.signupForm.valid) {
      this.authenticationService.signup(this.signupForm.value.username, this.signupForm.value.password, this.recaptchaResponse)
      .subscribe(
        () => this.router.navigate(['/portofolio']),
        response => {
          this.error = response.error.message;
          this.resetRecaptcha();
        });
    } else {
      this.signupForm.reset();
    }
  }
}
