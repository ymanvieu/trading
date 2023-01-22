import { NgModule } from '@angular/core';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { ClarityModule } from '@clr/angular';
import { RouterModule } from '@angular/router';
import { NgxPermissionsModule } from 'ngx-permissions';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { PipesModule } from './shared/pipes/pipes.module';

@NgModule({
  imports: [
    ClarityModule,
    RouterModule,
    NgxPermissionsModule,
    BrowserAnimationsModule,
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
    NgxPermissionsModule,
    BrowserAnimationsModule,
    PipesModule,
  ]
})
export class SharedModule { }

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}
