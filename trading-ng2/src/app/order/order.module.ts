import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SharedModule } from 'app/shared.module';
import { SpinnerModule } from 'primeng/spinner';
import { OrderComponent } from './order.component';
import { ClrComboboxModule } from '@porscheinformatik/clr-addons';

@NgModule({
  imports: [
    FormsModule,
    SpinnerModule,
    SharedModule,
    ClrComboboxModule
  ],
  declarations: [
    OrderComponent
  ],
  exports: [
    OrderComponent
  ]
})
export class OrderModule {}
