import {Asset} from './asset';

export class OrderInfo {
  selected: Asset;
  selectedCurrency: Asset;

  gainCost: number;

  constructor(obj?: Partial<OrderInfo>) {
    Object.assign(this, obj);
  }
}
