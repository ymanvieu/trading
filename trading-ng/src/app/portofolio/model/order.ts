import {Symbol} from '../../symbol/model/symbol';

export class Order {
  from: Symbol;
  quantity: number;
  to: Symbol;
  value: number;

  constructor(obj?: Partial<Order>) {
    Object.assign(this, obj);
  }
}
