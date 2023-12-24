import { ChangeDetectorRef, OnDestroy, Directive } from '@angular/core';
import { Observable, Subscription } from 'rxjs';

@Directive()
export abstract class RxjsComponent implements OnDestroy {
  subs: Subscription[] = [];

  protected constructor(private cdRef?: ChangeDetectorRef) {}

  register(...sub: Subscription[]) {
    this.subs = this.subs.concat(sub);
  }

  markForCheck(observable: Observable<any>) {
    if (!this.cdRef) {
      throw new Error(`"markForCheck" need change detector reference injected.`);
    }

    this.register(observable
      .subscribe(() => this.cdRef.markForCheck(), () => this.cdRef.markForCheck()));
  }

  ngOnDestroy() {
    this.subs.forEach(sub => sub.unsubscribe());
  }
}
