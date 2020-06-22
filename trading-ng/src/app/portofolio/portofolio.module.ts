import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared.module';
import { OrderModule } from '../order/order.module';
import { PortofolioComponent } from './portofolio.component';

@NgModule({
  declarations: [
    PortofolioComponent
  ],
  imports: [
    OrderModule,
    SharedModule
  ],
  exports: []
})
export class PortofolioModule {}
