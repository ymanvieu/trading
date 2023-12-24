import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TranslateModule } from '@ngx-translate/core';
import { AccordionModule } from 'primeng/accordion';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessagesModule } from 'primeng/messages';
import { PaginatorModule } from 'primeng/paginator';
import { Table, TableModule } from 'primeng/table';
import { Observable } from 'rxjs';
import { first, skip, filter } from 'rxjs/operators';
import { ActionsPairComponent } from '../admin/actions-pair/actions-pair.component';
import { SearchResultToResultPipe } from '../admin/search-result-to-result.pipe';
import { Order } from './model/order';
import { OrderComponent } from '../order/order.component';
import { RxStompService } from '../rx-stomp.service';
import { BalancedColorPipe } from '../shared/pipes/balanced-color.pipe';
import { RxjsComponent } from '../shared/rxjs.component';
import { Symbol } from '../symbol/model/symbol';
import { Portofolio } from './model/portofolio';
import { PortofolioService } from './portofolio.service';

@Component({
  selector: 'app-portofolio',
  standalone: true,
  imports: [
    OrderComponent,
    CommonModule,
    TranslateModule,
    BalancedColorPipe,
    ActionsPairComponent,
    ButtonModule,
    InputTextModule,
    MessagesModule,
    PaginatorModule,
    FormsModule,
    SearchResultToResultPipe,
    TableModule,
    AccordionModule
  ],
  providers: [MessageService],
  templateUrl: './portofolio.component.html',
  styleUrls: ['./portofolio.component.scss']
})
export class PortofolioComponent extends RxjsComponent implements OnInit {

  portofolio: Portofolio;
  availableSymbols$: Observable<Symbol[]>;

  filter: string;

  constructor(
    private portofolioService: PortofolioService,
    private rxStompService: RxStompService,
    private messageService: MessageService) { super(); }

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

  clearTable(table: Table) {
    this.filter = null;
    table.clear();
  }

  orderCompleted(order: Order) {
    this.messageService.add({severity: 'success', detail: `${order.quantity} ${order.from.code} (${order.from.name}) => ${order.value} ${order.to.code} (${order.to.name})`});
    this.portofolioService.refreshPortofolioAndAvailableSymbols();

    this.portofolioService.getPortofolio()
      .pipe(
        skip(1),
        first())
      .subscribe(p => {
        this.portofolio = p;
      });
  }
}
