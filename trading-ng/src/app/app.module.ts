import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { angleIcon, loginIcon, searchIcon, starIcon, timesIcon, pencilIcon } from '@cds/core/icon';
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from '@stomp/ng2-stompjs';
import { ProtectedGuard, PublicGuard } from 'ngx-auth';
import { AdminComponent } from './admin/admin.component';
import { appStompConfig } from './app-stomp.config';
import { AppComponent } from './app.component';
import { AuthenticationModule } from './authentication/authentication.module';
import { ChartRateComponent } from './chart-rate/chart-rate.component';
import { ChartRateModule } from './chart-rate/chart-rate.module';
import { LatestListComponent } from './latest-list/latest-list.component';
import { LatestListModule } from './latest-list/latest-list.module';
import { LoginComponent } from './login/login.component';
import { PortofolioComponent } from './portofolio/portofolio.component';
import { PortofolioModule } from './portofolio/portofolio.module';
import { SharedModule } from './shared.module';
import { SignupComponent } from './signup/signup.component';
import { SignupModule } from './signup/signup.module';
import { NgxPermissionsModule } from 'ngx-permissions';
import { BrowserModule } from '@angular/platform-browser';
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';
import { LoginModule } from './login/login.module';
import {LoginGuard} from "./login/login.guard";

import { ClarityIcons } from '@cds/core/icon';
import '@cds/core/icon/register.js';

ClarityIcons.addIcons(starIcon, loginIcon, angleIcon, timesIcon, searchIcon, pencilIcon);

const routes: Routes = [
  {path: '', redirectTo: 'latest', pathMatch: 'full'},
  {path: 'chart/:fromcur/:tocur', component: ChartRateComponent},
  {path: 'latest', component: LatestListComponent},
  {path: 'login', component: LoginComponent, canActivate: [PublicGuard, LoginGuard]},
  {path: 'portofolio', component: PortofolioComponent, canActivate: [ProtectedGuard]},
  {path: 'admin', component: AdminComponent, canActivate: [ProtectedGuard]},
  {path: 'signup', component: SignupComponent, canActivate: [PublicGuard]}
];

@NgModule({
   declarations: [
      AppComponent
   ],
   imports: [
      BrowserModule,
      SharedModule,
      HttpClientModule,
      LoginModule,
      LatestListModule,
      PortofolioModule,
      AuthenticationModule,
      RouterModule.forRoot(routes, {}),
      ChartRateModule,
      AdminComponent,
      SignupModule,
      NgxPermissionsModule.forRoot(),
      ServiceWorkerModule.register('ngsw-worker.js', { enabled: environment.production })
   ],
   bootstrap: [
      AppComponent
   ],
   providers: [
      {
         provide: InjectableRxStompConfig,
         useValue: appStompConfig
       },
       {
         provide: RxStompService,
         useFactory: rxStompServiceFactory,
         deps: [InjectableRxStompConfig]
       },
       LoginGuard
   ]
})
export class AppModule {}
