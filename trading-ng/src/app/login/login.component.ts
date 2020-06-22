import { AuthenticationService } from '../authentication/authentication.service';
import {Component, OnInit} from '@angular/core';
import {FormGroup, FormBuilder, Validators} from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  hasError = false;

  constructor(
    private formBuilder: FormBuilder,
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
