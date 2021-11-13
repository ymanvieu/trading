import {Asset} from '../portofolio/model/asset';
import {PortofolioService} from '../portofolio/portofolio.service';
import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Symbol} from '../symbol/model/symbol';
import {Order} from './model/order';
import {OrderInfo} from './model/order-info';

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss']
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

  private _selectedSymbol: Symbol;

  private _selectedQuantity = 1;

  error: string;

  opened = false;

  private _sellAll = false;

  @Output()
  orderCompleted: EventEmitter<Order> = new EventEmitter();

  constructor(private portofolioService: PortofolioService) {}

  ngOnInit(): void {
    this.initSelectedSymbol();
  }

  private initSelectedSymbol() {
    if (this.symbol) {
      this._selectedSymbol = this.symbol;
    } else {
      this._selectedSymbol = this.availableSymbols[0];
    }
  }

  open(type: string) {
    this.orderType = type;
    this._selectedQuantity = 1;
    this._sellAll = false;
    this.initSelectedSymbol();
    this.getData();
    this.opened = true;
  }

  close() {
    this.opened = false;
  }

  get selectedSymbol() {
    return this._selectedSymbol;
  }

  set selectedSymbol(val: Symbol) {
    this._selectedSymbol = val;
    this.getData();
  }

  get selectedQuantity() {
    return this._selectedQuantity;
  }

  set selectedQuantity(val: number) {
    this._selectedQuantity = val;
    this.getData();
  }

  get sellAll() {
    return this._sellAll
  }

  set sellAll(val: boolean) {
    this._sellAll = val;
    this.selectedQuantity = this.asset.quantity;
  }

  getData() {
    this.resetError();

    this.portofolioService.getOrderInfo(this._selectedSymbol.code, this._selectedQuantity).subscribe(orderInfo => this.orderInfo = orderInfo);
  }

  order() {
    this.resetError();

    this.portofolioService
      .order(this._selectedSymbol.code, this._selectedQuantity, this.orderType)
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
