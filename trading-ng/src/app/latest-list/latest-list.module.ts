import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared.module';
import { LatestListComponent } from './latest-list.component';

@NgModule({
  declarations: [
    LatestListComponent
  ],
  imports: [
    SharedModule
  ],
  exports: [LatestListComponent]
})
export class LatestListModule {}
