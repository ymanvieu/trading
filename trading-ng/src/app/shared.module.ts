import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { RouterModule } from '@angular/router';
import { NgxPermissionsModule } from 'ngx-permissions';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@NgModule({
  imports: [
    RouterModule,
    NgxPermissionsModule,
    BrowserAnimationsModule,
  ],
  declarations: [],
  exports: [
    TranslateModule,
    RouterModule,
    NgxPermissionsModule,
    BrowserAnimationsModule,
  ]
})
export class SharedModule { }
