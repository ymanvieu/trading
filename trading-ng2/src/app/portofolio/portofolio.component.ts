import { Component, OnInit, OnDestroy } from '@angular/core';
import { ClrDatagridSortOrder } from '@clr/angular';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Observable, Subscription } from 'rxjs';
import { first, skip, filter } from 'rxjs/operators';
import { Order } from '../order/model/order';
import { Symbol } from '../symbol/model/symbol';
import { Portofolio } from './model/portofolio';
import { PortofolioService } from './portofolio.service';

@Component({
  selector: 'app-portofolio',
  templateUrl: './portofolio.component.html',
  styleUrls: ['./portofolio.component.css']
})
export class PortofolioComponent implements OnInit, OnDestroy {

  descSort = ClrDatagridSortOrder.DESC;

  portofolio: Portofolio;
  availableSymbols$: Observable<Symbol[]>;

  message: string;

  private latestRatesTopicSubscription: Subscription;

  constructor(
    private portofolioService: PortofolioService,
    private rxStompService: RxStompService) {}

  ngOnInit(): void {
    this.availableSymbols$ = this.portofolioService.getAvailableSymbols();

    this.portofolioService.getPortofolio()
      .pipe(filter(p => !!p))
      .subscribe(p => {
        if (!!this.portofolio) {
          this.portofolio.baseCurrency = p.baseCurrency;
          this.portofolio.currentValue = p.currentValue;
          this.portofolio.percentChange = p.percentChange;
          this.portofolio.valueChange = p.valueChange;

          p.assets.forEach(a => {
            const foundAsset = this.portofolio.assets.find(pa => pa.symbol.code === a.symbol.code && pa.currency.code === a.currency.code);

            if (foundAsset) {
              Object.assign(foundAsset, a);
            }
          });

        } else {
          this.portofolio = p;
        }
      });

    this.latestRatesTopicSubscription = this.rxStompService.watch('/topic/latest/')
      .subscribe(rates => {
        this.portofolioService.refreshPortofolio();
      });
  }

  ngOnDestroy() {
    this.latestRatesTopicSubscription.unsubscribe();
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
