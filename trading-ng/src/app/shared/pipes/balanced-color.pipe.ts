import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'balancedColor', standalone: true})
export class BalancedColorPipe implements PipeTransform {

  transform(value: number, args?: any): string {
    return value > 0 ? 'green' : (value < 0 ? 'red' : 'orange');
  }
}
