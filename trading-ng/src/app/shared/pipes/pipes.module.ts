import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BalancedColorPipe } from './balanced-color.pipe';
import { TimeAgoPipe } from './time-ago.pipe';

const pipes = [
  BalancedColorPipe,
  TimeAgoPipe,
];

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    ...pipes
  ],
  exports: [
    ...pipes
  ]
})
export class PipesModule { }
