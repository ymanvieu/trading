import { Asset } from '../portofolio/model/asset';
import { PortofolioService } from '../portofolio/portofolio.service';
import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Symbol } from '../symbol/model/symbol';
import { Order } from './model/order';
import { OrderInfo } from './model/order-info';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.css']
})
export class OrderComponent implements OnInit {

  @Input()
  private symbol: Symbol;

  @Input()
  asset: Asset;

  @Input()
  availableSymbols: Symbol[];

  @Input()
  orderType: string;

  orderInfo: OrderInfo;

  private selectedSymbol: Symbol;

  selectQuantity = 1;

  error: string;

  opened = false;

  @Output()
  orderCompleted: EventEmitter<Order> = new EventEmitter();

  constructor(private portofolioService: PortofolioService) {}

  ngOnInit(): void {
    this.initSelectedSymbol();
  }

  private initSelectedSymbol() {
    if (this.symbol) {
      this.selectedSymbol = this.symbol;
    } else {
      this.selectedSymbol = this.availableSymbols[0];
    }
  }

  open(type: string) {
    this.orderType = type;
    this.initSelectedSymbol();
    this.getData();
    this.opened = true;
  }

  close() {
    this.opened = false;
  }

  get selectSymbol() {
    return this.selectedSymbol;
  }

  set selectSymbol(val: Symbol) {
    this.selectedSymbol = val;

    if (val) {
      this.getData();
    }
  }

  getData() {
    this.resetError();

    this.portofolioService.getOrderInfo(this.selectedSymbol.code, this.selectQuantity).subscribe(orderInfo => this.orderInfo = orderInfo);
  }

  order() {
    this.resetError();

    this.portofolioService
      .order(this.selectedSymbol.code, this.selectQuantity, this.orderType)
      .subscribe(order => {
        this.close();
        this.orderCompleted.emit(order);
      }, (error) => {
        console.log(error);
        this.error = error.error.message;
      });
  }

  resetError(): void {
    this.error = null;
  }
}
