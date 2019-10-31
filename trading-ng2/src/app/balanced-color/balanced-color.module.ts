import { BalancedColorDirective } from './balanced-color.directive';
import { NgModule } from '@angular/core';

import { BalancedColorPipe } from './balanced-color.pipe';
import { CommonModule } from '@angular/common';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    BalancedColorPipe,
    BalancedColorDirective
  ],
  exports: [BalancedColorPipe, BalancedColorDirective]
})
export class BalancedColorModule { }
