import { NgModule } from '@angular/core';
import { LoginModule } from '../login/login.module';
import { NavbarComponent } from './navbar.component';
import { SharedModule } from 'app/shared.module';

@NgModule({
  declarations: [
    NavbarComponent
  ],
  imports: [
    LoginModule,
    SharedModule
  ],
  exports: [NavbarComponent]
})
export class NavbarModule {}
