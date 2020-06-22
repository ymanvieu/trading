import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BalancedColorDirective } from './balanced-color.directive';

const directives = [
  BalancedColorDirective,
];

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    ...directives
  ],
  exports: [
    ...directives
  ]
})
export class DirectivesModule { }
