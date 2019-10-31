import {Asset} from '../../portofolio/model/asset';

export class OrderInfo {
  selected: Asset;
  selectedCurrency: Asset;

  gainCost: number;
}
