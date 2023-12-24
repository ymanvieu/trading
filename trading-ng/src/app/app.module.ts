import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { ServiceWorkerModule } from '@angular/service-worker';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { protectedGuard, publicGuard } from 'ngx-auth';
import { NgxPermissionsModule } from 'ngx-permissions';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { MenuModule } from 'primeng/menu';
import { MenubarModule } from 'primeng/menubar';
import { ToastModule } from 'primeng/toast';
import { environment } from '../environments/environment';
import { AdminComponent } from './admin/admin.component';
import { AppComponent } from './app.component';
import { AuthenticationModule } from './authentication/authentication.module';
import { ChartRateComponent } from './chart-rate/chart-rate.component';
import { LanguageService } from './language.service';
import { LatestListComponent } from './latest-list/latest-list.component';
import { LoginComponent } from './login/login.component';
import { LoginGuard } from './login/login.guard';
import { PortofolioComponent } from './portofolio/portofolio.component';
import { rxStompServiceFactory } from './rx-stomp-service-factory';
import { RxStompService } from './rx-stomp.service';
import { SharedModule } from './shared.module';
import { BalancedColorPipe } from './shared/pipes/balanced-color.pipe';
import { SignupComponent } from './signup/signup.component';

const routes: Routes = [
  {path: '', redirectTo: 'latest', pathMatch: 'full'},
  {path: 'chart/:fromcur/:tocur', component: ChartRateComponent},
  {path: 'latest', component: LatestListComponent},
  {path: 'login', component: LoginComponent, canActivate: [publicGuard, LoginGuard]},
  {path: 'portofolio', component: PortofolioComponent, canActivate: [protectedGuard]},
  {path: 'admin', component: AdminComponent, canActivate: [protectedGuard]},
  {path: 'signup', component: SignupComponent, canActivate: [publicGuard]}
];

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    SharedModule,
    HttpClientModule,
    LoginComponent,
    LatestListComponent,
    PortofolioComponent,
    AuthenticationModule,
    RouterModule.forRoot(routes, {}),
    ChartRateComponent,
    AdminComponent,
    SignupComponent,
    NgxPermissionsModule.forRoot(),
    ServiceWorkerModule.register('ngsw-worker.js', {enabled: environment.production}),
    MenubarModule,
    InputTextModule,
    ButtonModule,
    ToastModule,
    MenuModule,
    CardModule,
    BalancedColorPipe,
    TranslateModule.forRoot({
      defaultLanguage: 'en',
      loader: { provide: TranslateLoader, useClass: LanguageService },
    })
  ],
  bootstrap: [
    AppComponent
  ],
  providers: [
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
    },
    LoginGuard
  ]
})
export class AppModule {
}
