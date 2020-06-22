import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'app/shared.module';
import { SpinnerModule } from 'primeng/spinner';
import { OrderComponent } from './order.component';
import { KeyFilterModule } from 'primeng/keyfilter';

@NgModule({
  imports: [
    FormsModule,
    SpinnerModule,
    KeyFilterModule,
    SharedModule
  ],
  declarations: [
    OrderComponent
  ],
  exports: [
    OrderComponent
  ]
})
export class OrderModule {}
