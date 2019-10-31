import {Symbol} from '../../symbol/model/symbol';

export class Asset {

  symbol: Symbol;
  currency: Symbol;

  quantity: number;
  value: number;
  currentValue: number;
  currentRate: number;
  percentChange: number;
  valueChange: number;
}
