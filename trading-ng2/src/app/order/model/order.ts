import {Symbol} from '../../symbol/model/symbol';

export class Order {
  from: Symbol;
  quantity: number;
  to: Symbol;
  value: number;
}
