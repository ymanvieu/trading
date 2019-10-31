import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InjectableRxStompConfig, RxStompService, rxStompServiceFactory } from '@stomp/ng2-stompjs';
import { ProtectedGuard, PublicGuard } from 'ngx-auth';
import { AdminComponent } from './admin/admin.component';
import { AdminModule } from './admin/admin.module';
import { appStompConfig } from './app-stomp.config';
import { AppComponent } from './app.component';
import { AuthenticationModule } from './authentication/authentication.module';
import { ChartRateComponent } from './chart-rate/chart-rate.component';
import { ChartRateModule } from './chart-rate/chart-rate.module';
import { LatestListComponent } from './latest-list/latest-list.component';
import { LatestListModule } from './latest-list/latest-list.module';
import { LoginComponent } from './login/login.component';
import { NavbarModule } from './navbar/navbar.module';
import { PortofolioComponent } from './portofolio/portofolio.component';
import { PortofolioModule } from './portofolio/portofolio.module';
import { SharedModule } from './shared.module';
import { SignupComponent } from './signup/signup.component';
import { SignupModule } from './signup/signup.module';
import { NgxPermissionsModule } from 'ngx-permissions';
import { BrowserModule } from '@angular/platform-browser';
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';

const routes: Routes = [
  {path: '', redirectTo: 'latest', pathMatch: 'full'},
  {path: 'chart/:fromcur/:tocur', component: ChartRateComponent},
  {path: 'latest', component: LatestListComponent},
  {path: 'login', component: LoginComponent, canActivate: [PublicGuard]},
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
      LatestListModule,
      PortofolioModule,
      NavbarModule,
      AuthenticationModule,
      RouterModule.forRoot(routes),
      ChartRateModule,
      AdminModule,
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
       }
   ]
})
export class AppModule {}
