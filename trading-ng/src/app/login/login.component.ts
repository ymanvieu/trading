import { CommonModule } from '@angular/common';
import { TranslateModule } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { PasswordModule } from 'primeng/password'
import {Component, OnInit} from '@angular/core';
import { ReactiveFormsModule, UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import {Router} from '@angular/router';
import { AuthenticationService } from '../authentication/authentication.service';
import {AppConstants} from "./app.constants";

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    TranslateModule,
    ButtonModule,
    MessageModule,
    PasswordModule,
    InputTextModule,
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  googleURL = AppConstants.GOOGLE_AUTH_URL;
  githubURL = AppConstants.GITHUB_AUTH_URL;

  loginForm: UntypedFormGroup;

  hasError = false;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private authenticationService: AuthenticationService,
    private router: Router) {}

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required]]
    });
  }


  login() {
    if (this.loginForm.valid) {
      this.authenticationService.login(this.loginForm.value.username, this.loginForm.value.password)
        .subscribe(() => this.router.navigate(['/portofolio']),
        () => this.hasError = true);
    } else {
      this.hasError = false;
      this.loginForm.reset();
    }
  }
}
