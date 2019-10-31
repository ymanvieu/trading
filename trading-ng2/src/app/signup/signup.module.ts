import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from 'app/shared.module';
import { CaptchaModule } from 'primeng/captcha';
import { SignupComponent } from './signup.component';

@NgModule({
  declarations: [
    SignupComponent
  ],
  imports: [
    SharedModule,
    ReactiveFormsModule,
    CaptchaModule,
  ]
})
export class SignupModule {}
