import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {SharedModule} from 'app/shared.module';
import { BalancedColorDirective } from '../shared/directives/balanced-color.directive';
import {OrderComponent} from './order.component';
import {InputNumberModule} from "primeng/inputnumber";

@NgModule({
  imports: [
    FormsModule,
    InputNumberModule,
    SharedModule,
    BalancedColorDirective,
  ],
  declarations: [
    OrderComponent
  ],
  exports: [
    OrderComponent
  ]
})
export class OrderModule {}
