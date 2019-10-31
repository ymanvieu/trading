import {Directive, Input, ElementRef, OnChanges, SimpleChanges} from '@angular/core';

@Directive({
  selector: '[appBalancedColor]'
})
export class BalancedColorDirective implements OnChanges {

  @Input()
  appBalancedColor: number;

  constructor(private elementRef: ElementRef) {
  }

  ngOnChanges(changes: SimpleChanges): void {
    this.changeColor();
  }

  changeColor() {
    this.elementRef.nativeElement.style.color = (this.appBalancedColor > 0 ? 'green' : (this.appBalancedColor < 0 ? 'red' : 'orange'));
  }
}
