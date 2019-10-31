import {Symbol} from '../../symbol/model/symbol';

export class Rate {
  favorite: boolean;
  fromcur: Symbol;
  tocur: Symbol;
  value: number;
  date: Date;
}
