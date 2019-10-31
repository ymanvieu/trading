import { NgModule } from '@angular/core';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { ClarityModule } from '@clr/angular';
import { RouterModule } from '@angular/router';
import { BalancedColorModule } from './balanced-color';
import { NgxPermissionsModule } from 'ngx-permissions';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { ClrAddonsModule } from '@porscheinformatik/clr-addons';

@NgModule({
  imports: [
    ClarityModule,
    RouterModule,
    BalancedColorModule,
    NgxPermissionsModule,
    BrowserAnimationsModule,
    ClrAddonsModule,
    TranslateModule.forRoot({
      loader: {
          provide: TranslateLoader,
          useFactory: HttpLoaderFactory,
          deps: [HttpClient]
      }
  })
  ],
  declarations: [],
  exports: [
    TranslateModule,
    ClarityModule,
    RouterModule,
    BalancedColorModule,
    NgxPermissionsModule,
    BrowserAnimationsModule
  ]
})
export class SharedModule { }

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}
