import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputSwitchModule } from 'primeng/inputswitch';
import { MessageModule } from 'primeng/message';
import { MessagesModule } from 'primeng/messages';
import { Asset } from '../portofolio/model/asset';
import { Order } from '../portofolio/model/order';
import { OrderInfo } from '../portofolio/model/order-info';
import { PortofolioService } from '../portofolio/portofolio.service';
import { BalancedColorDirective } from '../shared/directives/balanced-color.directive';
import { Symbol } from '../symbol/model/symbol';

@Component({
  selector: 'app-order',
  standalone: true,
  imports: [
    FormsModule,
    InputNumberModule,
    CommonModule,
    TranslateModule,
    BalancedColorDirective,
    ButtonModule,
    DialogModule,
    MessagesModule,
    InputSwitchModule,
    DropdownModule,
    MessageModule,
  ],
  templateUrl: './order.component.html',
  styleUrls: ['./order.component.scss']
})
export class OrderComponent {

  @Input()
  asset: Asset;

  @Input()
  availableSymbols: Symbol[];

  @Output()
  orderCompleted: EventEmitter<Order> = new EventEmitter();

  orderType: string;
  orderInfo: OrderInfo;

  private _selectedSymbol: Symbol;
  private _selectedQuantity = 1;
  private _sellAll = false;

  opened = false;
  error: string;

  constructor(private portofolioService: PortofolioService,
              private translateService: TranslateService) {
  }

  private initSelectedSymbol() {
    if (this.asset) {
      this._selectedSymbol = this.asset.symbol;
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
    this.resetMessages();

    this.portofolioService.getOrderInfo(this._selectedSymbol.code, this._selectedQuantity).subscribe(orderInfo => this.orderInfo = orderInfo);
  }

  order() {
    this.resetMessages();

    this.portofolioService
      .order(this._selectedSymbol.code, this._selectedQuantity, this.orderType)
      .subscribe(order => {
        this.close();
        this.orderCompleted.emit(order);
      }, (error) => {
        this.error = this.translateService.instant(error.error.message, error.error.args);
      });
  }

  resetMessages(): void {
    this.error = null;
  }
}
