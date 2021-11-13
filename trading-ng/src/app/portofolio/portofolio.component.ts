import { Component, OnInit } from '@angular/core';
import { ClrDatagridSortOrder } from '@clr/angular';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Observable } from 'rxjs';
import { first, skip, filter } from 'rxjs/operators';
import { Order } from '../order/model/order';
import { Symbol } from '../symbol/model/symbol';
import { Portofolio } from './model/portofolio';
import { PortofolioService } from './portofolio.service';
import { RxjsComponent } from 'app/shared/rxjs.component';

@Component({
  selector: 'app-portofolio',
  templateUrl: './portofolio.component.html',
  styleUrls: ['./portofolio.component.scss']
})
export class PortofolioComponent extends RxjsComponent implements OnInit {

  descSort = ClrDatagridSortOrder.DESC;

  portofolio: Portofolio;
  availableSymbols$: Observable<Symbol[]>;

  message: string;

  constructor(
    private portofolioService: PortofolioService,
    private rxStompService: RxStompService) { super(); }

  ngOnInit(): void {
    this.availableSymbols$ = this.portofolioService.getAvailableSymbols();

    this.register(
      this.portofolioService.getPortofolio()
        .pipe(filter(p => !!p))
        .subscribe(p => {
          if (!!this.portofolio) {
            this.portofolio.baseCurrency = p.baseCurrency;
            this.portofolio.currentValue = p.currentValue;
            this.portofolio.percentChange = p.percentChange;
            this.portofolio.valueChange = p.valueChange;

            p.assets.forEach(a => {
              const foundAsset = this.portofolio.assets.find(pa => pa.symbol.code === a.symbol.code
                && pa.currency.code === a.currency.code);

              if (foundAsset) {
                Object.assign(foundAsset, a);
              }
            });

          } else {
            this.portofolio = p;
          }
        })
    );

    this.register(this.rxStompService.watch('/topic/latest/')
      .subscribe(() => {
        this.portofolioService.refreshPortofolio();
      })
    );
  }

  orderCompleted(order: Order) {
    this.message = `${order.quantity} ${order.from.code} (${order.from.name}) => ${order.value} ${order.to.code} (${order.to.name})`;

    this.portofolioService.getPortofolio()
      .pipe(
        skip(1),
        first())
      .subscribe(p => {
        this.portofolio = p;
      });
  }

  resetMessage() {
    this.message = null;
  }
}
